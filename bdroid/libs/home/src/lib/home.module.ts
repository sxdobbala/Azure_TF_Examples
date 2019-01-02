import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { SharedModule } from '@bdroid/shared';
import { ThemePickerModule } from '@bdroid/theme-picker';
import { HeaderComponent } from './components/header/header.component';
import { FooterComponent } from './components/footer/footer.component';
import { HomeLayoutComponent } from './containers/home-layout/home-layout.component';
import { LandingComponent } from './containers/landing/landing.component';
import { ScrollToTopModule } from '@bdroid/scroll-to-top';
import { SvgViewerModule } from '@bdroid/svg-viewer';
import { FeaturesComponent } from './containers/features/features.component';
import { StickyHeaderDirective } from './components/header/sticky-header.directive';
import { NgxPageScrollModule } from "ngx-page-scroll";

@NgModule({
  imports: [
    SharedModule,
    ScrollToTopModule,
    ThemePickerModule,
    SvgViewerModule,
    NgxPageScrollModule,
    RouterModule.forChild([
      /* {path: '', pathMatch: 'full', component: InsertYourComponentHere} */
      {
        path: '',
        component: HomeLayoutComponent,
        data: { title: 'Home', animation: 'home' },
        children: [
          {
            path: '',
            component: LandingComponent,
            data: { title: 'Landing', animation: 'home' },
          },
          {
            path: 'features',
            component: FeaturesComponent,
            data: { title: 'Features', animation: 'features' },
          },
        ],
      },
    ]),
  ],
  declarations: [
    HeaderComponent,
    StickyHeaderDirective,
    FooterComponent,
    HomeLayoutComponent,
    LandingComponent,
    FeaturesComponent,
  ],
})
export class HomeModule {}
