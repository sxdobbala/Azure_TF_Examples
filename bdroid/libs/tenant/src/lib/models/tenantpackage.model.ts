import { Entity } from '@bdroid/shared';

export class TenantPackage extends Entity {
  public id : 0;
  public active?: string;
  public rowKey?: string;
  public volume?: string;
  public chargebackProgram?: string
  public program?: string;
  public packageName?: string;
  public units?: string;
  public environment?: string;
  public startDate?: any;
  public endDate?: any;
  public comments?: string;
  public createdBy?: string;
  public createdDate?: string;
  public updatedBy?: string;
  public updatedDate?: string;

}

