import { Repository, EntityRepository } from "@iaminfinity/express-cassandra";
import { Observable } from "rxjs";
import { TenantCompute } from "../entity/tenantcompute.entity";

const models = require("express-cassandra");

@EntityRepository(TenantCompute)
export class TenantComputeRepository extends Repository<TenantCompute> {


  findAll(query: any, options?: any): Observable<TenantCompute[]> {
    return this.find({}, { raw: true, allow_filtering: true });
  }


}
