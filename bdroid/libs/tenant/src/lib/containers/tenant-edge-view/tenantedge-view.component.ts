import { AfterViewInit, Component, ViewChild } from "@angular/core";
import { Crumb } from "@bdroid/breadcrumbs";

import { NgxDataTableComponent } from "../../components/ngx-data-table/ngx-data-table.component";
import { TenantEdgeService } from "../../services/tenantedge.service";
import { Entity, EntityColumnDef } from "@bdroid/shared";


@Component({
  selector: "ngx-tenantedge-view",
  templateUrl: "./tenantedge-view.component.html",
  styleUrls: ["../../../../../shared/src/lib/containers/entity/entity.component.scss"]

})
export class TenantEdgeViewComponent implements AfterViewInit {
  crumbs: ReadonlyArray<Crumb> = [
    { name: "Dashboard", link: "/dashboard" },
    { name: "Tenants", link: "/dashboard/tenants" },
    { name: "Tenant Edges" }
  ];

  readonly title = "Tenant Edges";

  defaultColumns: any[] = [
    new EntityColumnDef<Entity>({
      property: "environment"
    }),
    new EntityColumnDef<Entity>({
      property: "programName"
    }),
    new EntityColumnDef<Entity>({
      property: "hostName"
    })];

  @ViewChild(NgxDataTableComponent) dataTable: NgxDataTableComponent;

  constructor(
    public tenantEdgeService: TenantEdgeService
  ) {
  }


  ngAfterViewInit() {

    this.dataTable.refresh();
  }


}
