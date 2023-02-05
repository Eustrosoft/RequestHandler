import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import {
  ChunkedFileRequest,
  TisRequest,
  TisResponse,
  TisResponseBody,
} from '../../requests/interfaces/request.interfaces';
import { delay, mergeMap, Observable, of } from 'rxjs';
import { environment } from '../../../environments/environment';
import { FileSystemObject } from '../interfaces/file-system-object.interface';
import { FileSystemObjectTypes } from '../constants/enums/file-system-object-types.enum';

@Injectable()
export class ExplorerService {
  constructor(private http: HttpClient) {}

  getFsObjects(): Observable<FileSystemObject[]> {
    return of([
      {
        id: '1',
        title: 'folder 1',
        type: FileSystemObjectTypes.FOLDER,
        child: [
          {
            id: '11',
            title: 'folder 11',
            type: FileSystemObjectTypes.FOLDER,
            child: [],
            info: {
              created: '11',
              modified: '11',
              owner: '11',
            },
          },
        ],
        info: {
          created: '1',
          modified: '1',
          owner: '1',
        },
      },
      {
        id: '2',
        title: 'folder 2',
        type: FileSystemObjectTypes.FOLDER,
        child: [],
        info: {
          created: '2',
          modified: '2',
          owner: '2',
        },
      },
      {
        id: '3',
        title: 'file 3',
        type: FileSystemObjectTypes.FILE,
        child: [],
        info: {
          created: '3',
          modified: '3',
          owner: '3',
        },
      },
    ]);
    // return this.http.get<FileSystemObject[]>(
    //   `${environment.apiUrl}/api/folders`
    // );
  }

  getFsObject(id: string): Observable<any> {
    return this.http.get(`${environment.apiUrl}/api/folders/${id}`);
  }

  createFsObject(obj: any): Observable<any> {
    return this.http.post(`${environment.apiUrl}/api/folders`, obj);
  }

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

  uploadChunks(
    body: FormData,
    headers: { [p: string]: string | string[] }
  ): Observable<any> {
    return of({
      title: 'Simulating HTTP Requests',
      content: 'This is off the hook!!',
    }).pipe(delay(500));
    return this.http.post<any>(`${environment.apiUrl}/api/dispatch`, body, {
      headers: new HttpHeaders(headers),
      observe: 'response',
    });
  }
}
