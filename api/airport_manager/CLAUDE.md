# Airport Manager — CLAUDE.md

Proiect universitar AWBD (Aplicații Web Bazate pe Date), Universitatea din București.
Sistem full-stack de management al unui aeroport.

---

## Backend — Spring Boot

### Stack
- **Java 25**, Spring Boot 4.0.6
- **Spring Data JPA** + Hibernate Envers (audit history în schema `hist`)
- **Spring Security** — stateless, Auth0 JWT Resource Server
- **Auth0** — `dev-fz464d04imnu8jll.eu.auth0.com`, audience `https://airport-manager-api`
- **Liquibase** — migrații în `src/main/resources/liquibase/`
- **PostgreSQL 18** — rulat local via Docker Compose (`docker/postgres.yml`)
- **Lombok** — `@Getter @Setter` pe entități (nu `@Data`), `@RequiredArgsConstructor` pe servicii/controllere
- **springdoc-openapi** — Swagger UI la `/swagger-ui.html`
- **Mapperele** sunt interfețe proprii cu metode `toDto`/`toEntity` (nu MapStruct)

### DataSource config
Datasource-ul folosește proprietăți **standard** Spring Boot (`spring.datasource.*`).
`DataSourceConfig` conține doar bean-ul `JpaProperties` cu namespace custom `spring.jpa.airport-manager`.
**Nu** recrea bean-ul `HikariDataSource` în `DataSourceConfig` — cauzează eroarea `jdbcUrl is required`.

### Structura proiectului

```
src/main/java/com/awbd/airport_manager/
├── config/
│   ├── security/      # Auth0JwtConverter, SecurityConfig, UserInfo, UserInfoAuthenticationToken
│   └── ...            # JpaConfig, CORS, DataSourceConfig (doar JpaProperties), Auditor
├── controller/        # REST controllers (unul per entitate)
├── dto/               # DTO-uri per entitate
├── exception/         # ResourceNotFoundException, ConflictException, GlobalExceptionHandler, ErrorResponse
├── mapper/            # EntityMapper<E,D> + implementări per entitate
├── model/             # Entități JPA
│   └── base/          # VersionedEntity — audit + soft-delete fields
├── repository/        # JpaRepository + JpaSpecificationExecutor per entitate
├── service/
│   ├── api/           # Interfețe service
│   └── impl/          # Implementări service
└── util/
    ├── enums/         # ApiPaths, RoleName
    ├── pagination/    # PagedResponse<T>, PaginationDTO
    ├── search/        # SearchDTO, FilterDTO, SorterDTO, FilterOperation, OperatorEnum
    ├── security/      # SecurityUtils — acces la UserInfo din SecurityContext
    └── spec/          # QuerySpecification<T>, DefaultPredicateBuilder
```

### Entități și relații

| Entitate     | Relații cheie |
|--------------|---------------|
| `Account`    | OneToMany → `AccountRole` |
| `AccountRole`| ManyToOne → `Account`, ManyToOne → `Role` |
| `Role`       | — |
| `Aircraft`   | — |
| `Gate`       | — |
| `Flight`     | ManyToOne → `Aircraft`, ManyToOne → `Gate` |
| `Seat`       | ManyToOne → `Flight` |
| `Booking`    | ManyToOne → `Flight`, ManyToOne → `Account`, OneToOne → `Seat` |

Toate entitățile extind `VersionedEntity`: `startDate`, `endDate`, `isActive`, `createdBy`, `createdDate`, `lastModifiedBy`, `lastModifiedDate`.

### Convenții obligatorii

- **Lombok pe entități**: `@Getter @Setter` separat, niciodată `@Data`
- **Fără cascade / orphanRemoval** pe relații JPA — ștergerile se gestionează manual în servicii
- **Liquibase**: changelog în `resources/liquibase/changelog.xml`, SQL în `resources/liquibase/sql/`
- **ID-uri UUID** generate de DB (`GenerationType.UUID`)
- **Audit history**: schema `hist`, tabele `*_hist`, gestionat de Hibernate Envers

### DTO pattern — nested objects

