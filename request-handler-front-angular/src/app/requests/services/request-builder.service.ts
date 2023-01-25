import { Injectable } from '@angular/core';
import {
  FileQuery,
  SqlQuery,
  TisQuery,
} from '../interfaces/request.interfaces';
import {
  combineLatest,
  forkJoin,
  mergeMap,
  Observable,
  of,
  ReplaySubject,
} from 'rxjs';
import { QueryTypes } from '../constants/enums/query-types.enum';
import { FormArray, FormGroup } from '@angular/forms';
import { SingleRequestForm } from '../types/request.types';

@Injectable()
export class RequestBuilderService {
  constructor() {}

  buildQuery(
    forms: FormArray<FormGroup<SingleRequestForm>>
  ): Observable<TisQuery> {
    const requests = forms.controls.map(
      (control: FormGroup<SingleRequestForm>) => {
        switch (control.value.queryType as QueryTypes) {
          case QueryTypes.FILE:
            return this.buildFileQuery(control.value.file as File);
          case QueryTypes.SQL:
            return this.buildSqlQuery(control.value.request as string);
        }
      }
    );

    return forkJoin(requests).pipe(
      mergeMap((value: (FileQuery | SqlQuery)[]) =>
        of({
          qtisver: 1,
          requests: value,
          qtisend: true,
        } as TisQuery)
      )
    );
  }

  private buildSqlQuery(query: string): Observable<SqlQuery> {
    return of({
      parameters: { method: 'plain/text', query: query },
      request: 'sql',
      subsystem: 'sql',
    });
  }

  private buildFileQuery(file: File): Observable<FileQuery> {
    return combineLatest([this.fileToBase64(file)]).pipe(
      mergeMap(([base64]) => {
        return of({
          parameters: {
            data: {
              file: base64,
              name: file.name,
              ext: file.name.split('.').pop() as string,
            },
            method: 'application/octet-stream',
          },
          request: 'upload',
          subsystem: 'file',
        });
      })
    );
  }

  private fileToBase64(file: File): Observable<string> {
    const result = new ReplaySubject<string>(1);
    const reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = (event) =>
      result.next(event.target!.result!.toString().replace(/^.*,/, ''));
    return result.asObservable();
  }
}
