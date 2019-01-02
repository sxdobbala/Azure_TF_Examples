import { AfterViewInit, Component, ViewChild } from "@angular/core";
import { Crumb } from "@bdroid/breadcrumbs";

import { NgxDataTableComponent } from "../../components/ngx-data-table/ngx-data-table.component";
import { TenantChargebackService } from "../../services/tenantchargeback.service";
import { Entity, EntityColumnDef } from "@bdroid/shared";

@Component({
  selector: "ngx-tenant-chargeback-view",
  templateUrl: "./tenant-chargeback-view.component.html",
  styleUrls: ["../../../../../shared/src/lib/containers/entity/entity.component.scss"]
})
export class TenantChargebackViewComponent implements AfterViewInit {
  crumbs: ReadonlyArray<Crumb> = [
    { name: "Dashboard", link: "/dashboard" },
    { name: "Tenants", link: "/dashboard/tenants" },
    { name: "Tenant Chargeback" }
  ];

  readonly title = "Tenant Chargeback";


  defaultColumns: any[] = [
    new EntityColumnDef<Entity>({
      property: "dateOfChargeback"
    }),
    new EntityColumnDef<Entity>({
      property: "serviceGroupCostCenter"
    }),
    new EntityColumnDef<Entity>({
      property: "internalApplicationName"
    }),
    new EntityColumnDef<Entity>({
      property: "projectNumber"
    }),
    new EntityColumnDef<Entity>({
      property: "clientAcceptance"
    }),
    new EntityColumnDef<Entity>({
      property: "gl_ou"
    }),
    new EntityColumnDef<Entity>({
      property: "gl_loc"
    }),
    new EntityColumnDef<Entity>({
      property: "serviceDeliveryDate"
    }),
    new EntityColumnDef<Entity>({
      property: "gl_bu"
    })];


  @ViewChild(NgxDataTableComponent) dataTable: NgxDataTableComponent;

  constructor(
    public tenantChargebackService: TenantChargebackService
  ) {
  }


  ngAfterViewInit() {

    this.dataTable.refresh();
  }


}
