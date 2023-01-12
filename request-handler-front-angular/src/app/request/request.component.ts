import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import {
  combineLatestWith,
  map,
  mergeMap,
  Observable,
  of,
  Subject,
  switchMap,
  take,
  takeUntil,
  tap,
} from 'rxjs';
import { RequestBuilderService } from './request-builder.service';
import { FileQuery, SqlQuery, TisQuery } from './interfaces/request.interfaces';
import { RequestService } from './request.service';
import { QueryTypes } from './constants/enums/query-types.enum';
import { RequestForm } from './types/request.types';
import { combineLatest } from 'rxjs/internal/operators/combineLatest';

@Component({
  selector: 'app-request',
  templateUrl: './request.component.html',
  styleUrls: ['./request.component.scss'],
})
export class RequestComponent implements OnInit, OnDestroy {
  form!: FormGroup;
  QueryTypes = QueryTypes;
  queryType = new FormControl<QueryTypes>(QueryTypes.SQL, {
    nonNullable: true,
  });
  options: string[] = [...Object.values(QueryTypes)];
  requestResult!: Observable<any>;
  private destroy$ = new Subject<void>();

  constructor(
    private fb: FormBuilder,
    private requestBuilderService: RequestBuilderService,
    private requestService: RequestService
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group<RequestForm>({
      request: this.fb.control('select * from tis.samusers;'),
      file: this.fb.control(null),
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  submit() {
    this.requestResult = this.requestBuilderService
      .buildQuery(this.queryType.value, this.form)
      .pipe(mergeMap((query: TisQuery) => this.requestService.dispatch(query)));
  }
}
