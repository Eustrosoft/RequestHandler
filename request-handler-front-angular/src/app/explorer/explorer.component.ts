import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  ViewChild,
} from '@angular/core';
import { FormControl } from '@angular/forms';
import {
  catchError,
  concatMap,
  EMPTY,
  finalize,
  mergeMap,
  Observable,
  tap,
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
  showProgressBar: boolean = false;

  constructor(
    private fileReaderService: FileReaderService,
    private explorerRequestBuilderService: ExplorerRequestBuilderService,
    private explorerService: ExplorerService,
    private snackBar: MatSnackBar,
    private cd: ChangeDetectorRef
  ) {}

  uploadFiles(): void {
    if (this.control.value.length === 0) {
      this.snackBar.open('Select a files first', 'Close');
      return;
    }
    let uploadError = false;
    this.uploadResult$ = this.fileReaderService.split(this.control.value).pipe(
      mergeMap((fc: { file: File; chunks: string[] }) =>
        this.explorerRequestBuilderService.buildChunkRequest(fc).pipe()
      ),
      tap((request: TisRequest) => {
        console.log(request);
        this.showProgressBar = true;
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
        this.showProgressBar = false;
        return EMPTY;
      }),
      finalize(() => {
        if (!uploadError) {
          this.snackBar.open('Upload completed', 'Close');
          this.showProgressBar = false;
          this.control.patchValue([]);
          this.inputFileComponent.patchInput();
          this.cd.markForCheck();
        }
        window.performance.mark('reader:end');
        console.log(
          'Duration: ',
          Math.ceil(
            window.performance.measure('reader', 'reader:start', 'reader:end')
              .duration
          )
        );
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
