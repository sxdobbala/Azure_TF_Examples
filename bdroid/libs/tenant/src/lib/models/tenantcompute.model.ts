import { Entity } from "@bdroid/shared";

export class TenantComputeModel extends Entity {
  public id: 0;
  public active?: string;
  public rowKey?: string;
  public environment?: string;
  public queue?: number;
  public volumeName?: number;
  public programName?: string;
  public minVcore?: string;
  public maxVcore?: string;
  public feedTime?: string;
}

