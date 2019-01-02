import { Entity, Column } from "@iaminfinity/express-cassandra";
import { ApiModelProperty } from "@nestjs/swagger";
import { IsNotEmpty, IsString } from "class-validator";

@Entity({
  table_name: "package",
  key: ["id"]
})
export class Package {

  @ApiModelProperty({ type: String })
  @IsString()
  @IsNotEmpty()
  @Column({ type: "text" })
  active: string;

  @ApiModelProperty({ type: String })
  @IsString()
  @IsNotEmpty()
  @Column({ type: "text" })
  chargeCode: string;


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
  environment: string;

  @ApiModelProperty({ type: String })
  @IsString()
  @IsNotEmpty()
  @Column({ type: "text" })
  features: string;

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
  includes: string;

  @ApiModelProperty({ type: String })
  @IsString()
  @IsNotEmpty()
  @Column({ type: "text" })
  memory: string;

  @ApiModelProperty({ type: String })
  @IsString()
  @IsNotEmpty()
  @Column({ type: "text" })
  name: string;

  @ApiModelProperty({ type: String })
  @IsString()
  @IsNotEmpty()
  @Column({ type: "text" })
  packageShortName: string;

  @ApiModelProperty({ type: String })
  @IsString()
  @IsNotEmpty()
  @Column({ type: "text" })
  rowKey: string;

  @ApiModelProperty({ type: String })
  @IsString()
  @IsNotEmpty()
  @Column({ type: "text" })
  storage: string;

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

  @ApiModelProperty({ type: String })
  @IsString()
  @IsNotEmpty()
  @Column({ type: "text" })
  vcores: string;

}
