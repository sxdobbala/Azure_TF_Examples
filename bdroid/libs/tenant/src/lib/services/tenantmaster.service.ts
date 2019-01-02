import { HttpClient, HttpHeaders, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { environment } from "@env/environment";
import { EntityColumnDef, EntityService } from "@bdroid/shared";
import { TenantMaster } from "../models/tenant.model";
import { catchError, finalize, map, retry } from "rxjs/operators";
import { TenantPackage } from "../models/tenantpackage.model";
import { Package } from "../models/package.model";


@Injectable()
export class TenantMasterService extends EntityService<TenantMaster> {
  // Optionally overwrite baseUrl
  public baseUrl = environment.API_BASE_URL;

  readonly entityPath = "tenants";
  readonly entityPackagesSearchPath = this.entityPath + "/packages/search";
  _packages: any[];

  constructor(httpClient: HttpClient) {
    super(httpClient);
    this.getPackages().subscribe(data => {
      this._packages = data;
    });
  }

  async getDetails(row: any) {
    const tenants = await this.getTenants(row);
    const queueNames = [];
    const dataNodes = [];
    const vRows = [];
    const qRows = [];
    let totalUnits = 0;
    let totalMemory = 0;
    let totalVCores = 0;
    tenants.forEach(tenant => {
      queueNames.push(tenant["queue"]);
    });


    const qString = queueNames.filter((v, i, a) => a.indexOf(v) === i).join(",");

    const tenantPackages = await this.getTenantPackageDetails(row);

    const vColumns = new Array<EntityColumnDef<TenantPackage>>();
    const qColumns = new Array<EntityColumnDef<TenantPackage>>();
    vColumns.push(new EntityColumnDef<TenantPackage>({
      property: "volume",
      header: this.convertToHeaderField("Volume"),
      displayFn: (dataEntity) => `${dataEntity["volume"] === null ? "" : dataEntity["volume"]}`
    }));

    vColumns.push(new EntityColumnDef<TenantPackage>({
      property: "units",
      header: this.convertToHeaderField("Storage (TB)"),
      displayFn: (dataEntity) => `${dataEntity["units"] === null ? "" : dataEntity["units"]}`
    }));

    vColumns.push(new EntityColumnDef<TenantPackage>({
      property: "active",
      header: this.convertToHeaderField("Status"),
      displayFn: (dataEntity) => `${dataEntity["active"] === "true" ? "Active" : "InActive"}`
    }));
    qColumns.push(new EntityColumnDef<TenantMaster>({
      property: "queue",
      header: this.convertToHeaderField("Queue"),
      displayFn: (dataEntity) => `${dataEntity["queue"] === null ? "" : dataEntity["queue"]}`
    }));

    qColumns.push(new EntityColumnDef<TenantMaster>({
      property: "memory",
      header: this.convertToHeaderField("Memory (GB)"),
      displayFn: (dataEntity) => `${dataEntity["memory"] === null ? "" : dataEntity["memory"]}`
    }));

    qColumns.push(new EntityColumnDef<TenantMaster>({
      property: "vcores",
      header: this.convertToHeaderField("Vcores"),
      displayFn: (dataEntity) => `${dataEntity["vcores"] === null ? "" : dataEntity["vcores"]}`
    }));

    /* Load the Queue Information */

    tenantPackages.forEach(tenant => {

      const vRow = new TenantPackage();
      vRow.units = tenant.units;
      vRow.active = tenant.active;
      vRow.volume = tenant.volume;

      totalUnits = +totalUnits + +tenant.units;
      vRows.push(vRow);

      const packages = this._packages
        .filter(qPackage => qPackage.packageshortname === tenant.packageName)
        .map(x => x);
      if (packages.length > 0) {
        totalMemory += +tenant.units * +packages[0].memory;
        totalVCores += +tenant.units * +packages[0].vcores;
      }

    });
    const qRow = new TenantMaster();
    qRow.queue = qString;
    qRow.vcores = totalVCores;
    qRow.memory = totalMemory;
    qRows.push(qRow);
    dataNodes.push({
      title: "Volume(s) - Total Storage (" + totalUnits + ") TB",
      rows: vRows,
      columns: vColumns

    });


    dataNodes.push({
      title: "Queue(s) - Total Memory (" + totalMemory + ") GB : Total VCores (" + totalVCores + ")",
      rows: qRows,
      columns: qColumns

    });
    return dataNodes;

  }


  convertToHeaderField(data: string) {
    return data.replace("/([A-Z])/g", " $1").replace(/^./, function(str) {
      return str.toUpperCase();
    });
  }

  async getTenantPackageDetails(row:any) {
    const headers = new HttpHeaders({
      "Content-Type": "application/json",
      Accept: "application/json"
    });
    const data = {
      program: row.programName,
      environment: row.environment
    };
    return await this.httpClient.post<TenantPackage[]>(`${this.baseUrl}/${this.entityPackagesSearchPath}`, data, { headers }).pipe(
      catchError(this.handleError)
    ).toPromise();
  }

  getPackages() {

    return this.httpClient.get<Package[]>(`${this.baseUrl}/${this.entityPath}/cb/packages`).pipe(
      catchError(this.handleError)
    );
  }

  getPackageNames() {
    return this._packages.map(_package => _package.packageshortname);
  }

  getAllPrograms(env: string) {
    const headers = new HttpHeaders({
      "Content-Type": "application/json",
      Accept: "application/json"
    });
    const data = {
      environment: env
    };
    this.loadingSubject.next(true);
    return this.httpClient.post<TenantMaster[]>(`${this.baseUrl}/${this.entityPath}/search`, data, { headers }).pipe(
      retry(3), // retry a failed request up to 3 times
      map(tenants => {
        return tenants.map(entity => entity.programName);
      }),
      catchError(this.handleError),
      finalize(() => this.loadingSubject.next(false))
    );
  }


  getAllVolumes(env: string, program: string) {
    const headers = new HttpHeaders({
      "Content-Type": "application/json",
      Accept: "application/json"
    });
    const data = {
      environment: env,
      programName: program
    };
    this.loadingSubject.next(true);
    return this.httpClient.post<TenantMaster[]>(`${this.baseUrl}/${this.entityPath}/search`, data, { headers }).pipe(
      retry(3), // retry a failed request up to 3 times
      map(tenants => {
        return tenants.map(entity => entity.volumeName);
      }),
      catchError(this.handleError),
      finalize(() => this.loadingSubject.next(false))
    );
  }

  async getTenants(row: any) {
    const headers = new HttpHeaders({
      "Content-Type": "application/json",
      Accept: "application/json"
    });
    const data = {
      programName: row.programName,
      environment: row.environment
    };

    return await this.httpClient.post<TenantMaster[]>(`${this.baseUrl}/${this.entityPath}/search`, data, { headers }).pipe(
      catchError(this.handleError)
    ).toPromise();
  }
}
