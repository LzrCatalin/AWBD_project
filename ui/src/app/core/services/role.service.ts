import { inject, Injectable } from '@angular/core';
import { AuthService } from '@auth0/auth0-angular';
import { toSignal } from '@angular/core/rxjs-interop';
import { map } from 'rxjs';

const ROLES_CLAIM = 'https://airport-manager.com/roles';

@Injectable({ providedIn: 'root' })
export class RoleService {
  private auth = inject(AuthService);

  private roles = toSignal(
    this.auth.user$.pipe(
      map(user => (user?.[ROLES_CLAIM] as string[] | undefined) ?? [])
    ),
    { initialValue: [] as string[] }
  );

  isAdmin()     { return this.roles().includes('ROLE_ADMIN'); }
  isStaff()     { return this.roles().includes('ROLE_STAFF'); }
  isPassenger() { return this.roles().includes('ROLE_PASSENGER'); }

  /** ADMIN sau STAFF — poate face CRUD pe flights, gates, seats */
  canManage()   { return this.isAdmin() || this.isStaff(); }

  /** Doar ADMIN — aircraft, accounts */
  adminOnly()   { return this.isAdmin(); }
}
