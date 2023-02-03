import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginPageComponent } from './login-page/login-page.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { LoginService } from './login-page/login.service';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { CoreModule } from './core/core.module';
import { HttpErrorInterceptor } from './core/interceptors/http-error.interceptor';
import { HeaderComponent } from './header/header.component';
import {
  MAT_ICON_DEFAULT_OPTIONS,
  MatIconDefaultOptions,
  MatIconModule,
} from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import {
  MAT_SNACK_BAR_DEFAULT_OPTIONS,
  MatSnackBarConfig,
} from '@angular/material/snack-bar';
import { RequestsModule } from './requests/requests.module';
import { ApplicationsComponent } from './applications/applications.component';

@NgModule({
  declarations: [
    AppComponent,
    LoginPageComponent,
    HeaderComponent,
    ApplicationsComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    BrowserAnimationsModule,
    CoreModule,
    MatIconModule,
    MatButtonModule,
    RequestsModule,
  ],
  providers: [
    LoginService,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: HttpErrorInterceptor,
      multi: true,
    },
    {
      provide: MAT_SNACK_BAR_DEFAULT_OPTIONS,
      useValue: {
        duration: 3000,
        panelClass: 'snackbar',
      } as MatSnackBarConfig,
    },
    {
      provide: MAT_ICON_DEFAULT_OPTIONS,
      useValue: {
        fontSet: 'material-symbols-outlined',
      } as MatIconDefaultOptions,
    },
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
