import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  OnInit,
} from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import {
  BehaviorSubject,
  catchError,
  map,
  mergeMap,
  Observable,
  of,
  tap,
} from 'rxjs';
import { RequestBuilderService } from './services/request-builder.service';
import {
  TisQuery,
  TisResponse,
  TisResponseBody,
  TisTableResult,
} from './interfaces/request.interfaces';
import { RequestService } from './services/request.service';
import { QueryTypes } from './constants/enums/query-types.enum';
import { RequestsForm } from './types/request.types';
import { DisplayTypes } from './constants/enums/display-types.enum';
import { Table } from './interfaces/table.interface';
import { MatSnackBar } from '@angular/material/snack-bar';
import { RequestFormBuilderService } from './services/request-form-builder.service';

@Component({
  selector: 'app-requests',
  templateUrl: './requests.component.html',
  styleUrls: ['./requests.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RequestsComponent implements OnInit {
  form!: FormGroup<RequestsForm>;
  DisplayTypes = DisplayTypes;
  displayType = new FormControl<DisplayTypes>(DisplayTypes.TEXT);
  queryTypeOptions: string[] = [...Object.values(QueryTypes)];
  displayTypeOptions: string[] = [...Object.values(DisplayTypes)];

  tables?: Table[][];

  requestResult$!: Observable<TisResponse | null>;
  isResultLoading = new BehaviorSubject<boolean>(false);

  constructor(
    private fb: FormBuilder,
    private requestBuilderService: RequestBuilderService,
    private requestFormBuilderService: RequestFormBuilderService,
    private requestService: RequestService,
    private cd: ChangeDetectorRef,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.form = this.requestFormBuilderService.makeRequestForm();
  }

  submit(): void {
    this.form.get('submit')?.disable();
    this.isResultLoading.next(true);
    this.requestResult$ = this.requestBuilderService
      .buildQuery(this.form.controls.forms)
      .pipe(
        mergeMap((query: TisQuery) => this.requestService.dispatch(query)),
        map((response: TisResponse) => {
          this.tables = response.responses.map((res: TisResponseBody) =>
            res.result.map((result: TisTableResult) => {
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
            })
          );
          return response;
        }),
        tap(() => {
          this.form.get('submit')?.enable();
          this.isResultLoading.next(false);
        }),
        catchError((err: string) => {
          this.form.get('submit')?.enable();
          this.isResultLoading.next(false);
          this.snackBar.open(err, 'Close');
          this.cd.detectChanges();
          return of(null);
        })
      );
  }

  addForm(): void {
    this.form.controls.forms.push(
      this.requestFormBuilderService.makeNewRequestForm()
    );
  }

  removeLastForm(): void {
    const index = this.form.controls.forms.length - 1;
    this.form.controls.forms.removeAt(index);
  }
}
