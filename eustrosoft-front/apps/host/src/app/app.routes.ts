import { NxWelcomeComponent } from './nx-welcome.component';
import { Route } from '@angular/router';

export const appRoutes: Route[] = [
  {
    path: 'file-explorer',
    loadChildren: () =>
      import('file-explorer/Module').then((m) => m.RemoteEntryModule),
  },
  {
    path: 'request-handler',
    loadChildren: () =>
      import('request-handler/Module').then((m) => m.RemoteEntryModule),
  },
  {
    path: '',
    component: NxWelcomeComponent,
  },
];
