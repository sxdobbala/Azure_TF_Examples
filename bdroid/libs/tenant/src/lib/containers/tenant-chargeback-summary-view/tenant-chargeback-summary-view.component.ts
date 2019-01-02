import { AfterViewInit, Component, OnInit, TemplateRef, ViewChild } from "@angular/core";
import { Crumb } from "@bdroid/breadcrumbs";

import { NgxDataTableComponent } from "../../components/ngx-data-table/ngx-data-table.component";
import { TenantChargebackService } from "../../services/tenantchargeback.service";
import { TenantChargebackSummaryService } from "../../services/tenantcbsummary.service";
import { Entity, EntityColumnDef } from "@bdroid/shared";

@Component({
  selector: "ngx-tenant-chargeback-summary-view",
  templateUrl: "./tenant-chargeback-summary-view.component.html",
  styleUrls: ["../../../../../shared/src/lib/containers/entity/entity.component.scss"]
})
export class TenantChargebackSummaryViewComponent implements OnInit, AfterViewInit {
  crumbs: ReadonlyArray<Crumb> = [
    { name: "Dashboard", link: "/dashboard" },
    { name: "Tenants", link: "/dashboard/tenants" },
    { name: "Tenant Chargeback Summary" }
  ];

  readonly title = "Tenant Chargeback";
  @ViewChild(NgxDataTableComponent) dataTable: NgxDataTableComponent;
  @ViewChild("packageUnit") packageUnit: TemplateRef<any>;
  defaultColumns: any[] = null;

  constructor(
    public tenantChargebackSummaryService: TenantChargebackSummaryService
  ) {
  }


  ngOnInit() {

    this.defaultColumns = [
      new EntityColumnDef<Entity>({
        property: "dateOfChargeback"
      }),
      new EntityColumnDef<Entity>({
        property: "basicUnits",
        header: "Basic Packages",
        cellTemplate: this.packageUnit
      }),
      new EntityColumnDef<Entity>({
        property: "analyticsUnits",
        header: "Analytic Packages",
        cellTemplate: this.packageUnit
      }),
      new EntityColumnDef<Entity>({
        property: "dateOfChargeback"
      })];
  }

  ngAfterViewInit() {

    this.dataTable.refresh();
  }


}
