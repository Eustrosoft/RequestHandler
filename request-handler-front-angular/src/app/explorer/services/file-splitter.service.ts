import { Injectable } from '@angular/core';
import {
  combineLatest,
  from,
  map,
  mergeMap,
  Observable,
  of,
  toArray,
} from 'rxjs';
import { FileBase64Service } from '../../core/services/file-base64.service';

@Injectable()
export class FileSplitterService {
  constructor(private fileBase64Service: FileBase64Service) {}

  /**
   *
   * @param files File[]
   * @param chunkSize 1048576 = 1 MB
   */
  split(
    files: File[],
    chunkSize: number = 1048576
  ): Observable<{ file: File; chunks: string[] }[]> {
    // First to base64 than slice
    return from(files).pipe(
      mergeMap((file: File) => {
        const buffer = this.fileBase64Service.blobToBase64(file);
        return combineLatest([of(file), buffer]).pipe(
          map(([file, buff]) => ({
            file: file,
            buffer: buff as string,
          }))
        );
      }),
      mergeMap(({ file, buffer }) => {
        let startPointer = 0;
        let endPointer = buffer.length;
        let chunks = [];
        while (startPointer < endPointer) {
          let newStartPointer = startPointer + chunkSize;
          chunks.push(buffer.slice(startPointer, newStartPointer));
          startPointer = newStartPointer;
        }
        return of({ file: file, chunks: chunks });
      }),
      toArray()
    );

    // First slice than chunks to base64
    // return from(files).pipe(
    //   map((file: File, index: number) => {
    //     let startPointer = 0;
    //     let endPointer = file.size;
    //     let chunks = [];
    //     while (startPointer < endPointer) {
    //       let newStartPointer = startPointer + chunkSize;
    //       chunks.push(
    //         file.slice(
    //           startPointer,
    //           newStartPointer,
    //           'application/octet-stream'
    //         )
    //       );
    //       startPointer = newStartPointer;
    //     }
    //     return { file: files[index], chunks: chunks };
    //   }),
    //   mergeMap((chunkedFile: { file: File; chunks: Blob[] }) => {
    //     const mappedChunks = from(chunkedFile.chunks).pipe(
    //       mergeMap((chunk: Blob) =>
    //         this.fileBase64Service.blobChunkToBase64(chunk)
    //       ),
    //       toArray()
    //     );
    //     return combineLatest([of(chunkedFile), mappedChunks]).pipe(
    //       map(([file, base64Chunks]) => ({
    //         ...file,
    //         chunks: base64Chunks,
    //       }))
    //     );
    //   }),
    //   toArray()
    // );
  }
}
