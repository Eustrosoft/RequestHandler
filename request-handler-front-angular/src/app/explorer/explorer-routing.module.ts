import { NgModule } from '@angular/core';
import {
  Route,
  RouterModule,
  Routes,
  UrlMatchResult,
  UrlSegment,
  UrlSegmentGroup,
} from '@angular/router';
import { ExplorerComponent } from './explorer.component';

const routes: Routes = [
  {
    matcher: (
      segments: UrlSegment[],
      group: UrlSegmentGroup,
      route: Route
    ): UrlMatchResult | null => {
      if ((group.segments[0].path = 'explorer')) {
        return {
          consumed: segments,
          posParams: {
            path: new UrlSegment(segments.slice().join('/'), {}),
          },
        };
      }
      return null;
    },
    component: ExplorerComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ExplorerRoutingModule {}
