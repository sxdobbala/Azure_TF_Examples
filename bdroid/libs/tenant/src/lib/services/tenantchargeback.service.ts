import { HttpClient, HttpHeaders, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { environment } from "@env/environment";
import { EntityService } from "@bdroid/shared";
import { TenantChargeback } from "../models/chargeback.model";
import { catchError, map} from "rxjs/operators";

import { forkJoin, from, Observable, of } from "rxjs";

@Injectable()
export class TenantChargebackService extends EntityService<TenantChargeback> {
  // Optionally overwrite baseUrl
  public baseUrl = environment.API_BASE_URL;

  readonly entityPath = "tenants";
  readonly entityPackagesPath = "/cb/packages";
  readonly entityTenantPackagesPath = "/packages/all";

  constructor(httpClient: HttpClient) {
    super(httpClient);
  }

  getAll(): Observable<any> {
    const urls = [this.entityPath,
      this.entityPath + this.entityPackagesPath,
      this.entityPath + this.entityTenantPackagesPath];
    return this.getDataFromRequest(urls).pipe(
      map((items: [][]) => {
        const tenantPackages = items[2];
        const tenants = items[0];
        const packages = items[1];
        const chargebacks = [];

        for (let idx = 0; idx < tenantPackages.length; idx++) {
          const tPackage: any = tenantPackages[idx];
          if ((tPackage.active === "TRUE") && tPackage.units.valueOf() !== 0) {
            const selectedTenant = tenants.filter((tenant: any) => tenant.rowKey === (tPackage.environment + ":" + tPackage.volume));
            const selectedPackage = packages.filter((packageItem: any) => packageItem.rowKey === (tPackage.environment + ":" + tPackage.packageName));
            if( selectedTenant[0] && selectedPackage[0] ) {
              chargebacks.push(new TenantChargeback().buildModel(tPackage, selectedTenant[0], selectedPackage[0]));
            }
          }
        }
        return chargebacks;
      }));
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
