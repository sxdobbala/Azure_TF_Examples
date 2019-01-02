import { Module } from "@nestjs/common";
import { TenantMasterController } from "./tenantmaster.controller";
import { TenantMasterService } from "./services/tenantmaster.service";
import { SharedModule } from "../shared";
import { TenantMaster } from "./entity/tenantmaster.entity";
import { PackageService } from "./services/package.service";
import { Package } from "./entity/package.entity";
import {
  ExpressCassandraModule
} from "@iaminfinity/express-cassandra";
import { TenantMasterRepository } from "./repository/tenantmaster.repository";
import { TenantPackageRepository } from "./repository/tenantpackage.repository";
import { TenantPackage } from "./entity/tenantpackage.entity";
import { TenantPackageService } from "./services/tenantpackage.service";
import { PackageRepository } from "./repository/package.repository";
import { TenantPackageController } from "./tenantpackage.controller";
import { TenantChargebackSummaryController } from "./tenantchargebacksummarycontroller";
import { TenantChargebackSummaryService } from "./services/tenantchargebacksummary.service";
import { TenantChargebackSummaryRepository } from "./repository/tenantchargebacksummary.repository";
import { TenantChargebackSummary } from "./entity/cbsummary.entity";
import { TenantDataSourceRepository } from "./repository/tenantdatasource.repository";
import { TenantDataSourceService } from "./services/tenantdatasource.service";
import { TenantDataSource } from "./entity/tenantdatasource.entity";
import { TenantCompute } from "./entity/tenantcompute.entity";
import { TenantComputeRepository } from "./repository/tenantcompute.repository";
import { TenantComputeService } from "./services/tenantcompute.service";

@Module({
  imports: [SharedModule, ExpressCassandraModule.forFeature([Package, TenantCompute, TenantDataSource, TenantMaster, TenantPackage, TenantChargebackSummary, PackageRepository,
    TenantMasterRepository, TenantChargebackSummaryRepository, TenantDataSourceRepository, TenantPackageRepository, TenantComputeRepository])],
  controllers: [TenantMasterController, TenantPackageController, TenantChargebackSummaryController],
  providers: [TenantMasterService, TenantPackageService, PackageService, TenantDataSourceService, TenantComputeService, TenantChargebackSummaryService]
})
export class TenantModule {
}
