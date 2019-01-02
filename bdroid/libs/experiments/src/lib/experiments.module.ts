import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { ScrollingModule } from '@angular/cdk/scrolling';

import { AnimationsComponent } from './containers/animations/animations.component';
import { HammertimeDirective } from './components/hammertime/hammertime.directive';
import { LayoutComponent } from './containers/layout/layout.component';
import { CardComponent } from './components/card/card.component';
import { SharedModule } from '@bdroid/shared';

@NgModule({
  imports: [
    SharedModule,
    ScrollingModule,
    RouterModule.forChild([
      /* {path: '', pathMatch: 'full', component: InsertYourComponentHere} */
      { path: '', redirectTo: 'animations', pathMatch: 'full' },
      {
        path: 'animations',
        component: AnimationsComponent,
        data: { title: 'Animations', depth: 3 },
      },
      {
        path: 'layout',
        component: LayoutComponent,
        data: { title: 'Layout', depth: 3 },
      },
    ]),
  ],
  declarations: [
    AnimationsComponent,
    HammertimeDirective,
    LayoutComponent,
    CardComponent,
  ],
})
export class ExperimentsModule {}
