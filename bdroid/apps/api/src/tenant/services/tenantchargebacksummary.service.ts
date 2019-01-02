import { Injectable } from "@nestjs/common";
import { InjectRepository } from "@iaminfinity/express-cassandra";
import { Observable } from "rxjs";
import {
  TenantChargebackSummaryRepository
} from "../repository/tenantchargebacksummary.repository";
import { TenantChargebackSummary } from "../entity/cbsummary.entity";

@Injectable()
export class TenantChargebackSummaryService {
  constructor(
    @InjectRepository(TenantChargebackSummaryRepository)
    private readonly tenantChargebackSummaryRepository: TenantChargebackSummaryRepository
  ) {
  }

  findAll(): Observable<TenantChargebackSummary[]> {
    return this.tenantChargebackSummaryRepository.findAll({}, { raw: true, allow_filtering: true });
  }



}
