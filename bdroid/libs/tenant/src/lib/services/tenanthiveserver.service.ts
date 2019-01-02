import { HttpClient, HttpHeaders, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { environment } from "@env/environment";
import { EntityService } from "@bdroid/shared";
import { TenantMaster } from "../models/tenant.model";
import { Observable } from "rxjs";
import { catchError, finalize, map, retry } from "rxjs/operators";
import { TenantHiveServer } from "../models/hiverserver.model";


@Injectable()
export class TenantHiveServerService extends EntityService<TenantHiveServer> {
  // Optionally overwrite baseUrl
  public baseUrl = environment.API_BASE_URL;

  readonly entityPath = "tenants";

  constructor(httpClient: HttpClient) {
    super(httpClient);
  }


  getAll(): Observable<TenantHiveServer[]> {
    this.loadingSubject.next(true);
    return this.httpClient.get<TenantMaster[]>(`${this.baseUrl}/${this.entityPath}`).pipe(
      retry(3), // retry a failed request up to 3 times
      map(res => {
        const hiveServers = [];
        for (let idx = 0; idx < res.length; idx++) {
          if( res[idx].hiveServers ) {
            for (let edx = 0; edx < res[idx].hiveServers.length; edx++) {
              const hiveServer = new TenantHiveServer();
              hiveServer.environment = res[idx].environment;
              hiveServer.programName = res[idx].programName;
              hiveServer.port = res[idx].hiveServers[edx]["port"];
              hiveServer.metaPort = res[idx].hiveServers[edx]["metaPort"];
              hiveServer.hostName = res[idx].hiveServers[edx]["hostName"];
              hiveServers.push(hiveServer);
            }
          }
        }
        return hiveServers;
      }),
      catchError(this.handleError),
      finalize(() => this.loadingSubject.next(false))
    );
  }
}
