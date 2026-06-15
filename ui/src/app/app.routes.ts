import { Routes } from '@angular/router';
import { AuthGuard } from '@auth0/auth0-angular';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () =>
      import('./pages/login/login.component').then((m) => m.LoginComponent),
  },
  {
    path: '',
    loadComponent: () =>
      import('./layout/shell/shell.component').then((m) => m.ShellComponent),
    canActivate: [AuthGuard],
    children: [
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./pages/dashboard/dashboard.component').then((m) => m.DashboardComponent),
      },
      {
        path: 'flights',
        loadComponent: () =>
          import('./pages/flights/flights.component').then((m) => m.FlightsComponent),
      },
      {
        path: 'aircraft',
        loadComponent: () =>
          import('./pages/aircraft/aircraft.component').then((m) => m.AircraftComponent),
      },
      {
        path: 'gates',
        loadComponent: () =>
          import('./pages/gates/gates.component').then((m) => m.GatesComponent),
      },
      {
        path: 'seats',
        loadComponent: () =>
          import('./pages/seats/seats.component').then((m) => m.SeatsComponent),
      },
      {
        path: 'bookings',
        loadComponent: () =>
          import('./pages/bookings/bookings.component').then((m) => m.BookingsComponent),
      },
      {
        path: 'accounts',
        loadComponent: () =>
          import('./pages/accounts/accounts.component').then((m) => m.AccountsComponent),
      },
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full',
      },
    ],
  },
  {
    path: '**',
    redirectTo: 'login',
  },
];
