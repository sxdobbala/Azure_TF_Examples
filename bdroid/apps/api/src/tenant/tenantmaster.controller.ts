import { Body, Controller, Get, HttpCode, HttpStatus, Param, Post, Put } from "@nestjs/common";
import { ApiOAuth2Auth, ApiOperation, ApiResponse, ApiUseTags } from "@nestjs/swagger";
import { TenantMaster } from "./entity/tenantmaster.entity";
import { TenantMasterService } from "./services/tenantmaster.service";
import { CreateTenantMasterDto } from "./dto/create-tenant.dto";
import { PackageService } from "./services/package.service";
import { Package } from "./entity/package.entity";
import { Observable } from "rxjs";
import { SearchTenantDto } from "./dto/search-tenant.dto";
import { TenantDataSource } from "./entity/tenantdatasource.entity";
import { TenantDataSourceService } from "./services/tenantdatasource.service";
import { CreateTenantDataSourceDto } from "./dto/create-tenant-datasource.dto";

@ApiOAuth2Auth(["read"])
@ApiUseTags("Bdroid", "Tenants")
@Controller()
export class TenantMasterController {
  constructor(private readonly tenantService: TenantMasterService,
              private readonly tenantDataSourceService: TenantDataSourceService,
              private readonly packageService: PackageService) {

  }

  @ApiOperation({ title: "Find all TenantMaster" })
  @ApiResponse({ status: HttpStatus.OK, description: "Get All Tenants", type: TenantMaster, isArray: true })
  @Get()
  getAllTenants(): Observable<TenantMaster[]> {
    return this.tenantService.findAll();
  }

  @ApiOperation({ title: "Find by id" })
  @ApiResponse({ status: HttpStatus.OK, description: "Found one record", type: TenantMaster })
  @ApiResponse({ status: HttpStatus.NOT_FOUND, description: "Record not found" })
  @Get(":id")
  findOne(@Param("id") id: string): Observable<TenantMaster> {
    return this.tenantService.getById(id);
  }

  @ApiOperation({ title: "Find all packages" })
  @ApiResponse({ status: HttpStatus.OK, description: "Get All Packages", type: Package, isArray: true })
  @Get("/cb/packages")
  findAllPackages(): Observable<Package[]> {
    return this.packageService.findAll();
  }

  @ApiOperation({ title: "Find all packages" })
  @ApiResponse({ status: HttpStatus.OK, description: "Get All Packages", type: Package, isArray: true })
  @Get("/datasources/search")
  findAllDataSources(): Observable<TenantDataSource[]> {
    return this.tenantDataSourceService.findAll();
  }

  @ApiOperation({ title: "Find all tenants" })
  @ApiResponse({ status: HttpStatus.OK, description: "All records", type: TenantMaster, isArray: true })
  @Post("/search")
  findTenants(@Body() entity: SearchTenantDto): Observable<TenantMaster[]> {
    return this.tenantService.findTenants(entity);
  }

  @ApiOperation({ title: "Create new Tenant" })
  @ApiResponse({
    status: HttpStatus.CREATED,
    description: "The Tenant has been successfully created.",
    type: TenantMaster
  })
  @ApiResponse({
    status: HttpStatus.BAD_REQUEST,
    description: "Invalid input, The response body may contain clues as to what went wrong"
  })
  @Post("/create")
  create(@Body() entity: CreateTenantMasterDto): Observable<TenantMaster> {
    return this.tenantService.create(entity);
  }

  @ApiOperation({ title: "Create new Tenant Data Source" })
  @ApiResponse({
    status: HttpStatus.CREATED,
    description: "The Tenant Data Source has been successfully created.",
    type: TenantDataSource
  })
  @ApiResponse({
    status: HttpStatus.BAD_REQUEST,
    description: "Invalid input, The response body may contain clues as to what went wrong"
  })
  @Post("/datasources/create")
  createTenantDataSource(@Body() entity: CreateTenantDataSourceDto): Observable<TenantDataSource> {
    return this.tenantDataSourceService.create(entity);
  }

  @ApiResponse({
    status: HttpStatus.BAD_REQUEST,
    description: "Invalid input, The response body may contain clues as to what went wrong"
  })
  @Put("/update")
  update(@Body() entity: CreateTenantMasterDto): Observable<TenantMaster> {
    console.log("Creation");
    return this.tenantService.update(entity);
  }

}
