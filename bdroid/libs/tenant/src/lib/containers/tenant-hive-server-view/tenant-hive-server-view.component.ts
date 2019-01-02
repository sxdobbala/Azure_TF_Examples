import { AfterViewInit, Component, ViewChild } from "@angular/core";
import { Crumb } from "@bdroid/breadcrumbs";

import { NgxDataTableComponent } from "../../components/ngx-data-table/ngx-data-table.component";
import { TenantHiveServerService } from "../../services/tenanthiveserver.service";
import { Entity, EntityColumnDef } from "@bdroid/shared";

@Component({
  selector: "ngx-tenant-hive-server-view",
  templateUrl: "./tenant-hive-server-view.component.html",
  styleUrls: ["../../../../../shared/src/lib/containers/entity/entity.component.scss"]
})
export class TenantHiveServerViewComponent implements AfterViewInit {
  crumbs: ReadonlyArray<Crumb> = [
    { name: "Dashboard", link: "/dashboard" },
    { name: "Tenants", link: "/dashboard/tenants" },
    { name: "Tenant Hive Servers" }
  ];

  readonly title = "Tenant Hive Servers";

  defaultColumns: any[] = [
    new EntityColumnDef<Entity>({
      property: "environment"
    }),
    new EntityColumnDef<Entity>({
      property: "programName"
    }),
    new EntityColumnDef<Entity>({
      property: "hostName"
    }),
    new EntityColumnDef<Entity>({
      property: "port"
    }),
    new EntityColumnDef<Entity>({
      property: "metaPort"
    })];

  @ViewChild(NgxDataTableComponent) dataTable: NgxDataTableComponent;

  constructor(
    public tenantHiveServerService: TenantHiveServerService
  ) {
  }


  ngAfterViewInit() {

    this.dataTable.refresh();
  }


}