DTO-urile returnează **atât UUID-ul relației cât și obiectul nested** pentru relațiile ManyToOne/OneToOne:
```java
// SeatDto exemplu
private UUID flightId;   // pentru scriere (create/update)
private FlightDto flight; // pentru citire (răspuns API)
```
Mapperele setează ambele câmpuri în `toDto()`. Frontul folosește obiectul nested pentru afișaj și UUID-ul pentru trimitere în formulare.

### Security — UserInfo & SecurityUtils

`Auth0JwtConverter` produce un `UserInfoAuthenticationToken` (nu `JwtAuthenticationToken`) cu principal de tip `UserInfo`:
```java
UserInfo { sub, email, roles: List<String>, authorities }
```
Pentru a accesa userul curent în controllere/servicii:
```java
SecurityUtils.getCurrentUserInfo()   // aruncă excepție dacă nu e autentificat
SecurityUtils.extractUserInfo()      // returnează null dacă nu e autentificat
SecurityUtils.hasRole("ROLE_ADMIN")
SecurityUtils.hasAnyRole("ROLE_ADMIN", "ROLE_STAFF")
```

### Pattern search/pagination

Toate listele folosesc `POST /resource/search` cu body `SearchDTO`:
```json
{
  "filters": [{ "field": "departureCity", "type": "TRIM_LIKE_IGNORE_CASE", "value": "Paris" }],
  "sorters": [{ "field": "departureTime", "direction": "ASC" }],
  "pagination": { "page": 0, "pageSize": 10 }
}
```
Răspuns: `PagedResponse<T>` cu `items`, `totalItems`, `totalPages`.
`QuerySpecification<T>` este generic, suportă nested paths (`flight.id`).

### Roluri Auth0

Rolurile sunt injectate în JWT printr-un Action post-login sub claim-ul `https://airport-manager.com/roles`.
`Auth0JwtConverter` le mapează la `ROLE_ADMIN`, `ROLE_STAFF`, `ROLE_PASSENGER`.

| Endpoint | Acces |
|---|---|
| `GET /flights, /gates, /seats` | public |
| `POST /flights/search, /gates/search, /seats/search` | public |
| `POST/PUT/DELETE /flights, /gates, /seats` | ADMIN sau STAFF |
| `/aircrafts/**` | ADMIN |
| `/accounts/**` | ADMIN |
| `/bookings/**` | autentificat |

### AccountController — endpoint special

`GET /accounts/me` — returnează `AccountDto` al userului curent, identificat prin emailul din JWT:
```java
String email = SecurityUtils.getCurrentUserInfo().getEmail();
return accountService.getByEmail(email);
```

### Booking — logică server-side

`BookingService.create()` **nu acceptă `accountId` de la client** — îl rezolvă intern din JWT:
```java
String email = SecurityUtils.getCurrentUserInfo().getEmail();
booking.setAccount(accountRepository.findByEmail(email)...);
```
`BookingDto.accountId` nu are `@NotNull` — este populat doar în răspuns, nu citit din request.

`BookingService.search()` **filtrează automat după userul curent** dacă acesta nu e ADMIN/STAFF:
```java
if (!SecurityUtils.hasAnyRole("ROLE_ADMIN", "ROLE_STAFF")) {
    searchDTO.addFilter("account.email", FilterOperation.EQ, email);
}
```
ADMIN și STAFF văd toate rezervările; PASSENGER vede doar ale lui, indiferent ce filtre trimite clientul.

### Seed data (004_seed_data.sql)

4 conturi preinserate cu parola `password` (bcrypt):
| Email | Rol |
|---|---|
| `admin@airport.com` | ADMIN |
| `staff@airport.com` | STAFF |
| `john.doe@example.com` | PASSENGER |
| `maria.pop@example.com` | PASSENGER |

### Baza de date locală

```bash
docker compose -f docker/postgres.yml up -d
mvn spring-boot:run
```
- Host: `localhost:5432`, DB/User/Password: `airport-manager`

---

## Frontend — Angular

### Stack
- **Angular 20**, standalone components
- **PrimeNG 20** + **PrimeFlex** + **PrimeIcons**
- **Tailwind CSS 3** + `tailwindcss-primeui`
- **Auth0 Angular SDK** (`@auth0/auth0-angular`) — login, Google OAuth, token management
- **Zone.js** — importat în `main.ts`

