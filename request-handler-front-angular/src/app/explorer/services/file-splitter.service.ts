import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';

@Injectable()
export class FileSplitterService {
  constructor() {}

  // 1048576 байт = 1 Мегабайт
  split(
    files: File[],
    chunkSize: number = 1048576
  ): Observable<{ file: File; chunks: Blob[] }[]> {
    const chunks = files.flatMap((file: File, index: number) => {
      let startPointer = 0;
      let endPointer = file.size;
      let chunks = [];
      while (startPointer < endPointer) {
        let newStartPointer = startPointer + chunkSize;
        chunks.push(
          file.slice(startPointer, newStartPointer, 'application/octet-stream')
        );
        startPointer = newStartPointer;
      }
      return { file: files[index], chunks: chunks };
    });
    return of(chunks);
  }
}
