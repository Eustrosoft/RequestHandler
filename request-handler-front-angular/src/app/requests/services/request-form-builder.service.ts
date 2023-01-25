import { Injectable } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { RequestsForm, SingleRequestForm } from '../types/request.types';
import { QueryTypes } from '../constants/enums/query-types.enum';

@Injectable()
export class RequestFormBuilderService {
  constructor(private fb: FormBuilder) {}

  makeRequestForm(): FormGroup<RequestsForm> {
    return this.fb.group<RequestsForm>({
      forms: this.fb.array<FormGroup<SingleRequestForm>>([
        this.fb.group<SingleRequestForm>({
          request: this.fb.control(
            'select * from tis.samusers;select * from tis.files;'
          ),
          file: this.fb.control(null),
          queryType: this.fb.control(QueryTypes.SQL),
        }),
        this.fb.group<SingleRequestForm>({
          request: this.fb.control(
            'select * from tis.samacl;select * from tis.comments;'
          ),
          file: this.fb.control(null),
          queryType: this.fb.control(QueryTypes.SQL),
        }),
      ]),
      submit: this.fb.control(false),
    });
  }

  makeNewRequestForm(): FormGroup<SingleRequestForm> {
    return this.fb.group<SingleRequestForm>({
      request: this.fb.control(''),
      file: this.fb.control(null),
      queryType: this.fb.control(QueryTypes.SQL),
    });
  }
}
