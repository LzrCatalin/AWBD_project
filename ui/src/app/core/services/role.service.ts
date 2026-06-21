import { inject, Injectable } from '@angular/core';
import { AuthService } from '@auth0/auth0-angular';
import { toSignal } from '@angular/core/rxjs-interop';
import { from, of, switchMap, map, catchError } from 'rxjs';

const ROLES_CLAIM = 'https://airport-manager.com/roles';

@Injectable({ providedIn: 'root' })
export class RoleService {
  private auth = inject(AuthService);

  private roles = toSignal(
    this.auth.isAuthenticated$.pipe(
      switchMap(isAuth => {
        if (!isAuth) return of([] as string[]);
        return from(this.auth.getAccessTokenSilently()).pipe(
          map(token => {
            // JWT payload is base64url-encoded — replace url-safe chars before decoding
            const base64 = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
            const payload = JSON.parse(atob(base64));
            return (payload[ROLES_CLAIM] as string[] | undefined) ?? [];
          }),
          catchError(() => of([] as string[]))
        );
      })
    ),
    { initialValue: [] as string[] }
  );

  isAdmin()     { return this.roles().includes('ADMIN'); }
  isStaff()     { return this.roles().includes('STAFF'); }
  isPassenger() { return this.roles().includes('PASSENGER'); }
  canManage()   { return this.isAdmin() || this.isStaff(); }
  adminOnly()   { return this.isAdmin(); }
}
