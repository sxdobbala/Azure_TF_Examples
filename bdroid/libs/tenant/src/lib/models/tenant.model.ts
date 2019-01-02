import { Entity } from '@bdroid/shared';

export class TenantMaster extends Entity {
  public id = 0;
  public programName?: string;
  public volumeName?: string;
  public active?: string;
  public rowKey?: string;
  public serviceAccount?: string;
  public serviceUnits?: string;
  public globalGroup?: string;
  public primaryGroup?: string;
  public maprfsPath?: string;
  public queue?: string;
  public quota?: string;
  public tenantType?: string;
  public businessSponsor?: string;
  public businessContact?: string;
  public technicalContact?: string;
  public createdBy?: string;
  public updatedBy?: string;
  public createdDate?: string;
  public updatedDate?: string;
  public description?: string;
  public classification?: string;
  public environment?: string;
  public dataSummary?: string;
  public serviceDeliveryDate?: string;
  public clientAcceptance?: string;
  public serviceRequestNumber?: string;
  public busAppSearchCode?: string;
  public segment?: string;
  public status?: string;
  public hiveServers?: string;
  public edgeNodes?: string;
  public applications?: string;
  public vcores?:number;
  public memory?:number;
}

