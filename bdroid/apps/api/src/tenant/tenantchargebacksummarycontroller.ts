import { Body, Controller, Get, HttpStatus } from "@nestjs/common";
import { ApiOAuth2Auth, ApiOperation, ApiResponse, ApiUseTags } from "@nestjs/swagger";
import { Observable } from "rxjs";
import { TenantChargebackSummary } from "./entity/cbsummary.entity";
import { TenantChargebackSummaryService } from "./services/tenantchargebacksummary.service";

@ApiOAuth2Auth(["read"])
@ApiUseTags("Bdroid", "Tenant Chargeback")
@Controller()
export class TenantChargebackSummaryController {
  constructor(private readonly tenantChargebackSummaryService: TenantChargebackSummaryService) {

  }

  @ApiOperation({ title: "Find all Tenant Chargeback Summary" })
  @ApiResponse({
    status: HttpStatus.OK,
    description: "Get All Chargeback Summary",
    type: TenantChargebackSummary,
    isArray: true
  })
  @Get("/cb/summary")
  getAllTenantChargebacks(): Observable<TenantChargebackSummary[]> {
    return this.tenantChargebackSummaryService.findAll();
  }

}
