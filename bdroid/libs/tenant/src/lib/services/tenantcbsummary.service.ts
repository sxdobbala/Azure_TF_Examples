import { HttpClient, HttpHeaders, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { environment } from "@env/environment";
import { EntityService } from "@bdroid/shared";
import { TenantPackage } from "../models/tenantpackage.model";
import { TenantChargebackSummary } from "../models/chargebacksummary.model";
import { Observable } from "rxjs";
import { map } from "rxjs/operators";


@Injectable()
export class TenantChargebackSummaryService extends EntityService<TenantChargebackSummary> {
  // Optionally overwrite baseUrl
  public baseUrl = environment.API_BASE_URL;

  readonly entityPath = "tenants/cb/summary";

  constructor(httpClient: HttpClient) {
    super(httpClient);
  }

  getAll(): Observable<TenantChargebackSummary[]> {

    return super.getAll().pipe(
      map(chargeBackData => {
        let oldBasic = 0, oldAnalytics = 0;
        chargeBackData.sort((a,b) => a.dateOfChargeback.localeCompare(b.dateOfChargeback));
        chargeBackData.forEach(data => {
          const newBasic = data.basicUnits, newAnalytics = data.analyticsUnits;

          data.basicVariance = newBasic - oldBasic;
          data.isBasicPositive = (data.basicVariance > 0);
          data.basicPercentage = this.getPercentage(newBasic, oldBasic);
          data.basicVariance = Math.abs(data.basicVariance);
          data.analyticsVariance = newAnalytics - oldAnalytics;
          data.isAnalyticsPositive = (data.analyticsVariance > 0);
          data.analyticsPercentage = this.getPercentage(newAnalytics, oldAnalytics);
          data.analyticsVariance = Math.abs(data.analyticsVariance);
          oldBasic = newBasic;
          oldAnalytics = newAnalytics;
        });
        return chargeBackData;
      }));
  }


  getPercentage(a: number, b: number) {
    return "(" + ((Math.abs(a - b) / Math.max(a, b)) * 100).toFixed(2) + "%)";
  }
}
