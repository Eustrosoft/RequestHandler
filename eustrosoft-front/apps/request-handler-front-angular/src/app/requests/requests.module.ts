import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RequestsComponent } from './requests.component';
import { RequestComponent } from './components/request/request.component';
import { RequestBuilderService } from './services/request-builder.service';
import { RequestFormBuilderService } from './services/request-form-builder.service';
import { RequestService } from './services/request.service';
import { ReactiveFormsModule } from '@angular/forms';
import { CoreModule } from '../core/core.module';
import { MatTableModule } from '@angular/material/table';
import { RequestsRoutingModule } from './requests-routing.module';

@NgModule({
  declarations: [RequestsComponent, RequestComponent],
  imports: [
    CommonModule,
    RequestsRoutingModule,
    ReactiveFormsModule,
    CoreModule,
    MatTableModule,
  ],
  providers: [RequestBuilderService, RequestFormBuilderService, RequestService],
})
export class RequestsModule {}
