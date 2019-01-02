import { Entity, Column } from "@iaminfinity/express-cassandra";
import { ApiModelProperty } from "@nestjs/swagger";
import { IsNotEmpty, IsString } from "class-validator";

@Entity({
  table_name: "chargebacksummary",
  key: ["id"]
})
export class TenantChargebackSummary {

  @ApiModelProperty({ type: String })
  @IsString()
  @IsNotEmpty()
  @Column({ type: "text" })
  active: string;

  @ApiModelProperty({ type: Number })
  @IsString()
  @IsNotEmpty()
  @Column({ type: "int" })
  analyticsUnits: number;

  @ApiModelProperty({ type: Number })
  @IsString()
  @IsNotEmpty()
  @Column({ type: "int" })
  basicUnits: number;

  @ApiModelProperty({ type: String })
  @IsString()
  @IsNotEmpty()
  @Column({ type: "text" })
  createdBy: string;

  @ApiModelProperty({ type: String })
  @IsString()
  @IsNotEmpty()
  @Column({ type: "text" })
  createdDate: string;

  @ApiModelProperty({ type: String })
  @IsString()
  @IsNotEmpty()
  @Column({ type: "text" })
  dateOfChargeback: string;

  @ApiModelProperty({ type: String })
  @IsNotEmpty()
  @Column({
    type: "uuid",
    default: { $db_function: "uuid()" }
  })
  id: any;

  @ApiModelProperty({ type: String })
  @IsString()
  @IsNotEmpty()
  @Column({ type: "text" })
  rowKey: string;

  @ApiModelProperty({ type: String })
  @IsString()
  @IsNotEmpty()
  @Column({ type: "text" })
  updatedBy: string;

  @ApiModelProperty({ type: String })
  @IsString()
  @IsNotEmpty()
  @Column({ type: "text" })
  updatedDate: string;

}
