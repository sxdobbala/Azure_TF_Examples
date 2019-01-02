import { AfterViewInit, Component, ViewChild } from "@angular/core";
import { Crumb } from "@bdroid/breadcrumbs";

import { NgxDataTableComponent } from "../../components/ngx-data-table/ngx-data-table.component";
import { Entity, EntityColumnDef } from "@bdroid/shared";
import { TenantComputeService } from "../../services/tenantcompute.service";

// TODO: search with facets https://github.com/sfeir-open-source/angular-search-experience
// https://ngx.tools/#/search?q=Go
@Component({
  selector: 'ngx-tenant-compute-view',
  templateUrl: './tenant-compute-view.component.html',
  styleUrls: ["../../../../../shared/src/lib/containers/entity/entity.component.scss"]
})
export class TenantComputeViewComponent implements AfterViewInit {
  crumbs: ReadonlyArray<Crumb> = [
    { name: "Dashboard", link: "/dashboard" },
    { name: "Tenant", link: "/dashboard/tenants" },
    { name: "Tenant Compute" }
  ];

  readonly title = "Tenant Compute";

  readonly dialogComponent = null;
  dialogEntity = null;
  data: any[] = [];
  defaultColumns: any[] = [

    new EntityColumnDef<Entity>({
      property: "environment"
    }),
    new EntityColumnDef<Entity>({
      property: "queue"
    }),
    new EntityColumnDef<Entity>({
      property: "volumeName"
    }),
    new EntityColumnDef<Entity>({
      property: "programName"
    }),
    new EntityColumnDef<Entity>({
      property: "minVcore"
    }),
    new EntityColumnDef<Entity>({
      property: "maxVcore"
    })];
  @ViewChild(NgxDataTableComponent) dataTable: NgxDataTableComponent;

  constructor(
    public tenantComputeService: TenantComputeService
  ) {
  }


  ngAfterViewInit() {

    this.dataTable.refresh();
  }


}
