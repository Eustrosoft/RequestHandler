import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {
  TisQuery,
  TisResponse,
} from '../../requests/interfaces/request.interfaces';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable()
export class ExplorerService {
  constructor(private http: HttpClient) {}

  upload(query: TisQuery): Observable<TisResponse> {
    return this.http.post<TisResponse>(
      `${environment.apiUrl}/api/dispatch`,
      query
    );
  }
}
