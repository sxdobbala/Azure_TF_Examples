import { HttpClient, HttpHeaders, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { environment } from "@env/environment";
import { EntityService } from "@bdroid/shared";
import { TenantPackage } from "../models/tenantpackage.model";
import { TenantDataSource } from "../models/tenantdatasource.model";
import { Observable } from "rxjs";
import { catchError, finalize, retry } from "rxjs/operators";


@Injectable()
export class TenantDataSourceService extends EntityService<TenantDataSource> {
  // Optionally overwrite baseUrl
  public baseUrl = environment.API_BASE_URL;

  readonly entityPath = "tenants/datasources";

  constructor(httpClient: HttpClient) {
    super(httpClient);
  }

  getAll(): Observable<TenantDataSource[]> {
    this.loadingSubject.next(true);
    return this.httpClient.get<TenantDataSource[]>(`${this.baseUrl}/${this.entityPath}/search`).pipe(
      retry(3), // retry a failed request up to 3 times
      catchError(this.handleError),
      finalize(() => this.loadingSubject.next(false)),
    );
  }

}
