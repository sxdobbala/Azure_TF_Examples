import { Body, Controller, Get, HttpCode, HttpStatus, Param, Post, Put } from "@nestjs/common";
import { ApiOAuth2Auth, ApiOperation, ApiResponse, ApiUseTags } from "@nestjs/swagger";
import { CreateTenantMasterDto } from "./dto/create-tenant.dto";
import { Observable } from "rxjs";
import { TenantPackageService } from "./services/tenantpackage.service";
import { TenantPackage } from "./entity/tenantpackage.entity";
import { SearchTenantPackageDto } from "./dto/search-tenant-package.dto";
import { CreateTenantPackageDto } from "./dto/create-tenant-package.dto";
import { TenantCompute } from "./entity/tenantcompute.entity";
import { TenantComputeService } from "./services/tenantcompute.service";

@ApiOAuth2Auth(["read"])
@ApiUseTags("Bdroid", "Tenant Packages")
@Controller()
export class TenantPackageController {
  constructor(private readonly tenantPackageService: TenantPackageService, private readonly tenantComputeService: TenantComputeService) {

  }

  @ApiOperation({ title: "Find all Tenant Packages" })
  @ApiResponse({ status: HttpStatus.OK, description: "Get All Tenants", type: TenantPackage, isArray: true })
  @Get("/packages/all")
  getAllTenantPackages(): Observable<TenantPackage[]> {
    return this.tenantPackageService.findAll();
  }

  @ApiOperation({ title: "Find all Tenant Resource Allocation" })
  @ApiResponse({ status: HttpStatus.OK, description: "Get All Resource Allocation", type: TenantCompute, isArray: true })
  @Get("/compute/all")
  getAllResourceAllocation(): Observable<TenantCompute[]> {
    return this.tenantComputeService.findAll();
  }

  @ApiOperation({ title: "Find all tenant Packages" })
  @ApiResponse({ status: HttpStatus.OK, description: "All records", type: TenantPackage, isArray: true })
  @Post("/packages/search")
  findAllTenantPackages(@Body() entity: SearchTenantPackageDto): Observable<TenantPackage[]> {
    return this.tenantPackageService.findAllTenantPackages(entity);
  }

  @ApiOperation({ title: "Create new Tenant Package" })
  @ApiResponse({
    status: HttpStatus.CREATED,
    description: "The Tenant Package has been successfully created.",
    type: TenantPackage
  })
  @ApiResponse({
    status: HttpStatus.BAD_REQUEST,
    description: "Invalid input, The response body may contain clues as to what went wrong"
  })
  @Post("/packages/create")
  createPackage(@Body() entity: CreateTenantPackageDto): Observable<TenantPackage> {
    return this.tenantPackageService.create(entity);
  }

  @ApiResponse({
    status: HttpStatus.BAD_REQUEST,
    description: "Invalid input, The response body may contain clues as to what went wrong"
  })
  @Put("/packages/update")
  update(@Body() entity: CreateTenantMasterDto): Observable<TenantPackage> {
    console.log("Creation");
    return this.tenantPackageService.update(entity);
  }

}
