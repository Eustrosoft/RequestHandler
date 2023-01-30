import { Injectable } from '@angular/core';
import {
  FileQuery,
  SqlQuery,
  TisQuery,
} from '../interfaces/request.interfaces';
import { combineLatest, mergeMap, Observable, of } from 'rxjs';
import { QueryTypes } from '../constants/enums/query-types.enum';
import { FormArray, FormGroup } from '@angular/forms';
import { SingleRequestForm } from '../types/request.types';
import { FileBase64Service } from '../../core/services/file-base64.service';

@Injectable()
export class RequestBuilderService {
  constructor(private fileBase64Service: FileBase64Service) {}

  buildQuery(
    forms: FormArray<FormGroup<SingleRequestForm>>
  ): Observable<TisQuery> {
    const requests = forms.controls.map(
      (control: FormGroup<SingleRequestForm>) => {
        switch (control.value.queryType as QueryTypes) {
          case QueryTypes.FILE:
            return this.buildFileQuery(control.value.file!.pop() as File);
          case QueryTypes.SQL:
            return this.buildSqlQuery(control.value.request as string);
        }
      }
    );

    return combineLatest(requests).pipe(
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
    return this.fileBase64Service.fileToBase64(file).pipe(
      mergeMap((base64: string) =>
        of({
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
        })
      )
    );
  }
}
