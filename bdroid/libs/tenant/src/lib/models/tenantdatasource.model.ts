import { Entity } from '@bdroid/shared';

export class TenantDataSource extends Entity {
  public id : 0;
  public environment?: string;
  public sourceType?: string;
  public source?: string;
  public subjectArea?: string
  public dataFrequency?: string;
  public description?: string;
  public comments?: string;
  public active?: string;
  public createdBy?: string;
  public createdDate?: string;
  public updatedBy?: string;
  public updatedDate?: string;
}

