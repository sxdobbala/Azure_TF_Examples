import { Entity, Column } from "@iaminfinity/express-cassandra";
import { ApiModelPropertyOptional } from "@nestjs/swagger";
import { IsNotEmpty, IsNumber, IsString } from "class-validator";


@Entity({
  table_name: "tenantcompute",
  key: ["id"]
})
export class TenantCompute {


  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsNotEmpty()
  @Column({ type: "text" })
  active: string;


  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsNotEmpty()
  @Column({ type: "text" })
  environment: string;

  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsNotEmpty()
  @Column({ type: "text" })
  feedtime: string;


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
  maxVcore: string;

  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsNotEmpty()
  @Column({ type: "text" })
  minVcore: string;

  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsNotEmpty()
  @Column({ type: "text" })
  programName: string;

  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsNotEmpty()
  @Column({ type: "text" })
  queue: string;

  @ApiModelPropertyOptional({ type: Number })
  @IsNumber()
  @IsNotEmpty()
  @Column({ type: "text" })
  rowKey: number;

  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsNotEmpty()
  @Column({ type: "text" })
  volumeName: string;

}
