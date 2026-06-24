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
import { definePreset } from '@primeuix/themes';
import Aura from '@primeuix/themes/aura';
import { MessageService, ConfirmationService } from 'primeng/api';

// Align PrimeNG's primary palette with the "Book of Flights" blue accent (#3159d4).
const AppPreset = definePreset(Aura, {
  semantic: {
    primary: {
      50: '#eef3fd',
      100: '#d6e1f8',
      200: '#aec2f0',
      300: '#85a3e8',
      400: '#5c84e0',
      500: '#3159d4',
      600: '#2b4fbe',
      700: '#233f97',
      800: '#1c3273',
      900: '#15264f',
      950: '#0d1730',
    },
  },
});

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
      theme: { preset: AppPreset, options: { darkModeSelector: '.dark' } },
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