### Auth0 Frontend
- Domain: `dev-fz464d04imnu8jll.eu.auth0.com`
- Client ID: `SCQriHuGoUkZEWr4uhHu7ixSDxxJK4Wt`
- Audience: `https://airport-manager-api`
- SPA trebuie autorizată pe API în Auth0 dashboard (Applications → APIs → Airport Manager API → Machine to Machine Applications)

### Locație
```
/Users/user01/Documents/unibuc/AWBD_project/ui/
```

### Structura proiectului

```
src/app/
├── core/
│   ├── interceptors/     # error.interceptor.ts
│   └── services/         # flight.service.ts, aircraft.service.ts, gate.service.ts,
│                         # seat.service.ts, booking.service.ts, account.service.ts, role.service.ts
├── layout/
│   └── shell/            # shell.component — navbar top + sidebar colapsabil + router-outlet
├── pages/                # o pagină = folder cu .ts + .html + .scss
│   ├── login/            # login cu email/parolă + Google OAuth
│   ├── home/             # pagină simplă welcome (legacy, nefolosită)
│   ├── dashboard/        # dashboard post-login (stat cards + quick actions)
│   ├── flights/          # tabel flights + search + CRUD dialog
│   ├── aircraft/         # tabel aircraft + search + CRUD dialog
│   ├── gates/            # tabel gates + search + CRUD dialog
│   ├── seats/            # tabel seats + search (flight/seatNo) + CRUD dialog
│   ├── bookings/         # tabel bookings + search (flight) + create/cancel dialog
│   └── accounts/         # tabel accounts + search (email/name) + CRUD dialog (ADMIN only)
├── shared/
│   └── models/           # pagination.model.ts, paged-response.model.ts, search.model.ts
│                         # flight.model.ts, aircraft.model.ts, gate.model.ts
│                         # seat.model.ts, booking.model.ts, account.model.ts
├── app.config.ts         # Auth0 + PrimeNG (Aura theme) + HTTP + Animations
├── app.routes.ts         # rute lazy-loaded — shell wraps toate rutele autentificate
├── app.ts
└── app.html
src/environments/
├── environment.ts        # dev — localhost:8080
└── environment.prod.ts
```

### Layout — Shell

Toate rutele autentificate sunt copii ale `ShellComponent` (`layout/shell/`):
- Sidebar stânga (240px, colapsabil la 64px) cu nav items: Dashboard, Flights, Bookings, Aircraft, Gates, Seats, Accounts
- Navbar top (64px) cu buton toggle sidebar + avatar user + logout
- `<router-outlet>` în zona principală

Structura rutelor:
```
/login              → LoginComponent (fără shell)
/                   → ShellComponent (AuthGuard)
  /dashboard        → DashboardComponent
  /flights          → FlightsComponent
  /aircraft         → AircraftComponent
  /gates            → GatesComponent
  /seats            → SeatsComponent
  /bookings         → BookingsComponent
  /accounts         → AccountsComponent
** → redirect /login
```

### Pagini existente

| Rută | Componentă | Guard | Descriere |
|---|---|---|---|
| `/login` | `LoginComponent` | — | Login email/parolă + Google |
| `/dashboard` | `DashboardComponent` | AuthGuard (shell) | Role-based: PASSENGER (Available Flights + My Bookings); ADMIN/STAFF (Flights + Bookings + Aircraft + Gates) + quick actions per rol |
| `/flights` | `FlightsComponent` | AuthGuard (shell) | Tabel lazy + search (flightNo/from/to) + CRUD |
| `/aircraft` | `AircraftComponent` | AuthGuard (shell) | Tabel lazy + search (model) + CRUD |
| `/gates` | `GatesComponent` | AuthGuard (shell) | Tabel lazy + search (gateNo/terminal) + CRUD |
| `/seats` | `SeatsComponent` | AuthGuard (shell) | Tabel lazy + search (flight/seatNo) + CRUD; afișaj flight via `seat.flight` nested |
| `/bookings` | `BookingsComponent` | AuthGuard (shell) | Tabel lazy + filter flight; create booking (selectează flight→available seats); cancel |
| `/accounts` | `AccountsComponent` | AuthGuard (shell) | Tabel lazy + search (email/firstName/lastName) + CRUD; vizibil doar ADMIN |

