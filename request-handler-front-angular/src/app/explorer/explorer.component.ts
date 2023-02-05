import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  OnInit,
  ViewChild,
} from '@angular/core';
import { FormControl } from '@angular/forms';
import {
  asapScheduler,
  BehaviorSubject,
  catchError,
  combineLatest,
  concatMap,
  delay,
  delayWhen,
  EMPTY,
  finalize,
  from,
  interval,
  map,
  mergeMap,
  Observable,
  of,
  tap,
  timer,
  zip,
} from 'rxjs';
import { ExplorerRequestBuilderService } from './services/explorer-request-builder.service';
import { ExplorerService } from './services/explorer.service';
import {
  ChunkedFileRequest,
  TisRequest,
  TisResponse,
} from '../requests/interfaces/request.interfaces';
import { FileReaderService } from '../core/services/file-reader.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { InputFileComponent } from '../core/components/input-file/input-file.component';
import { HttpHeaders } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-explorer',
  templateUrl: './explorer.component.html',
  styleUrls: ['./explorer.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ExplorerComponent {
  @ViewChild(InputFileComponent) inputFileComponent!: InputFileComponent;
  control = new FormControl<File[]>([], { nonNullable: true });
  uploadResult$!: Observable<any>;
  progressBarValue: number = 0;
  currentFile: string = '';
  showProgressBar = new BehaviorSubject<boolean>(false);

  constructor(
    private fileReaderService: FileReaderService,
    private explorerRequestBuilderService: ExplorerRequestBuilderService,
    private explorerService: ExplorerService,
    private snackBar: MatSnackBar,
    private cd: ChangeDetectorRef
  ) {}

  uploadFilesBase64(): void {
    if (this.control.value.length === 0) {
      this.snackBar.open('Select files first', 'Close');
      return;
    }
    let uploadError = false;
    this.uploadResult$ = this.fileReaderService
      .splitBase64(this.control.value)
      .pipe(
        mergeMap((fc: { file: File; chunks: string[] }) =>
          this.explorerRequestBuilderService.buildChunkRequest(fc).pipe()
        ),
        tap((request: TisRequest) => {
          console.log(request);
          this.showProgressBar.next(true);
        }),
        concatMap((query: TisRequest) => this.explorerService.upload(query)),
        tap(
          (response: {
            request: TisRequest;
            response: TisResponse;
            totalChunks: number;
            currentChunk: number;
          }) => {
            const req = response.request.requests[0] as ChunkedFileRequest;
            this.currentFile = req.parameters.data.name;
            this.progressBarValue =
              100 * ((response.currentChunk + 1) / response.totalChunks);
          }
        ),
        catchError((err) => {
          uploadError = true;
          console.error(err);
          this.snackBar.open('Error making request', 'Close');
          this.showProgressBar.next(false);
          return EMPTY;
        }),
        finalize(() => {
          if (!uploadError) {
            this.snackBar.open('Upload completed', 'Close');
            this.showProgressBar.next(false);
            this.control.patchValue([]);
            this.inputFileComponent.patchInput();
            this.cd.markForCheck();
          }
        })
      );
  }

  uploadFilesBinary(): void {
    if (this.control.value.length === 0) {
      this.snackBar.open('Select files first', 'Close');
      return;
    }
    let uploadError = false;
    this.uploadResult$ = this.fileReaderService
      .splitBinary(this.control.value)
      .pipe(
        tap(() => {
          this.showProgressBar.next(true);
        }),
        concatMap(({ file, chunks }) => {
          return from(chunks).pipe(
            concatMap((chunk: Blob, currentChunk: number) => {
              const request =
                this.explorerRequestBuilderService.buildBinaryChunkRequest(
                  file,
                  chunk,
                  currentChunk,
                  chunks.length
                );
              const formData = new FormData();
              formData.set('file', chunk);
              formData.set('json', JSON.stringify(request));

              return combineLatest([
                this.explorerService.uploadChunks(formData, {
                  'Content-Disposition': `form-data; name="file"; filename="${file.name}"`,
                }),
                of(file),
                of(chunks),
                of(currentChunk),
              ]);
            })
          );
        }),
        tap(([response, file, chunks, currentChunk]) => {
          this.currentFile = file.name;
          this.progressBarValue = 100 * ((currentChunk + 1) / chunks.length);
        }),
        catchError((err) => {
          uploadError = true;
          console.error(err);
          this.snackBar.open('Error making request', 'Close');
          this.showProgressBar.next(false);
          return EMPTY;
        }),
        finalize(() => {
          if (!uploadError) {
            this.snackBar.open('Upload completed', 'Close');
            this.showProgressBar.next(false);
            this.control.patchValue([]);
            this.inputFileComponent.patchInput();
            this.cd.markForCheck();
          }
        })
      );
  }

  filesDropped(files: File[]): void {
    this.control.patchValue(files);
    this.inputFileComponent.patchInput();
  }

  deleteFile(index: number): void {
    this.control.value.splice(index, 1);
    this.control.patchValue(this.control.value);
    this.inputFileComponent.patchInput();
  }
}
