import { Injectable } from "@nestjs/common";
import { InjectRepository } from "@iaminfinity/express-cassandra";
import { Observable } from "rxjs";
import { TenantPackageRepository } from "../repository/tenantpackage.repository";
import { TenantPackage } from "../entity/tenantpackage.entity";
import { SearchTenantPackageDto } from "../dto/search-tenant-package.dto";
import { CreateTenantPackageDto } from "../dto/create-tenant-package.dto";

@Injectable()
export class TenantPackageService {
  constructor(
    @InjectRepository(TenantPackageRepository)
    private readonly tenantPackageRepository: TenantPackageRepository
  ) {
  }

  findAll(): Observable<TenantPackage[]> {
    return this.tenantPackageRepository.findAll({}, { raw: true, allow_filtering: true });
  }

  create(createTenantPackageDto: CreateTenantPackageDto): Observable<TenantPackage> {
    return this.tenantPackageRepository.save(createTenantPackageDto);
  }


  update(createTenantPackageDto: CreateTenantPackageDto): Observable<TenantPackage> {
    return this.tenantPackageRepository.edit(createTenantPackageDto);
  }

  findAllTenantPackages(filter: SearchTenantPackageDto): Observable<TenantPackage[]> {
    if (Object.keys(filter).length !== 0) {
      return this.tenantPackageRepository.find({ program: filter.program, environment: filter.environment }, {
        raw: true,
        allow_filtering: true
      });
    }
    return this.findAll();

  }

}
