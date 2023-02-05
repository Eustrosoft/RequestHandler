import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ExplorerComponent } from './explorer.component';
import { FileSystemComponent } from './components/file-system/file-system.component';

const routes: Routes = [
  {
    path: '',
    component: ExplorerComponent,
    children: [
      {
        path: 'folders',
        component: FileSystemComponent,
      },
      {
        path: 'folders/:id',
        component: FileSystemComponent,
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ExplorerRoutingModule {}
