import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { RoleService } from '../services/role.service';

/**
 * Restricts management screens (dashboard, aircraft, gates, seats, accounts) to
 * STAFF/ADMIN. Passengers are redirected to the flight browse page.
 */
export const manageGuard: CanActivateFn = () => {
  const roles = inject(RoleService);
  const router = inject(Router);
  return roles.canManage() ? true : router.createUrlTree(['/flights']);
};

/** ADMIN-only screens (accounts). */
export const adminGuard: CanActivateFn = () => {
  const roles = inject(RoleService);
  const router = inject(Router);
  return roles.adminOnly() ? true : router.createUrlTree(['/flights']);
};
