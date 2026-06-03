import { Component, inject } from '@angular/core';
import { AuthService } from '@auth0/auth0-angular';
import { ButtonModule } from 'primeng/button';

@Component({
  selector: 'app-login',
  imports: [ButtonModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
})
export class LoginComponent {
  private auth = inject(AuthService);

  login(): void {
    this.auth.loginWithRedirect();
  }

  loginWithGoogle(): void {
    this.auth.loginWithRedirect({
      authorizationParams: { connection: 'google-oauth2' },
    });
  }
}
