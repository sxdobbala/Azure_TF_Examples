import { Injectable } from "@nestjs/common";
import { InjectRepository } from "@iaminfinity/express-cassandra";
import { Observable } from "rxjs";
import { TenantCompute } from "../entity/tenantcompute.entity";
import { TenantComputeRepository } from "../repository/tenantcompute.repository";

@Injectable()
export class TenantComputeService {
  constructor(
    @InjectRepository(TenantComputeRepository)
    private readonly tenantComputeRepository: TenantComputeRepository
  ) {
  }

  findAll(): Observable<TenantCompute[]> {
    return this.tenantComputeRepository.findAll({}, { raw: true, allow_filtering: true });
  }


}
