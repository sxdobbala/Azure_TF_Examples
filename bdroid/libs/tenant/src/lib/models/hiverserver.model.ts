import { Entity } from '@bdroid/shared';

export class TenantHiveServer extends Entity {
  public id : 0;
  public environment?: string;
  public programName?: string;
  public hostName?: string;
  public port?: string;
  public metaPort?: string;
}

