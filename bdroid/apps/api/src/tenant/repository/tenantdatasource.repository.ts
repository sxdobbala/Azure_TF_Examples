import { Repository, EntityRepository } from "@iaminfinity/express-cassandra";
import { Observable } from "rxjs";
import { TenantDataSource } from "../entity/tenantdatasource.entity";
import { CreateTenantDataSourceDto } from "../dto/create-tenant-datasource.dto";

@EntityRepository(TenantDataSource)
export class TenantDataSourceRepository extends Repository<TenantDataSource> {


  findAll(query: any, options?: any): Observable<TenantDataSource[]> {
    return this.find({}, { raw: true, allow_filtering: true });
  }

  create(createTenantDataSourceDto: CreateTenantDataSourceDto): Observable<TenantDataSource> {
    return this.save(createTenantDataSourceDto);
  }


}
