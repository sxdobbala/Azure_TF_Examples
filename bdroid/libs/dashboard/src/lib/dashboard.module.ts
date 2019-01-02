import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { SharedModule } from '@bdroid/shared';

import { AuthGuard } from '@bdroid/auth';

import { DashboardLayoutComponent } from './containers/dashboard-layout/dashboard-layout.component';
import { OverviewComponent } from './containers/overview/overview.component';
import { RainbowComponent } from './components/rainbow/rainbow.component';
import { QuickpanelModule } from '@bdroid/quickpanel';
import { ToolbarModule } from '@bdroid/toolbar';
import { SidenavModule } from '@bdroid/sidenav';
import { NavMenuComponent } from "./components/nav-menu/nav-menu.component";
import { AppMenuComponent } from "./components/app-menu/app-menu.component";

@NgModule({
  imports: [
    SharedModule,
    SidenavModule,
    ToolbarModule,
    QuickpanelModule,
    RouterModule.forChild([
      /* {path: '', pathMatch: 'full', component: InsertYourComponentHere} */
      {
        path: '',
        component: DashboardLayoutComponent,
        canActivate: [AuthGuard],
        data: { title: 'Dashboard', depth: 1 },
        children: [
          {
            path: '',
            component: OverviewComponent,
            data: { animation: 'overview' },
          },
          {
            path: 'grid',
            loadChildren: '@bdroid/grid#GridModule',
            data: { title: 'Grid', depth: 2, preload: false },
          },
          {
            path: 'experiments',
            loadChildren: '@bdroid/experiments#ExperimentsModule',
            data: { title: 'Experiments', depth: 2, preload: false },
          },
          {
            path: 'tenants',
            loadChildren: '@bdroid/tenant#TenantModule',
            data: { title: 'Tenant', depth: 2, preload: false },
          },
        ],
      },
    ]),
  ],
  declarations: [DashboardLayoutComponent, OverviewComponent, NavMenuComponent, AppMenuComponent, RainbowComponent],
})
export class DashboardModule {
}
