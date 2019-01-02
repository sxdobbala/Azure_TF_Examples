import { AfterViewInit, Component, ViewChild } from "@angular/core";
import { Crumb } from "@bdroid/breadcrumbs";

import { NgxDataTableComponent } from "../../components/ngx-data-table/ngx-data-table.component";
import { TenantDataSourcesEditComponent } from '../../components/tenant-data-sources-edit/tenant-data-sources-edit.component';
import { Entity, EntityColumnDef } from "@bdroid/shared";
import { TenantDataSourceService } from "../../services/tenantdatasource.service";
import { TenantDataSource } from "../../models/tenantdatasource.model";

// TODO: search with facets https://github.com/sfeir-open-source/angular-search-experience
// https://ngx.tools/#/search?q=Go
@Component({
  selector: 'ngx-tenant-data-sources-view',
  templateUrl: './tenant-data-sources-view.component.html',
  styleUrls: ["../../../../../shared/src/lib/containers/entity/entity.component.scss"]
})
export class TenantDataSourcesViewComponent implements AfterViewInit {
  crumbs: ReadonlyArray<Crumb> = [
    { name: "Dashboard", link: "/dashboard" },
    { name: "Tenant", link: "/dashboard/tenants" },
    { name: "Data Sources" }
  ];

  readonly title = "Data Sources";

  readonly dialogComponent = TenantDataSourcesEditComponent;
  dialogEntity = new TenantDataSource();
  data: any[] = [];


  defaultColumns: any[] = [
    new EntityColumnDef<Entity>({
      property: "environment"
    }),
    new EntityColumnDef<Entity>({
      property: "sourceType"
    }),
    new EntityColumnDef<Entity>({
      property: "source"
    }),
    new EntityColumnDef<Entity>({
      property: "subjectArea"
    }),
    new EntityColumnDef<Entity>({
      property: "dataFrequency"
    }),
    new EntityColumnDef<Entity>({
      property: "description"
    }),
    new EntityColumnDef<Entity>({
      property: "comments"
    })];

  @ViewChild(NgxDataTableComponent) dataTable: NgxDataTableComponent;

  constructor(
    public tenantDataSourceService: TenantDataSourceService
  ) {
  }


  ngAfterViewInit() {

    this.dataTable.refresh();
  }


}

