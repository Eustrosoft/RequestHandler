import { Injectable } from '@angular/core';
import { Observable, ReplaySubject } from 'rxjs';

@Injectable()
export class FileBase64Service {
  constructor() {}

  fileToBase64(file: File): Observable<string> {
    const result = new ReplaySubject<string>(1);
    const reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = (event) =>
      result.next(event.target!.result!.toString().replace(/^.*,/, ''));
    return result.asObservable();
  }
}
