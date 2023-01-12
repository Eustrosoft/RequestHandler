import { Injectable } from '@angular/core';
import { FileQuery, SqlQuery, TisQuery } from './interfaces/request.interfaces';
import { combineLatest, mergeMap, Observable, of, ReplaySubject } from 'rxjs';
import { QueryTypes } from './constants/enums/query-types.enum';
import { FormGroup } from '@angular/forms';
import { RequestForm } from './types/request.types';

@Injectable()
export class RequestBuilderService {
  constructor() {}

  buildQuery(
    queryType: QueryTypes,
    form: FormGroup<RequestForm>
  ): Observable<TisQuery> {
    switch (queryType) {
      case QueryTypes.FILE:
        return this.buildFileQuery(form.value.file as File);
      case QueryTypes.SQL:
        return this.buildSqlQuery(form.value.request as string);
      default:
        throw new Error('Query type unspecified');
    }
  }

  private buildSqlQuery(query: string): Observable<TisQuery> {
    return of({
      qtisver: 1,
      requests: [
        {
          parameters: { method: 'plain/text', query: query },
          request: 'sql',
          subsystem: 'sql',
        },
      ],
      qtisend: true,
    });
  }

  private buildFileQuery(file: File): Observable<TisQuery> {
    return combineLatest([this.fileToBase64(file)]).pipe(
      mergeMap(([base64]) => {
        return of({
          qtisver: 1,
          requests: [
            {
              parameters: {
                data: {
                  file: base64,
                  name: file.name,
                  ext: file.name.split('.').pop(),
                },
                method: 'application/octet-stream',
              },
              request: 'upload',
              subsystem: 'file',
            },
          ],
          qtisend: true,
        } as TisQuery);
      })
    );
  }

  private fileToBase64(file: File): Observable<string> {
    const result = new ReplaySubject<string>(1);
    const reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = (event) =>
      result.next(event.target!.result!.toString().replace(/^.*,/, ''));
    return result;
  }
}
