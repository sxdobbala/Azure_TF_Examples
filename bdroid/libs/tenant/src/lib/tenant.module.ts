import { LOCALE_ID, NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";
import { SharedModule } from "@bdroid/shared";
import { HelperModule } from "@bdroid/ngx-utils";
import { TenantMasterService } from "./services/tenantmaster.service";
import { AppConfirmModule } from "@bdroid/app-confirm";

import { TenantMasterViewComponent } from "./containers/tenant-master-view/tenant-master-view.component";
import { TenantMasterEditComponent } from "./components/tenantmaster-edit/tenantmaster-edit.component";
import { TenantMasterDetailComponent } from "./components/tenantmaster-detail/tenantmaster-detail.component";
import { NgxChartsModule } from "@swimlane/ngx-charts";
import { NgxGraphModule } from "@swimlane/ngx-graph";
import { NgxDataTableComponent } from "./components/ngx-data-table/ngx-data-table.component";
import {
  MatCheckboxModule,
  MatInputModule, MatDatepickerModule,
  MatPaginatorModule,
  MatSortModule,
  MatTableModule, MAT_DATE_LOCALE
} from "@angular/material";
import { NgxDataDetailTableComponent } from "./components/ngx-data-detail-table/ngx-data-detail-table.component";
import { FormsModule } from "@angular/forms";
import { TenantEdgeViewComponent } from "./containers/tenant-edge-view/tenantedge-view.component";
import { TenantEdgeService } from "./services/tenantedge.service";
import { TenantHiveServerService } from "./services/tenanthiveserver.service";
import { TenantHiveServerViewComponent } from "./containers/tenant-hive-server-view/tenant-hive-server-view.component";
import { TenantPackagesViewComponent } from "./containers/tenant-packages-view/tenant-packages-view.component";
import { TenantPackagesEditComponent } from "./components/tenant-packages-edit/tenant-packages-edit.component";
import { TenantPackageService } from "./services/tenantpackage.service";
import { TenantChargebackViewComponent } from "./containers/tenant-chargeback-view/tenant-chargeback-view.component";
import { TenantChargebackService } from "./services/tenantchargeback.service";
import { TenantChargebackSummaryViewComponent } from "./containers/tenant-chargeback-summary-view/tenant-chargeback-summary-view.component";
import { TenantChargebackSummaryService } from "./services/tenantcbsummary.service";
import { TenantDataSourcesViewComponent } from "./containers/tenant-data-sources-view/tenant-data-sources-view.component";
import { TenantDataSourceService } from "./services/tenantdatasource.service";
import { TenantDataSourcesEditComponent } from "./components/tenant-data-sources-edit/tenant-data-sources-edit.component";
import { TenantChargebackProfileComponent } from "./containers/tenant-chargeback-profile/tenant-chargeback-profile.component";
import { TenantChargebackProfileService } from "./services/tenantchargebackprofile.service";
import { TenantComputeViewComponent } from "./containers/tenant-compute-view/tenant-compute-view.component";
import { TenantComputeService } from "./services/tenantcompute.service";


@NgModule({
  imports: [
    CommonModule,
    SharedModule,
    HelperModule,
    FormsModule,
    AppConfirmModule,
    NgxChartsModule,
    NgxGraphModule,
    MatTableModule,
    MatDatepickerModule,
    MatCheckboxModule,
    MatPaginatorModule,
    MatSortModule,
    MatInputModule,
    RouterModule.forChild([

      { path: "", redirectTo: "tenant-master-view", pathMatch: "full" },
      {
        path: "tenant-master-view",
        component: TenantMasterViewComponent,
        data: { title: "Tenants", depth: 3 },
        children: [
          {
            path: ":id",
            component: TenantMasterDetailComponent,
            data: { title: "Tenant Master Detail" }
          }
        ]
      },
      {
        path: "tenant-edge-view",
        component: TenantEdgeViewComponent,
        data: { title: "Tenants Edge", depth: 3 }
      },
      {
        path: "tenant-hive-server-view",
        component: TenantHiveServerViewComponent,
        data: { title: "Tenants Hive Servers", depth: 2 }
      },
      {
        path: "tenant-packages",
        component: TenantPackagesViewComponent,
        data: { title: "Tenants Packages", depth: 2 }
      },
      {
        path: "tenant-chargeback",
        component: TenantChargebackViewComponent,
        data: { title: "Tenants Chargeback", depth: 2 }
      },
      {
        path: "tenant-chargeback-profile",
        component: TenantChargebackProfileComponent,
        data: { title: "Tenant Chargeback Profile", depth: 2 }
      },
      {
        path: "tenant-chargeback-summary",
        component: TenantChargebackSummaryViewComponent,
        data: { title: "Tenants Chargeback Summary", depth: 2 }
      },
      {
        path: "tenant-data-source",
        component: TenantDataSourcesViewComponent,
        data: { title: "Tenant Data Sources", depth: 2 }
      },
      {
        path: "tenant-compute",
        component: TenantComputeViewComponent,
        data: { title: "Tenant Compute", depth: 2 }
      }
    ])
  ],
  declarations: [NgxDataTableComponent, NgxDataDetailTableComponent, TenantMasterViewComponent, TenantMasterEditComponent,
    TenantMasterDetailComponent,
    TenantEdgeViewComponent,
    TenantHiveServerViewComponent,
    TenantPackagesViewComponent,
    TenantPackagesEditComponent,
    TenantChargebackViewComponent,
    TenantChargebackSummaryViewComponent,
    TenantDataSourcesViewComponent,
    TenantDataSourcesEditComponent,
    TenantChargebackProfileComponent,
    TenantComputeViewComponent],
  entryComponents: [TenantMasterEditComponent, TenantPackagesEditComponent, TenantDataSourcesEditComponent],
  providers: [TenantMasterService, TenantEdgeService, TenantChargebackProfileService, TenantHiveServerService,
    TenantDataSourceService, TenantChargebackSummaryService, TenantChargebackService, TenantComputeService, TenantPackageService]

})
export class TenantModule {
}
