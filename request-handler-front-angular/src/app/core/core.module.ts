import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { InputComponent } from './components/input/input.component';
import { MatInputModule } from '@angular/material/input';
import { ReactiveFormsModule } from '@angular/forms';
import { FormControlPipe } from './pipes/form-control.pipe';
import { ButtonComponent } from './components/button/button.component';
import { MatButtonModule } from '@angular/material/button';
import { AuthenticationGuard } from './guards/authentication.guard';
import { AuthenticationService } from './services/authentication.service';
import { SelectComponent } from './components/select/select.component';
import { MatOptionModule } from '@angular/material/core';
import { MatSelectModule } from '@angular/material/select';
import { InputFileComponent } from './components/input-file/input-file.component';
import { UnauthenticatedInterceptor } from './interceptors/unauthenticated-interceptor.service';
import { HTTP_INTERCEPTORS } from '@angular/common/http';

@NgModule({
  declarations: [
    InputComponent,
    FormControlPipe,
    ButtonComponent,
    SelectComponent,
    InputFileComponent,
  ],
  imports: [
    CommonModule,
    MatInputModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatOptionModule,
    MatSelectModule,
  ],
  exports: [
    InputComponent,
    FormControlPipe,
    ButtonComponent,
    SelectComponent,
    InputFileComponent,
  ],
  providers: [
    AuthenticationGuard,
    AuthenticationService,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: UnauthenticatedInterceptor,
      multi: true,
    },
  ],
})
export class CoreModule {}
