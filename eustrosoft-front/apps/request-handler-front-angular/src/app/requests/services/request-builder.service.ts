import { Injectable } from '@angular/core';
import {
  FileRequest,
  SqlRequest,
  TisRequest,
} from '../interfaces/request.interfaces';
import { combineLatest, mergeMap, Observable, of } from 'rxjs';
import { QueryTypes } from '../constants/enums/query-types.enum';
import { FormArray, FormGroup } from '@angular/forms';
import { SingleRequestForm } from '../types/request.types';
import { FileReaderService } from '../../core/services/file-reader.service';

@Injectable()
export class RequestBuilderService {
  constructor(private fileReaderService: FileReaderService) {}

  buildQuery(
    forms: FormArray<FormGroup<SingleRequestForm>>
  ): Observable<TisRequest> {
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
      mergeMap((value: (FileRequest | SqlRequest)[]) =>
        of({
          qtisver: 1,
          requests: value,
          qtisend: true,
        } as TisRequest)
      )
    );
  }

  private buildSqlQuery(query: string): Observable<SqlRequest> {
    return of({
      parameters: { method: 'plain/text', query: query },
      request: 'sql',
      subsystem: 'sql',
    });
  }

  private buildFileQuery(file: File): Observable<FileRequest> {
    return this.fileReaderService.blobToBase64(file).pipe(
      mergeMap((base64) =>
        of({
          parameters: {
            data: {
              file: base64 as string,
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
