import { AfterViewInit, Component, OnDestroy, OnInit, ViewChild } from "@angular/core";
import { TenantMasterService } from "../../services/tenantmaster.service";
import { Crumb } from "@bdroid/breadcrumbs";

import { TenantMasterEditComponent } from "../../components/tenantmaster-edit/tenantmaster-edit.component";
import { NgxDataTableComponent } from "../../components/ngx-data-table/ngx-data-table.component";
import { TenantMaster } from "../../models/tenant.model";
import { Entity, EntityColumnDef } from "@bdroid/shared";

// TODO: search with facets https://github.com/sfeir-open-source/angular-search-experience
// https://ngx.tools/#/search?q=Go
@Component({
  selector: "ngx-tenants-table",
  templateUrl: "./tenant-master-view.component.html",
  styleUrls: ["../../../../../shared/src/lib/containers/entity/entity.component.scss"]
})
export class TenantMasterViewComponent implements AfterViewInit {
  crumbs: ReadonlyArray<Crumb> = [
    { name: "Dashboard", link: "/dashboard" },
    { name: "Tenant", link: "/dashboard/tenants" },
    { name: "Tenant Master" }
  ];

  readonly title = "Tenant Master";

  readonly dialogComponent = TenantMasterEditComponent;
  dialogEntity = new TenantMaster();
  data: any[] = [];

  defaultColumns: any[] = [
    new EntityColumnDef<Entity>({
      property: "programName"
    }),
    new EntityColumnDef<Entity>({
      property: "environment"
    }),
    new EntityColumnDef<Entity>({
      property: "segment"
    }),
    new EntityColumnDef<Entity>({
      property: "status"
    }),
    new EntityColumnDef<Entity>({
      property: "technicalContact"
    }),
    new EntityColumnDef<Entity>({
      property: "businessContact"
    }),
    new EntityColumnDef<Entity>({
      property: "businessSponsor"
    })];

  @ViewChild(NgxDataTableComponent) dataTable: NgxDataTableComponent;

  constructor(
    public tenantService: TenantMasterService
  ) {
  }


  ngAfterViewInit() {

    this.dataTable.refresh();
  }


}
