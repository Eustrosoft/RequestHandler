import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginPageComponent } from './login-page/login-page.component';
import { RequestComponent } from './request/request.component';
import { AuthenticationGuard } from './core/guards/authentication.guard';
import { NotFoundComponent } from './core/components/not-found/not-found.component';

const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', title: 'TIS | Login', component: LoginPageComponent },
  {
    path: 'request',
    title: 'TIS | Request',
    component: RequestComponent,
    canActivate: [AuthenticationGuard],
  },
  {
    path: '**',
    title: 'TIS | Not found',
    pathMatch: 'full',
    component: NotFoundComponent,
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
