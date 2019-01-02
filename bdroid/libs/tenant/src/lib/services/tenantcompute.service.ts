import { HttpClient, HttpHeaders, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { environment } from "@env/environment";
import { EntityService } from "@bdroid/shared";
import { TenantPackage } from "../models/tenantpackage.model";
import { TenantComputeModel } from "../models/tenantcompute.model";


@Injectable()
export class TenantComputeService extends EntityService<TenantComputeModel> {
  // Optionally overwrite baseUrl
  public baseUrl = environment.API_BASE_URL;

  readonly entityPath = "tenants/compute/all";

  constructor(httpClient: HttpClient) {
    super(httpClient);
  }


}
