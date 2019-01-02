import { Entity } from "@bdroid/shared";

export class TenantChargebackSummary extends Entity {
  public id: 0;
  public active?: string;
  public rowKey?: string;
  public dateOfChargeback?: string;
  public basicUnits?: number;
  public analyticsUnits?: number;
  public createdBy?: string;
  public createdDate?: string;
  public updatedBy?: string;
  public updatedDate?: string;
  public basicVariance?: number;
  public isBasicPositive?: boolean;
  public basicPercentage?: string;
  public analyticsVariance?: number;
  public isAnalyticsPositive?: boolean;
  public analyticsPercentage?: string;
}

