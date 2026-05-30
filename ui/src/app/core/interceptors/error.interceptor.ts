import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { MessageService } from 'primeng/api';
import { catchError, throwError } from 'rxjs';

export const errorInterceptorFn: HttpInterceptorFn = (req, next) => {
  const messageService = inject(MessageService);

  return next(req).pipe(
    catchError((error) => {
      const message = error?.error?.message ?? 'An unexpected error occurred';
      messageService.add({ severity: 'error', summary: 'Error', detail: message });
      return throwError(() => error);
    }),
  );
};
