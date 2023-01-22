import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormControl, FormGroup} from '@angular/forms';
import {catchError, EMPTY, mergeMap, Observable, tap} from 'rxjs';
import {RequestBuilderService} from './request-builder.service';
import {TisQuery} from './interfaces/request.interfaces';
import {RequestService} from './request.service';
import {QueryTypes} from './constants/enums/query-types.enum';
import {RequestForm} from './types/request.types';
import {DisplayTypes} from './constants/enums/display-types.enum';

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

  requestResult!: Observable<any>;

  constructor(
    private fb: FormBuilder,
    private requestBuilderService: RequestBuilderService,
    private requestService: RequestService
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group<RequestForm>({
      request: this.fb.control('select * from tis.samusers;'),
      file: this.fb.control(null),
      submit: this.fb.control(false),
    });
  }

  submit() {
    this.form.get('submit')?.disable();
    this.requestResult = this.requestBuilderService
      .buildQuery(this.queryType.value, this.form)
      .pipe(
        mergeMap((query: TisQuery) => this.requestService.dispatch(query)),
        tap(() => this.form.get('submit')?.enable()),
        catchError((err) => {
          this.form.get('submit')?.enable();
          return EMPTY;
        })
      );
  }
}
