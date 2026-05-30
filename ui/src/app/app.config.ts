import {
  ApplicationConfig,
  provideBrowserGlobalErrorListeners,
  provideZoneChangeDetection,
} from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideAnimations } from '@angular/platform-browser/animations';
import { authHttpInterceptorFn, provideAuth0 } from '@auth0/auth0-angular';
import { providePrimeNG } from 'primeng/config';
import Aura from '@primeuix/themes/aura';
import { MessageService, ConfirmationService } from 'primeng/api';

import { routes } from './app.routes';
import { environment } from '../environments/environment';
import { errorInterceptorFn } from './core/interceptors/error.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideAnimations(),
    provideHttpClient(
      withInterceptors([errorInterceptorFn, authHttpInterceptorFn]),
    ),
    providePrimeNG({
      theme: { preset: Aura, options: { darkModeSelector: '.dark' } },
    }),
    MessageService,
    ConfirmationService,
    provideAuth0({
      domain: environment.auth0.domain,
      clientId: environment.auth0.clientId,
      authorizationParams: {
        redirect_uri: environment.auth0.redirectUri,
        audience: environment.auth0.audience,
      },
      cacheLocation: 'localstorage',
      httpInterceptor: {
        allowedList: [`${environment.apiUrl}/*`],
      },
    }),
  ],
};
