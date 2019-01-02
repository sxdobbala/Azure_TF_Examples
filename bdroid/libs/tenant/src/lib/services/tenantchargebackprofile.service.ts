import { HttpClient, HttpHeaders, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { environment } from "@env/environment";
import { EntityService } from "@bdroid/shared";
import { TenantChargeback } from "../models/chargeback.model";
import { catchError, filter, map } from "rxjs/operators";
import * as _ from 'underscore';
import { forkJoin, from, Observable, of } from "rxjs";
import { CBChartDataModel } from "../models/cbchart.model";

@Injectable()
export class TenantChargebackProfileService extends EntityService<TenantChargeback> {
  // Optionally overwrite baseUrl
  public baseUrl = environment.API_BASE_URL;

  readonly entityPath = "tenants";
  readonly entityPackagesPath = "/cb/packages";
  readonly entityTenantPackagesPath = "/packages/all";

  constructor(httpClient: HttpClient) {
    super(httpClient);
  }

  getAll(): Observable<any[]> {
    const urls = [
      this.entityPath + this.entityPackagesPath,
      this.entityPath + this.entityTenantPackagesPath];
    return this.getDataFromRequest(urls).pipe(
      map((items: [][]) => {
        const tenantPackages = items[1];
        const packages = items[0];
        const cbChartDataModelArray = [];
        const cbChartDataModel = new CBChartDataModel();

        for (let idx = 0; idx < tenantPackages.length; idx++) {
          const tPackage: any = tenantPackages[idx];
          const selectedPackage: any = packages.filter((packageItem: any) => packageItem.rowKey === (tPackage.environment + ":" + tPackage.packageName));

          tPackage.vcores = +tPackage.units * +selectedPackage[0].vcores;
          tPackage.memory = +tPackage.units * +selectedPackage[0].memory;
        }
        cbChartDataModel.totalUnits = this.sumPackageUnits(tenantPackages);
        cbChartDataModel.prodUnits = this.sumPackageUnits(tenantPackages.filter((tPackage : any) => tPackage.environment === 'datalake_prod'));
        cbChartDataModel.nonProdUnits = this.sumPackageUnits(tenantPackages.filter((tPackage : any) => tPackage.environment === 'datalake_test'));
        cbChartDataModelArray.push(cbChartDataModel);
        return cbChartDataModelArray;
      }));
  }


  sumPackageUnits(tenantPackages: any []){
    const pgmGroups = _.groupBy(tenantPackages, function(tenantPackage){ return tenantPackage.program});
    const results = _.map(pgmGroups, function(group, key){
      return { name: key,
        vcores: _.reduce(group, function(s, x) { return s + +x.vcores;}, 0),
        memory: _.reduce(group, function(s, x) { return s + +x.memory;}, 0)
      };
    });
    return results.sort((a,b)=> b.vcores - a.vcores);
  }

  getDataFromRequest(urls: string[]) {
    return forkJoin(
      urls.map(url => this.getTenantChargebackRequest(url))
    );
  }

  getTenantChargebackRequest(url: string) {
    return this.httpClient.get(`${this.baseUrl}/${url}`).pipe(
      catchError(this.handleError)
    );
  }

}
