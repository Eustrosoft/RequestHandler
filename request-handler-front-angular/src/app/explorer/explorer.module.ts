import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ExplorerRoutingModule } from './explorer-routing.module';
import { ExplorerComponent } from './explorer.component';
import { CoreModule } from '../core/core.module';
import { ExplorerService } from './services/explorer.service';
import { ExplorerRequestBuilderService } from './services/explorer-request-builder.service';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { FilesDragAndDropDirective } from './directives/files-drag-and-drop.directive';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatRippleModule } from '@angular/material/core';
import { RippleHoverDirective } from './directives/ripple-hover.directive';
import { MatMenuModule } from '@angular/material/menu';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatTableModule } from '@angular/material/table';

@NgModule({
  declarations: [
    ExplorerComponent,
    FilesDragAndDropDirective,
    RippleHoverDirective,
  ],
  imports: [
    CommonModule,
    ExplorerRoutingModule,
    CoreModule,
    MatProgressBarModule,
    MatListModule,
    MatIconModule,
    MatRippleModule,
    MatMenuModule,
    MatCheckboxModule,
    MatTableModule,
  ],
  providers: [ExplorerService, ExplorerRequestBuilderService],
})
export class ExplorerModule {}
