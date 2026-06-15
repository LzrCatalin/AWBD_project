export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080',
  auth0: {
    domain: 'dev-***.eu.auth0.com',
    clientId: '***',
    redirectUri: window.location.origin,
    audience: 'https://airport-manager-api',
  },
};
