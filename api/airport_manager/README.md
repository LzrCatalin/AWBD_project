# Airport Manager

REST API pentru managementul unui aeroport. Proiect universitar AWBD — Universitatea din București.

## Tehnologii

- Java 25, Spring Boot 4.0.6
- Spring Data JPA, Hibernate Envers (audit history)
- Spring Security (stateless, JWT — în curs de implementare)
- Liquibase, PostgreSQL 18
- springdoc-openapi (Swagger UI)

## Rulare locală

**1. Pornește baza de date**

```bash
docker compose -f docker/postgres.yml up -d
```

**2. Pornește aplicația**

```bash
./mvnw spring-boot:run
```

**3. Swagger UI**

```
http://localhost:8080/swagger-ui.html
```

## Entități principale

`Account` · `Role` · `Aircraft` · `Gate` · `Flight` · `Seat` · `Booking`

## Structura API

| Resursă     | Path         |
|-------------|--------------|
| Accounts    | `/accounts`  |
| Aircrafts   | `/aircrafts` |
| Flights     | `/flights`   |
| Gates       | `/gates`     |
| Seats       | `/seats`     |
| Bookings    | `/booking`   |

Fiecare resursă suportă operațiile standard CRUD: `GET`, `GET /{id}`, `POST`, `PUT /{id}`, `DELETE /{id}`.
