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

@NgModule({
  declarations: [InputComponent, FormControlPipe, ButtonComponent],
  imports: [CommonModule, MatInputModule, ReactiveFormsModule, MatButtonModule],
  exports: [InputComponent, FormControlPipe, ButtonComponent],
  providers: [AuthenticationGuard, AuthenticationService],
})
export class CoreModule {}
