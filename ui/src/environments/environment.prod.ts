export const environment = {
  production: true,
  apiUrl: 'https://api.airport-manager.xyz',
  auth0: {
    domain: 'dev-***.eu.auth0.com',
    clientId: '***',
    redirectUri: window.location.origin,
    audience: 'https://airport-manager-api',
  },
};
