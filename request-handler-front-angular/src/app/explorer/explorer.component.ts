import { ChangeDetectionStrategy, Component } from '@angular/core';
import { FormControl } from '@angular/forms';
import { FileSplitterService } from './services/file-splitter.service';
import { mergeMap, Observable, tap } from 'rxjs';
import { ExplorerRequestBuilderService } from './services/explorer-request-builder.service';

@Component({
  selector: 'app-explorer',
  templateUrl: './explorer.component.html',
  styleUrls: ['./explorer.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ExplorerComponent {
  control = new FormControl<File[]>([], { nonNullable: true });
  uploadResult$!: Observable<any>;

  constructor(
    private fileSplitterService: FileSplitterService,
    private explorerRequestBuilderService: ExplorerRequestBuilderService
  ) {}

  uploadFiles() {
    this.uploadResult$ = this.fileSplitterService
      .split(this.control.value)
      .pipe(
        mergeMap((fc: { file: File; chunks: Blob[] }[]) =>
          this.explorerRequestBuilderService.buildChunkRequest(fc)
        ),
        tap(console.log)
      );
  }
}
