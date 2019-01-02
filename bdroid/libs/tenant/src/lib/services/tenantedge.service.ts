import { HttpClient, HttpHeaders, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { environment } from "@env/environment";
import { EntityService } from "@bdroid/shared";
import { TenantEdges } from "../models/edges.model";
import { TenantMaster } from "../models/tenant.model";
import { Observable } from "rxjs";
import { catchError, finalize, map, retry } from "rxjs/operators";


@Injectable()
export class TenantEdgeService extends EntityService<TenantEdges> {
  // Optionally overwrite baseUrl
  public baseUrl = environment.API_BASE_URL;

  readonly entityPath = "tenants";

  constructor(httpClient: HttpClient) {
    super(httpClient);
  }


  getAll(): Observable<TenantEdges[]> {
    this.loadingSubject.next(true);
    return this.httpClient.get<TenantMaster[]>(`${this.baseUrl}/${this.entityPath}`).pipe(
      retry(3), // retry a failed request up to 3 times
      map(res => {
        const tenantEdges = [];
        for (let idx = 0; idx < res.length; idx++) {
          if( res[idx].edgeNodes ) {
            for (let edx = 0; edx < res[idx].edgeNodes.length; edx++) {
              const tenantEdge = new TenantEdges();
              tenantEdge.environment = res[idx].environment;
              tenantEdge.programName = res[idx].programName;
              tenantEdge.hostName = res[idx].edgeNodes[edx]["hostName"];
              tenantEdges.push(tenantEdge);
            }
          }
        }
        return tenantEdges;
      }),
      catchError(this.handleError),
      finalize(() => this.loadingSubject.next(false))
    );
  }
}
