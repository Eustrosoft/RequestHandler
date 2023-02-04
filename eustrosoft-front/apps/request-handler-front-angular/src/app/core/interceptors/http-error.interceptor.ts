import { Injectable } from '@angular/core';
import {
  HttpErrorResponse,
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
} from '@angular/common/http';
import { catchError, EMPTY, Observable, throwError } from 'rxjs';
import { MatSnackBar } from '@angular/material/snack-bar';

@Injectable()
export class HttpErrorInterceptor implements HttpInterceptor {
  constructor(private snackBar: MatSnackBar) {}

  intercept(
    request: HttpRequest<unknown>,
    next: HttpHandler
  ): Observable<HttpEvent<unknown>> {
    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.url?.includes('api/login')) {
          this.snackBar.open(error.error.answer, 'Close');
          return throwError(() => error);
        }
        if (error.url?.includes('api/dispatch')) {
          this.snackBar.open('Error making request', 'Close');
          return throwError(() => error);
        }
        if (error.url?.includes('upload/chunks')) {
          this.snackBar.open('Error making request', 'Close');
          return throwError(() => error);
        }
        return EMPTY;
      })
    );
  }
}
