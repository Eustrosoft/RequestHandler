import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { catchError, EMPTY, map, mergeMap, Observable, tap } from 'rxjs';
import { RequestBuilderService } from './request-builder.service';
import {
  TisQuery,
  TisResponse,
  TisTableResult,
} from './interfaces/request.interfaces';
import { RequestService } from './request.service';
import { QueryTypes } from './constants/enums/query-types.enum';
import { RequestForm } from './types/request.types';
import { DisplayTypes } from './constants/enums/display-types.enum';
import { Table } from './interfaces/table.interface';

@Component({
  selector: 'app-request',
  templateUrl: './request.component.html',
  styleUrls: ['./request.component.scss'],
})
export class RequestComponent implements OnInit {
  form!: FormGroup;
  QueryTypes = QueryTypes;
  DisplayTypes = DisplayTypes;
  queryType = new FormControl<QueryTypes>(QueryTypes.SQL, {
    nonNullable: true,
  });
  queryTypeOptions: string[] = [...Object.values(QueryTypes)];
  displayType = new FormControl<DisplayTypes>(DisplayTypes.TEXT, {
    nonNullable: true,
  });
  displayTypeOptions: string[] = [...Object.values(DisplayTypes)];

  tables?: Table[];

  requestResult$!: Observable<TisResponse>;
  isResultLoading: boolean = false;

  constructor(
    private fb: FormBuilder,
    private requestBuilderService: RequestBuilderService,
    private requestService: RequestService
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group<RequestForm>({
      request: this.fb.control(
        'select * from tis.samusers;select * from tis.files;select * from tis.samacl;select * from tis.comments;'
      ),
      file: this.fb.control(null),
      submit: this.fb.control(false),
    });
  }

  submit() {
    this.form.get('submit')?.disable();
    this.requestResult$ = this.requestBuilderService
      .buildQuery(this.queryType.value, this.form)
      .pipe(
        mergeMap((query: TisQuery) => this.requestService.dispatch(query)),
        map((response: TisResponse) => {
          const results = response.responses.map((res) => res.result).flat();
          this.tables = results.map((result: TisTableResult) => {
            return {
              dataSource: result.rows.map((row) => {
                return Object.fromEntries(
                  result.columns.map((_, i) => [result.columns[i], row[i]])
                );
              }),
              columnsToDisplay: result.columns,
              displayedColumns: result.columns,
              data_types: result.data_types,
            };
          });
          return response;
        }),
        tap(() => this.form.get('submit')?.enable()),
        catchError((err) => {
          this.form.get('submit')?.enable();
          return EMPTY;
        })
      );
  }
}
