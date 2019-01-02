import { Entity, Column } from "@iaminfinity/express-cassandra";
import { ApiModelPropertyOptional } from "@nestjs/swagger";
import { IsNotEmpty, IsNumber, IsString } from "class-validator";


@Entity({
  table_name: "tenantpackage",
  key: ["id"]
})
export class TenantPackage {


  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsNotEmpty()
  @Column({ type: "text" })
  active: string;

  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsNotEmpty()
  @Column({ type: "text" })
  chargebackProgram: string;


  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsNotEmpty()
  @Column({ type: "text" })
  comments: string;


  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsNotEmpty()
  @Column({ type: "text" })
  createdBy: string;

  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsNotEmpty()
  @Column({ type: "text" })
  createdDate: string;


  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsNotEmpty()
  @Column({ type: "text" })
  endDate: string;


  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsNotEmpty()
  @Column({ type: "text" })
  environment: string;


  @ApiModelPropertyOptional({ type: String })
  @IsNotEmpty()
  @Column({
    type: "uuid",
    default: { $db_function: "uuid()" }
  })
  id: any;


  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsNotEmpty()
  @Column({ type: "text" })
  packageName: string;

  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsNotEmpty()
  @Column({ type: "text" })
  program: string;


  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsNotEmpty()
  @Column({ type: "text" })
  rowKey: string;

  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsNotEmpty()
  @Column({ type: "text" })
  startDate: string;

  @ApiModelPropertyOptional({ type: Number })
  @IsNumber()
  @IsNotEmpty()
  @Column({ type: "int" })
  units: number;


  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsNotEmpty()
  @Column({ type: "text" })
  updatedBy: string;

  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsNotEmpty()
  @Column({ type: "text" })
  updatedDate: string;


  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsNotEmpty()
  @Column({ type: "text" })
  volume: string;

}
