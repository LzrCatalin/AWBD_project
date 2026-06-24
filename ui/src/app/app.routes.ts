import { Routes } from '@angular/router';
import { AuthGuard } from '@auth0/auth0-angular';
import { manageGuard, adminGuard } from './core/guards/manage.guard';

export const routes: Routes = [
  // Public landing page (no auth)
  {
    path: '',
    pathMatch: 'full',
    loadComponent: () =>
      import('./pages/landing/landing.component').then((m) => m.LandingComponent),
  },
  {
    path: 'login',
    loadComponent: () =>
      import('./pages/login/login.component').then((m) => m.LoginComponent),
  },
  // Authenticated app — chrome (sidebar vs top-nav) chosen by role inside the shell
  {
    path: '',
    loadComponent: () =>
      import('./layout/shell/shell.component').then((m) => m.ShellComponent),
    canActivate: [AuthGuard],
    children: [
      {
        path: 'dashboard',
        canActivate: [manageGuard],
        loadComponent: () =>
          import('./pages/dashboard/dashboard.component').then((m) => m.DashboardComponent),
      },
      {
        path: 'flights',
        loadComponent: () =>
          import('./pages/flights/flights.component').then((m) => m.FlightsComponent),
      },
      {
        path: 'bookings',
        loadComponent: () =>
          import('./pages/bookings/bookings.component').then((m) => m.BookingsComponent),
      },
      {
        path: 'checkout/:flightId',
        loadComponent: () =>
          import('./pages/checkout/checkout.component').then((m) => m.CheckoutComponent),
      },
      {
        path: 'profile',
        loadComponent: () =>
          import('./pages/profile/profile.component').then((m) => m.ProfileComponent),
      },
      {
        path: 'aircraft',
        canActivate: [manageGuard],
        loadComponent: () =>
          import('./pages/aircraft/aircraft.component').then((m) => m.AircraftComponent),
      },
      {
        path: 'gates',
        canActivate: [manageGuard],
        loadComponent: () =>
          import('./pages/gates/gates.component').then((m) => m.GatesComponent),
      },
      {
        path: 'seats',
        canActivate: [manageGuard],
        loadComponent: () =>
          import('./pages/seats/seats.component').then((m) => m.SeatsComponent),
      },
      {
        path: 'accounts',
        canActivate: [adminGuard],
        loadComponent: () =>
          import('./pages/accounts/accounts.component').then((m) => m.AccountsComponent),
      },
    ],
  },
  {
    path: '**',
    redirectTo: '',
  },
];
