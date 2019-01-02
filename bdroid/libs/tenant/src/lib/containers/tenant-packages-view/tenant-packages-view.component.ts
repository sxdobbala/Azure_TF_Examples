import { AfterViewInit, Component, ViewChild } from "@angular/core";
import { Crumb } from "@bdroid/breadcrumbs";

import { NgxDataTableComponent } from "../../components/ngx-data-table/ngx-data-table.component";
import { TenantPackagesEditComponent } from "../../components/tenant-packages-edit/tenant-packages-edit.component";
import { TenantPackageService } from "../../services/tenantpackage.service";
import { TenantPackage } from "../../models/tenantpackage.model";
import { Entity, EntityColumnDef } from "@bdroid/shared";

// TODO: search with facets https://github.com/sfeir-open-source/angular-search-experience
// https://ngx.tools/#/search?q=Go
@Component({
  selector: 'ngx-tenant-packages-view',
  templateUrl: './tenant-packages-view.component.html',
  styleUrls: ["../../../../../shared/src/lib/containers/entity/entity.component.scss"]
})
export class TenantPackagesViewComponent implements AfterViewInit {
  crumbs: ReadonlyArray<Crumb> = [
    { name: "Dashboard", link: "/dashboard" },
    { name: "Tenant", link: "/dashboard/tenants" },
    { name: "Tenant Packages" }
  ];

  readonly title = "Tenant Packages";

  readonly dialogComponent = TenantPackagesEditComponent;
  dialogEntity = new TenantPackage();
  data: any[] = [];
   defaultColumns: any[] = [
    new EntityColumnDef<Entity>({
      property: "chargebackProgram"
    }),
    new EntityColumnDef<Entity>({
      property: "environment"
    }),
    new EntityColumnDef<Entity>({
      property: "program"
    }),
    new EntityColumnDef<Entity>({
      property: "volume"
    }),
    new EntityColumnDef<Entity>({
      property: "packageName"
    }),
    new EntityColumnDef<Entity>({
      property: "units"
    }),
    new EntityColumnDef<Entity>({
      property: "startDate"
    }),
    new EntityColumnDef<Entity>({
      property: "endDate"
    })];
  @ViewChild(NgxDataTableComponent) dataTable: NgxDataTableComponent;

  constructor(
    public tenantPackageService: TenantPackageService
  ) {
  }


  ngAfterViewInit() {

    this.dataTable.refresh();
  }


}
