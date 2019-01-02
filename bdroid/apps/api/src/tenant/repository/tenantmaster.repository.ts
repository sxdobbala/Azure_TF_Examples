import { Repository, EntityRepository } from "@iaminfinity/express-cassandra";
import { Observable } from "rxjs";
import { TenantMaster } from "../entity/tenantmaster.entity";
import { CreateTenantMasterDto } from "../dto/create-tenant.dto";

const models = require("express-cassandra");

@EntityRepository(TenantMaster)
export class TenantMasterRepository extends Repository<TenantMaster> {


  create(createTenantMasterDto: CreateTenantMasterDto): Observable<TenantMaster> {
    return this.save(createTenantMasterDto);
  }


  edit(createTenantMasterDto: CreateTenantMasterDto): Observable<TenantMaster> {

    console.log( models.datatypes.Uuid.fromString(createTenantMasterDto.id) )
    return this.update({
      programName: createTenantMasterDto.programName,
      environment: createTenantMasterDto.environment
    }, createTenantMasterDto);
  }

  findAll(query: any, options?: any): Observable<TenantMaster[]> {
    return this.find(query, { raw: true, allow_filtering: true });
  }


  findById(id: any): Observable<TenantMaster> {

    return this.findOne({ id: models.datatypes.Uuid.fromString(id) }, { raw: true, allow_filtering: true });
  }
}
