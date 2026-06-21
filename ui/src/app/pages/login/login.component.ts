import { Component, inject, OnInit } from '@angular/core';
import { AuthService } from '@auth0/auth0-angular';
import { Router } from '@angular/router';
import { filter, take } from 'rxjs';
import { ButtonModule } from 'primeng/button';

@Component({
  selector: 'app-login',
  imports: [ButtonModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
})
export class LoginComponent implements OnInit {
  private auth = inject(AuthService);
  private router = inject(Router);

  ngOnInit(): void {
    this.auth.isAuthenticated$.pipe(
      filter((isAuthenticated) => isAuthenticated),
      take(1),
    ).subscribe(() => {
      this.router.navigate(['/dashboard']);
    });
  }

  login(): void {
    this.auth.loginWithRedirect({ appState: { target: '/dashboard' } });
  }

  loginWithGoogle(): void {
    this.auth.loginWithRedirect({
      authorizationParams: { connection: 'google-oauth2' },
      appState: { target: '/dashboard' },
    });
  }
}
