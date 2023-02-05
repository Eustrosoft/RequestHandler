import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ExplorerRoutingModule } from './explorer-routing.module';
import { ExplorerComponent } from './explorer.component';
import { CoreModule } from '../core/core.module';
import { ExplorerService } from './services/explorer.service';
import { ExplorerRequestBuilderService } from './services/explorer-request-builder.service';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { FilesDragAndDropDirective } from './directives/files-drag-and-drop.directive';
import { FileSystemComponent } from './components/file-system/file-system.component';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatRippleModule } from '@angular/material/core';

@NgModule({
  declarations: [
    ExplorerComponent,
    FilesDragAndDropDirective,
    FileSystemComponent,
  ],
  imports: [
    CommonModule,
    ExplorerRoutingModule,
    CoreModule,
    MatProgressBarModule,
    MatListModule,
    MatIconModule,
    MatRippleModule,
  ],
  providers: [ExplorerService, ExplorerRequestBuilderService],
})
export class ExplorerModule {}
