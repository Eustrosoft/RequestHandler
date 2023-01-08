import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { LoginService } from './login.service';
import { Observable } from 'rxjs';
interface LoginForm {
  login: FormControl<string>;
  password: FormControl<string>;
}

@Component({
  selector: 'app-login-page',
  templateUrl: './login-page.component.html',
  styleUrls: ['./login-page.component.scss'],
})
export class LoginPageComponent implements OnInit {
  form!: FormGroup;
  loginStatus!: Observable<any>;

  constructor(
    private formBuilder: FormBuilder,
    private loginService: LoginService
  ) {}

  ngOnInit(): void {
    this.form = this.formBuilder.group<LoginForm>({
      login: this.formBuilder.nonNullable.control(''),
      password: this.formBuilder.nonNullable.control(''),
    });
  }

  submit() {
    this.loginStatus = this.loginService.login(
      this.form.value.login,
      this.form.value.password
    );
  }
}
