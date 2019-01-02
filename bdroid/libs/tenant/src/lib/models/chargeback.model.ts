import { Entity } from "@bdroid/shared";
import { TenantPackage } from "./tenantpackage.model";
import { TenantMaster } from "./tenant.model";
import { Package } from "./package.model";
import * as _moment from "moment";

const moment = _moment;

export class TenantChargeback extends Entity {
  public id: 0;
  public rowKey?: string;
  public active?: string;
  public dateOfChargeback?: string;
  public serviceGroupCostCenter = "151330";
  public internalApplicationName?: string;
  public projectNumber?: string;
  public serviceDeliveryDate?: string;
  public clientAcceptance?: string;
  public gl_bu?: string;
  public gl_ou?: string;
  public gl_loc?: string;
  public gl_acct?: string;
  public gl_dept?: string;
  public gl_prod?: string;
  public gl_cust?: string;
  public gl_pid?: string;
  public billingCode?: string;
  public packageName?: string;
  public servicePhysical?: string;
  public serviceVirtual?: string;
  public serviceStorage = "0";
  public serviceUniqueIdentifier?: string;
  public serviceUnits?: string;
  public serviceRequestNumber?: string;
  public businessApplicationSearchCode?: string;
  public prodnonprod?: string;
  public drmates?: string;
  public drcodes?: string;
  public suiDescription?: string;
  public networkZone?: string;
  public dataCenterCode?: string;
  public environmentCode?: string;
  public createdBy?: string;
  public updatedBy?: string;
  public createdDate?: string;
  public updatedDate?: string;

  buildModel = (tenantPackage: TenantPackage, tenant: TenantMaster, packageInfo: Package) => {
    if (tenant) {
      const glCode = tenant.gl.split("-");
      this.gl_bu = glCode[0];
      this.gl_ou = glCode[1];
      this.gl_loc = glCode[2];
      this.gl_acct = glCode[3];
      this.gl_dept = glCode[4];
      this.gl_prod = glCode[5];
      this.gl_cust = glCode[6];
      this.gl_pid = glCode[7];
      this.clientAcceptance = tenant.clientAcceptance;
      this.serviceDeliveryDate = tenant.serviceDeliveryDate;
      this.projectNumber = tenant.projectNumber;
      this.serviceRequestNumber = tenant.serviceRequestNumber;
      this.businessApplicationSearchCode = tenant.busAppSearchCode;
    }
    this.internalApplicationName = tenantPackage.volume.toUpperCase();
    this.networkZone = "Intranet";
    this.dateOfChargeback = moment().set("date", 23).format("MM/DD/YYYY");
    this.serviceGroupCostCenter = "151330";
    this.billingCode = packageInfo.chargeCode;
    this.packageName = packageInfo.packageShortName;
    this.serviceUnits = tenantPackage.units;
    this.servicePhysical = tenantPackage.environment;
    this.serviceVirtual = tenantPackage.environment;
    this.serviceStorage = "0";
    this.serviceUniqueIdentifier = (this.servicePhysical === "datalake_prod") ? this.internalApplicationName + "_PR" : this.internalApplicationName + "_NP";
    this.suiDescription = (this.servicePhysical === "datalake_prod") ? this.internalApplicationName + "_PROD" : this.internalApplicationName + "_DEV";
    this.prodnonprod = (this.servicePhysical === "datalake_prod") ? "PROD" : "NONPROD";
    this.dataCenterCode = (this.servicePhysical === "datalake_prod") ? "DC-ELR" : "DC-CTC";
    this.environmentCode = (this.servicePhysical === "datalake_prod") ? "EC-PRD" : "EC-DEV";
    this.rowKey = moment().format("YYYYMM") + ":" + this.servicePhysical + ":" + this.internalApplicationName + ":" + this.packageName;
    this.active = "true";
    //this.createdBy = user;
    //this.updatedBy = user;
    this.createdDate = moment().format("MM/DD/YYYY");
    this.updatedDate = moment().format("MM/DD/YYYY");
    return this;
  };

}

