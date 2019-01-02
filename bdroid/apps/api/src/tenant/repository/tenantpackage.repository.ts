import { Repository, EntityRepository } from "@iaminfinity/express-cassandra";
import { Observable } from "rxjs";
import { TenantPackage } from "../entity/tenantpackage.entity";
import { CreateTenantPackageDto } from "../dto/create-tenant-package.dto";

const models = require("express-cassandra");

@EntityRepository(TenantPackage)
export class TenantPackageRepository extends Repository<TenantPackage> {


  findAll(query: any, options?: any): Observable<TenantPackage[]> {
    return this.find({}, { raw: true, allow_filtering: true });
  }


  findById(id: any): Observable<TenantPackage> {

    return this.findOne({ id: models.datatypes.Uuid.fromString(id) }, { raw: true, allow_filtering: true });
  }


  edit(createTenantPackageDto: CreateTenantPackageDto): Observable<TenantPackage> {

    console.log(models.datatypes.Uuid.fromString(createTenantPackageDto.id));
    return this.update({}, createTenantPackageDto);
  }

}
