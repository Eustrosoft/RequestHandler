import { Injectable } from '@angular/core';
import { Observable, Subscriber } from 'rxjs';

@Injectable()
export class FileBase64Service {
  constructor() {}

  blobToBase64(blob: Blob | File): Observable<string | ArrayBuffer | null> {
    return new Observable((obs: Subscriber<string | ArrayBuffer | null>) => {
      if (!(blob instanceof Blob)) {
        obs.error(new Error('`blob` must be an instance of File or Blob.'));
        return;
      }

      const reader = new FileReader();

      reader.onerror = (err) => obs.error(err);
      reader.onabort = (err) => obs.error(err);
      reader.onload = () =>
        obs.next(reader.result!.toString().replace(/^.*,/, ''));
      reader.onloadend = () => obs.complete();

      return reader.readAsDataURL(blob);
    });
  }
}
