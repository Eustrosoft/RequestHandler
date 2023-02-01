import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {
  ChunkedFileRequest,
  TisRequest,
  TisResponse,
  TisResponseBody,
} from '../../requests/interfaces/request.interfaces';
import { mergeMap, Observable, of } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable()
export class ExplorerService {
  constructor(private http: HttpClient) {}

  upload(query: TisRequest): Observable<{
    request: TisRequest;
    response: TisResponse;
    totalChunks: number;
    currentChunk: number;
  }> {
    return this.http
      .post<TisResponse>(`${environment.apiUrl}/api/dispatch`, query)
      .pipe(
        mergeMap((response: TisResponse) => {
          const req = query.requests[0] as ChunkedFileRequest;
          const res = response.responses[0] as TisResponseBody;
          const totalChunks = req.parameters.data.all_chunks;
          const currentChunk = req.parameters.data.chunk;
          return of({
            request: query,
            response: response,
            totalChunks,
            currentChunk,
          });
        })
      );
  }
}
