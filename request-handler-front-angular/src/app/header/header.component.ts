import {Component, OnInit} from '@angular/core';
import {LoginService} from '../login-page/login.service';
import {AuthenticationService} from '../core/services/authentication.service';
import {Router} from '@angular/router';
import {Observable, take, tap} from 'rxjs';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss'],
})
export class HeaderComponent implements OnInit {
  constructor(
    private loginService: LoginService,
    private authenticationService: AuthenticationService,
    private router: Router
  ) {}

  isAuthenticated: Observable<boolean> | undefined;

  ngOnInit() {
    this.isAuthenticated =
      this.authenticationService.isAuthenticated.asObservable();
  }

  logout() {
    this.loginService
      .logout()
      .pipe(
        take(1),
        tap(() => {
          localStorage.setItem('isAuthenticated', 'false');
          this.authenticationService.isAuthenticated.next(false);
          this.router.navigate(['login']);
        })
      )
      .subscribe();
  }
}
