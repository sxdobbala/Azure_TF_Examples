import { Repository, EntityRepository } from "@iaminfinity/express-cassandra";
import { Observable } from "rxjs";
import { TenantChargebackSummary } from "../entity/cbsummary.entity";


@EntityRepository(TenantChargebackSummary)
export class TenantChargebackSummaryRepository extends Repository<TenantChargebackSummary> {


  findAll(query: any, options?: any): Observable<TenantChargebackSummary[]> {
    return this.find({}, { raw: true, allow_filtering: true });
  }


}
