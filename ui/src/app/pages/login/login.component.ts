import { Component, inject, OnInit } from '@angular/core';
import { AuthService } from '@auth0/auth0-angular';
import { Router } from '@angular/router';
import { filter, switchMap, take, from, map, catchError, of } from 'rxjs';
import { RouterLink } from '@angular/router';

const ROLES_CLAIM = 'https://airport-manager.com/roles';

@Component({
  selector: 'app-login',
  imports: [RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
})
export class LoginComponent implements OnInit {
  private auth = inject(AuthService);
  private router = inject(Router);

  ngOnInit(): void {
    this.auth.isAuthenticated$
      .pipe(
        filter((isAuthenticated) => isAuthenticated),
        take(1),
        switchMap(() =>
          from(this.auth.getAccessTokenSilently()).pipe(
            map((token) => this.homeForToken(token)),
            catchError(() => of('/flights')),
          ),
        ),
      )
      .subscribe((home) => this.router.navigate([home]));
  }

  login(): void {
    this.auth.loginWithRedirect();
  }

  loginWithGoogle(): void {
    this.auth.loginWithRedirect({
      authorizationParams: { connection: 'google-oauth2' },
    });
  }

  /** Staff/admin land on the management dashboard; passengers on flight browse. */
  private homeForToken(token: string): string {
    try {
      const base64 = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
      const roles: string[] = JSON.parse(atob(base64))[ROLES_CLAIM] ?? [];
      return roles.includes('ADMIN') || roles.includes('STAFF') ? '/dashboard' : '/flights';
    } catch {
      return '/flights';
    }
  }
}
