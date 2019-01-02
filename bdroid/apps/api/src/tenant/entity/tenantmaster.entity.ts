import { Entity, Column } from "@iaminfinity/express-cassandra";
import { ApiModelPropertyOptional } from "@nestjs/swagger";
import { ArrayUnique, IsArray, IsNotEmpty, IsOptional, IsString } from "class-validator";
import { ServerEntity } from "./server.entity";

@Entity({
  table_name: "tenantmaster",
  key: ["id"]
})
export class TenantMaster {


  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsNotEmpty()
  @Column({ type: "text" })
  active: string;

  @ApiModelPropertyOptional({ type: ServerEntity, isArray: true })
  @IsOptional() @IsArray()
  @Column({ type: "set", typeDef: "<frozen<serverentity>>" })
  applications?: Array<ServerEntity>;

  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsNotEmpty()
  @Column({ type: "text" })
  busAppSearchCode: string;


  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsNotEmpty()
  @Column({ type: "text" })
  businessContact: string;

  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsNotEmpty()
  @Column({ type: "text" })
  businessSponsor: string;


  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsNotEmpty()
  @Column({ type: "text" })
  classification: string;


  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsNotEmpty()
  @Column({ type: "text" })
  clientAcceptance: string;

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
  dataLakeUser: string;

  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsNotEmpty()
  @Column({ type: "text" })
  dataSummary: string;

  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsNotEmpty()
  @Column({ type: "text" })
  description: string;

  @ApiModelPropertyOptional({ type: ServerEntity, isArray: true })
  @IsOptional() @IsArray() @ArrayUnique()
  @Column({ type: "set", typeDef: "<frozen<serverentity>>" })
  edgeNodes?: Array<ServerEntity>;


  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsNotEmpty()
  @Column({ type: "text" })
  environment: string;

  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsNotEmpty()
  @Column({ type: "text" })
  gl: string;


  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsNotEmpty()
  @Column({ type: "text" })
  globalGroup: string;


  @ApiModelPropertyOptional({ type: ServerEntity, isArray: true })
  @IsOptional() @IsArray()
  @Column({ type: "set", typeDef: "<frozen<serverentity>>" })
  hiveServers?: Array<ServerEntity>;


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
  maprfsPath: string;

  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsNotEmpty()
  @Column({ type: "text" })
  primaryGroup: string;

  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsNotEmpty()
  @Column({ type: "text" })
  programName: string;

  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsNotEmpty()
  @Column({ type: "text" })
  projectNumber: string;

  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsNotEmpty()
  @Column({ type: "text" })
  queue: string;

  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsNotEmpty()
  @Column({ type: "text" })
  quota: string;

  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsNotEmpty()
  @Column({ type: "text" })
  rowKey: string;


  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsNotEmpty()
  @Column({ type: "text" })
  segment: string;

  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsNotEmpty()
  @Column({ type: "text" })
  serviceAccount: string;


  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsNotEmpty()
  @Column({ type: "text" })
  serviceDeliveryDate: string;


  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsNotEmpty()
  @Column({ type: "text" })
  serviceRequestNumber: string;


  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsNotEmpty()
  @Column({ type: "text" })
  serviceUnits: string;

  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsNotEmpty()
  @Column({ type: "text" })
  status: string;

  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsNotEmpty()
  @Column({ type: "text" })
  technicalContact: string;

  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsNotEmpty()
  @Column({ type: "text" })
  tenantType: string;

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
  volumeName: string;


}
