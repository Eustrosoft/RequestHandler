import { NgModule } from '@angular/core';
import { PreloadAllModules, RouterModule, Routes } from '@angular/router';
import { LoginPageComponent } from './login-page/login-page.component';
import { AuthenticationGuard } from './core/guards/authentication.guard';
import { NotFoundComponent } from './core/components/not-found/not-found.component';
import { ApplicationsComponent } from './applications/applications.component';

const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', title: 'TIS | Login', component: LoginPageComponent },
  {
    path: '',
    canActivate: [AuthenticationGuard],
    children: [
      {
        path: 'apps',
        title: 'TIS | Apps',
        component: ApplicationsComponent,
      },
      {
        path: 'requests',
        title: 'TIS | Requests',
        loadChildren: () =>
          import('./requests/requests.module').then((m) => m.RequestsModule),
      },
      {
        path: 'explorer',
        title: 'TIS | Explorer',
        loadChildren: () =>
          import('./explorer/explorer.module').then((m) => m.ExplorerModule),
      },
    ],
  },
  {
    path: '**',
    title: 'TIS | Not found',
    pathMatch: 'full',
    component: NotFoundComponent,
  },
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, {
      preloadingStrategy: PreloadAllModules,
    }),
  ],
  exports: [RouterModule],
})
export class AppRoutingModule {}
