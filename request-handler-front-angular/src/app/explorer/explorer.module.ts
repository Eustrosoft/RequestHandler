import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ExplorerRoutingModule } from './explorer-routing.module';
import { ExplorerComponent } from './explorer.component';
import { CoreModule } from '../core/core.module';
import { FileSplitterService } from './services/file-splitter.service';
import { ExplorerService } from './services/explorer.service';
import { ExplorerRequestBuilderService } from './services/explorer-request-builder.service';

@NgModule({
  declarations: [ExplorerComponent],
  imports: [CommonModule, ExplorerRoutingModule, CoreModule],
  providers: [
    ExplorerService,
    FileSplitterService,
    ExplorerRequestBuilderService,
  ],
})
export class ExplorerModule {}
