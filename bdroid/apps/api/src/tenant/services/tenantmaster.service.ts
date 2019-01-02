import { Injectable } from "@nestjs/common";
import { TenantMaster } from "../entity/tenantmaster.entity";
import { InjectRepository } from "@iaminfinity/express-cassandra";
import { CreateTenantMasterDto } from "../dto/create-tenant.dto";
import { Observable } from "rxjs";
import { TenantMasterRepository } from "../repository/tenantmaster.repository";
import { SearchTenantDto } from "../dto/search-tenant.dto";

@Injectable()
export class TenantMasterService {
  constructor(
    @InjectRepository(TenantMasterRepository)
    private readonly tenantMasterRepository: TenantMasterRepository
  ) {
  }

  create(createTenantMasterDto: CreateTenantMasterDto): Observable<TenantMaster> {
    return this.tenantMasterRepository.save(createTenantMasterDto);
  }


  update(createTenantMasterDto: CreateTenantMasterDto): Observable<TenantMaster> {
    return this.tenantMasterRepository.edit(createTenantMasterDto);
  }

  findAll(): Observable<TenantMaster[]> {
    return this.tenantMasterRepository.findAll({}, { raw: true, allow_filtering: true });
  }


  findTenants(filter: SearchTenantDto): Observable<TenantMaster[]> {
    return this.tenantMasterRepository.findAll(filter, { raw: true, allow_filtering: true });
  }


  getById(id: any): Observable<TenantMaster> {
    return this.tenantMasterRepository.findById(id);
  }

}