### Services Angular

Toate în `core/services/`, pattern uniform:
- `search(query: SearchDTO): Observable<PagedResponse<T>>` → `POST /api/{resource}/search`
- `getById(id)`, `create(payload)`, `update(id, payload)`, `delete(id)`
- `authHttpInterceptorFn` adaugă automat JWT Bearer token la toate request-urile către `apiUrl/*`

**`AccountService`** are și `getMe(): Observable<Account>` → `GET /api/accounts/me`.
**Nu apela `getMe()` din componente pentru a obține `accountId`** — booking-ul este rezolvat server-side din JWT.

**`RoleService`** (`core/services/role.service.ts`) — citește rolurile din **access token** (nu din ID token / `user$`):
```typescript
// Auth0 Actions setează claim-ul pe access token, nu pe ID token
// => auth.user$ nu are rolurile; trebuie decodat access token-ul
from(this.auth.getAccessTokenSilently()).pipe(
  map(token => {
    const base64 = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
    return JSON.parse(atob(base64))['https://airport-manager.com/roles'] ?? [];
  })
)
```
Expune: `isAdmin()`, `isStaff()`, `isPassenger()`, `canManage()` (ADMIN|STAFF), `adminOnly()` (ADMIN).
Folosit în template-uri: `@if (roles.canManage()) { ... }`, `@if (roles.adminOnly()) { ... }`.

### Pattern pagini cu tabel

Fiecare pagină de entitate urmează același pattern:
1. **Search bar** cu câmpuri separate per coloană filtrabilă (filtrele sunt AND-uite în backend)
2. **`p-table`** cu `[lazy]="true"` — paginare + sortare server-side via `(onLazyLoad)`
3. **`p-dialog`** pentru create/edit cu form simplu
4. **`p-confirmdialog`** pentru delete
5. **`p-toast`** pentru feedback succes/eroare

### DTO pattern — nested objects în frontend

Modelele Angular reflectă același pattern ca backend-ul — UUID pentru scriere, obiect nested pentru citire:
```typescript
interface Seat {
  id: string;
  seatNo: string;
  flightId: string;      // trimis la create/update
  flight: Flight | null; // primit din API, folosit pentru afișaj
}
interface Booking {
  id: string;
  flightId: string; seatId: string; accountId: string; // pentru răspuns
  flight: Flight | null; seat: Seat | null; account: Account | null;
}
interface BookingForm {
  flightId: string;
  seatId: string;
  // accountId lipsește intenționat — backend-ul îl rezolvă din JWT
}
```
**Nu folosi lookup maps** (ex. `flightMap`, `seatMap`) — folosește direct câmpul nested `.flight`, `.seat` etc.

### Convenții Angular
- Paginile merg în `src/app/pages/` — câte un folder per pagină cu `.ts` + `.html` + `.scss`
- Nu crea componente fără să fie discutate mai întâi
- Modele în `shared/models/` — `PagedResponse<T>`, `SearchDTO`, `FilterDTO`, `SorterDTO`, `Pagination`
- Filtrele din SearchDTO sunt **AND-uite** în `QuerySpecification` — câmp separat per filtru, nu un singur search global
- Post-login redirect: `/dashboard`

### Auth0 Angular
- `authHttpInterceptorFn` adaugă automat JWT la toate request-urile către `apiUrl/*`
- Login: `auth.loginWithRedirect({ appState: { target: '/dashboard' } })`
- Login Google: `auth.loginWithRedirect({ authorizationParams: { connection: 'google-oauth2' }, appState: { target: '/dashboard' } })`
- `AuthGuard` din `@auth0/auth0-angular` protejează rutele private

### Run frontend
```bash
cd ui && ng serve
```
Disponibil la `http://localhost:4200`

---

## TODO rămas

### Backend
- **Booking business logic**: marcare `isAvailable=false` pe `Seat` la rezervare, eliberare la anulare (cancel)
- **Teste**: unit tests pe servicii, integration tests pe controllere

### Deploy
- Kubernetes + Cloudflare (mai târziu)
