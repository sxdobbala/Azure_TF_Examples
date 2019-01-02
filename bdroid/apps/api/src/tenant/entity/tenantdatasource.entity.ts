import { Entity, Column } from "@iaminfinity/express-cassandra";
import { ApiModelPropertyOptional } from "@nestjs/swagger";
import { ArrayUnique, IsArray, IsNotEmpty, IsOptional, IsString } from "class-validator";
import { ServerEntity } from "./server.entity";

@Entity({
  table_name: "tenantdatasource",
  key: ["id"]
})
export class TenantDataSource {

  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsNotEmpty()
  @Column({ type: "text" })
  active: string;

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
  dataFrequency: string;

  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsNotEmpty()
  @Column({ type: "text" })
  description: string;

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
  rowKey: string;

  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsNotEmpty()
  @Column({ type: "text" })
  source: string;

  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsNotEmpty()
  @Column({ type: "text" })
  sourceType: string;

  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsNotEmpty()
  @Column({ type: "text" })
  subjectArea: string;

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

}
