import { Injectable } from '@angular/core';
import { TisRequest } from '../../requests/interfaces/request.interfaces';
import { mergeMap, Observable, of } from 'rxjs';

@Injectable()
export class ExplorerRequestBuilderService {
  constructor() {}

  buildChunkRequest(payload: {
    file: File;
    chunks: string[];
  }): Observable<TisRequest> {
    return of(payload).pipe(
      mergeMap((obj: { file: File; chunks: string[] }) => {
        return obj.chunks.map(
          (chunk: string, index: number) =>
            ({
              qtisver: 1,
              requests: [
                {
                  parameters: {
                    data: {
                      file: chunk,
                      name: obj.file.name,
                      ext: obj.file.name.split('.').pop() as string,
                      chunk: index,
                      all_chunks: obj.chunks.length,
                    },
                  },
                  request: 'upload_chunks',
                  subsystem: 'file',
                },
              ],
              qtisend: true,
            } as TisRequest)
        );
      })
    );
  }
}