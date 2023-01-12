import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { AuthenticationService } from '../core/services/authentication.service';
import { environment } from '../../environments/environment';

@Injectable()
export class LoginService {
  constructor(
    private http: HttpClient,
    private authenticationService: AuthenticationService
  ) {}

  login(login: string, password: string): Observable<HttpResponse<Object>> {
    return this.http
      .post(
        `${environment.apiUrl}/api/login`,
        {
          login,
          password,
        },
        { observe: 'response' }
      )
      .pipe(
        tap((res) => (this.authenticationService.isAuthenticated = res.ok))
      );
  }
}
