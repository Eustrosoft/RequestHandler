import { Injectable } from '@angular/core';
import { TisQuery } from '../../requests/interfaces/request.interfaces';
import { from, map, mergeMap, Observable, of } from 'rxjs';

@Injectable()
export class ExplorerRequestBuilderService {
  constructor() {}

  buildChunkRequest(
    payload: { file: File; chunks: string[] }[]
  ): Observable<TisQuery> {
    return of(payload).pipe(
      map((pl: { file: File; chunks: string[] }[]) => {
        return pl.flatMap((obj: { file: File; chunks: string[] }) => {
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
              } as TisQuery)
          );
        });
      }),
      mergeMap((v: TisQuery[]) => from(v))
    );
  }
}
