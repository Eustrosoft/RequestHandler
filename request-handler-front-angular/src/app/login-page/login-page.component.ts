import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, Validators,} from '@angular/forms';
import {LoginService} from './login.service';
import {take} from 'rxjs';
import {Router} from '@angular/router';
import {InputTypes} from '../core/constants/enums/input-type.enum';

type LoginForm = {
  login: FormControl<string>;
  password: FormControl<string>;
  submit: FormControl<boolean>;
};

@Component({
  selector: 'app-login-page',
  templateUrl: './login-page.component.html',
  styleUrls: ['./login-page.component.scss'],
})
export class LoginPageComponent implements OnInit {
  form!: FormGroup;
  InputTypes = InputTypes;

  constructor(
    private fb: FormBuilder,
    private loginService: LoginService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.form = this.fb.nonNullable.group<LoginForm>({
      login: this.fb.nonNullable.control('', Validators.required),
      password: this.fb.nonNullable.control('', Validators.required),
      submit: this.fb.nonNullable.control(false),
    });
  }

  submit(): void {
    this.form.get('submit')?.disable();
    this.loginService
      .login(this.form.value.login, this.form.value.password)
      .pipe(take(1))
      .subscribe({
        next: () => this.router.navigate(['request']),
        complete: () => this.form.get('submit')?.enable(),
      });
  }
}
