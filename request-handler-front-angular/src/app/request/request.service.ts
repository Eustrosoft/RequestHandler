import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { TisQuery } from './interfaces/request.interfaces';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable()
export class RequestService {
  constructor(private http: HttpClient) {}

  dispatch(query: TisQuery): Observable<Object> {
    return this.http.post(`${environment.apiUrl}/api/dispatch`, query);
  }
}
