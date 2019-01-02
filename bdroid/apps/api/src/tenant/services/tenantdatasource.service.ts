import { Injectable } from "@nestjs/common";
import { InjectRepository } from "@iaminfinity/express-cassandra";
import { Observable } from "rxjs";
import { TenantDataSourceRepository } from "../repository/tenantdatasource.repository";
import { TenantDataSource } from "../entity/tenantdatasource.entity";
import { CreateTenantDataSourceDto } from "../dto/create-tenant-datasource.dto";

@Injectable()
export class TenantDataSourceService {
  constructor(
    @InjectRepository(TenantDataSourceRepository)
    private readonly tenantDataSourceRepository: TenantDataSourceRepository
  ) {
  }

  findAll(): Observable<TenantDataSource[]> {
    return this.tenantDataSourceRepository.findAll({}, { raw: true, allow_filtering: true });
  }


  create(createTenantDataSourceDto: CreateTenantDataSourceDto): Observable<TenantDataSource> {
    return this.tenantDataSourceRepository.save(createTenantDataSourceDto);
  }
}

