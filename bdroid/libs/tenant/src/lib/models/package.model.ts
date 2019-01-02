import { Entity } from '@bdroid/shared';

export class Package extends Entity {
  public id : 0;
  public active?: string;
  public rowKey?: string;
  public name?: string;
  public features?: string
  public includes?: string;
  public packageShortName?: string;
  public environment?: string;
  public createdBy?: string;
  public createdDate?: string;
  public updatedBy?: string;
  public updatedDate?: string;
  public memory?: string;
  public vcores?: string;
  public storage?: string;
  public chargeCode?: string;
}

