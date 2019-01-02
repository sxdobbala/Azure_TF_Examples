import { HttpClient, HttpHeaders, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { environment } from "@env/environment";
import { EntityService } from "@bdroid/shared";
import { TenantPackage } from "../models/tenantpackage.model";


@Injectable()
export class TenantPackageService extends EntityService<TenantPackage> {
  // Optionally overwrite baseUrl
  public baseUrl = environment.API_BASE_URL;

  readonly entityPath = "tenants/packages/all";

  constructor(httpClient: HttpClient) {
    super(httpClient);
  }


}
