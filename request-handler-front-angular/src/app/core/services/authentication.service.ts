import { Injectable } from '@angular/core';

@Injectable()
export class AuthenticationService {
  get isAuthenticated(): boolean {
    return Boolean(localStorage.getItem('isAuthenticated'));
  }

  set isAuthenticated(value: boolean) {
    localStorage.setItem('isAuthenticated', String(value));
  }

  constructor() {}
}
