import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { LoginService } from './login.service';
import { Observable, switchMap, take, tap } from 'rxjs';
import { Router } from '@angular/router';

type LoginForm = {
  login: FormControl<string>;
  password: FormControl<string>;
};

@Component({
  selector: 'app-login-page',
  templateUrl: './login-page.component.html',
  styleUrls: ['./login-page.component.scss'],
})
export class LoginPageComponent implements OnInit {
  form!: FormGroup;

  constructor(
    private fb: FormBuilder,
    private loginService: LoginService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.form = this.fb.nonNullable.group<LoginForm>({
      login: this.fb.nonNullable.control(''),
      password: this.fb.nonNullable.control(''),
    });
  }

  submit(): void {
    this.loginService
      .login(this.form.value.login, this.form.value.password)
      .pipe(
        take(1),
        tap(() => this.router.navigate(['request']))
      )
      .subscribe();
  }
}
