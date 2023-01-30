import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { TisQuery } from '../../requests/interfaces/request.interfaces';
import { from, Observable } from 'rxjs';

@Injectable()
export class ExplorerRequestBuilderService {
  constructor(private http: HttpClient) {}

  buildChunkRequest(
    payload: { file: File; chunks: Blob[] }[]
  ): Observable<TisQuery> {
    const requests = payload.flatMap((obj: { file: File; chunks: Blob[] }) => {
      return obj.chunks.map(
        (chunk: Blob, index: number) =>
          ({
            qtisver: 1,
            requests: [
              {
                parameters: {
                  data: {
                    file: chunk,
                    name: obj.file.name,
                    ext: obj.file.name.split('.').pop() as string,
                    chunk: index + 1,
                    all_chunks: obj.chunks.length,
                  },
                },
                request: 'upload_chunks',
                subsystem: 'file',
              },
            ],
            qtisend: true,
          } as TisQuery)
      );
    });

    return from(requests);
    // return of({
    //   qtisver: 1,
    //   requests: [
    //     {
    //       parameters: {
    //         data: {
    //           file: [] as Blob[],
    //           name: '',
    //           ext: '',
    //           chunk: 1,
    //           all_chunks: 10,
    //         },
    //       },
    //       request: 'upload_chunks',
    //       subsystem: 'file',
    //     },
    //   ],
    //   qtisend: true,
    // } as TisQuery);
  }
}
