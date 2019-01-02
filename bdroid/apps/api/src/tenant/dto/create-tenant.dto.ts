import { ApiModelPropertyOptional } from "@nestjs/swagger";
import { ArrayUnique, IsArray, IsNotEmpty, IsOptional, IsString, ValidateNested } from "class-validator";
import { ServerEntity } from "../entity/server.entity";
import { Type } from "class-transformer";

export class CreateTenantMasterDto {


  @ApiModelPropertyOptional({ type: String })
  @IsOptional() @IsString()
  readonly active: string;


  @ApiModelPropertyOptional({ type: ServerEntity, isArray: true })
  @IsOptional() @IsArray() @ArrayUnique()
  @ValidateNested({ each: true }) @Type(() => ServerEntity)
  readonly applications?: Array<ServerEntity>;


  @ApiModelPropertyOptional({ type: String })
  @IsOptional() @IsString()
  readonly busAppSearchCode: string;

  @ApiModelPropertyOptional({ type: String })
  @IsOptional() @IsString()
  readonly businessContact: string;

  @ApiModelPropertyOptional({ type: String })
  @IsOptional() @IsString()
  readonly businessSponsor: string;

  @ApiModelPropertyOptional({ type: String })
  @IsOptional() @IsString()
  readonly classification: string;

  @ApiModelPropertyOptional({ type: String })
  @IsOptional() @IsString()
  readonly clientAcceptance: string;

  @ApiModelPropertyOptional({ type: String })
  @IsOptional() @IsString()
  readonly createdBy: string;

  @ApiModelPropertyOptional({ type: String })
  @IsOptional() @IsString()
  readonly createdDate: string;

  @ApiModelPropertyOptional({ type: String })
  @IsOptional() @IsString()
  readonly dataLakeUser: string;

  @ApiModelPropertyOptional({ type: String })
  @IsOptional() @IsString()
  readonly dataSummary: string;

  @ApiModelPropertyOptional({ type: String })
  @IsOptional() @IsString()
  readonly description: string;

  @ApiModelPropertyOptional({ type: ServerEntity, isArray: true })
  @IsOptional() @IsArray() @ArrayUnique()
  @ValidateNested({ each: true }) @Type(() => ServerEntity)
  readonly edgeNodes?: Array<ServerEntity>;

  @ApiModelPropertyOptional({ type: String })
  @IsOptional() @IsString()
  readonly environment: string;

  @ApiModelPropertyOptional({ type: String })
  @IsOptional() @IsString()
  readonly gl: string;

  @ApiModelPropertyOptional({ type: String })
  @IsOptional() @IsString()
  readonly globalGroup: string;

  @ApiModelPropertyOptional({ type: ServerEntity, isArray: true })
  @IsOptional() @IsArray() @ArrayUnique()
  @ValidateNested({ each: true }) @Type(() => ServerEntity)
  readonly hiveServers?: Array<ServerEntity>;

  @ApiModelPropertyOptional({ type: String })
  @IsOptional() @IsString()
  readonly maprfsPath: string;

  @ApiModelPropertyOptional({ type: String })
  @IsOptional() @IsString()
  readonly primaryGroup: string;

  @ApiModelPropertyOptional({ type: String })
  @IsOptional() @IsString()
  readonly programName: string;

  @ApiModelPropertyOptional({ type: String })
  @IsOptional() @IsString()
  readonly projectNumber: string;

  @ApiModelPropertyOptional({ type: String })
  @IsOptional() @IsString()
  readonly queue: string;

  @ApiModelPropertyOptional({ type: String })
  @IsOptional() @IsString()
  readonly quota: string;

  @ApiModelPropertyOptional({ type: String })
  @IsOptional() @IsString()
  readonly rowKey: string;

  @ApiModelPropertyOptional({ type: String })
  @IsOptional() @IsString()
  readonly segment: string;

  @ApiModelPropertyOptional({ type: String })
  @IsOptional() @IsString()
  readonly serviceAccount: string;

  @ApiModelPropertyOptional({ type: String })
  @IsOptional() @IsString()
  readonly serviceDeliveryDate: string;

  @ApiModelPropertyOptional({ type: String })
  @IsOptional() @IsString()
  readonly serviceRequestNumber: string;

  @ApiModelPropertyOptional({ type: String })
  @IsOptional() @IsString()
  readonly serviceUnits: string;

  @ApiModelPropertyOptional({ type: String })
  @IsOptional() @IsString()
  readonly status: string;

  @ApiModelPropertyOptional({ type: String })
  @IsOptional() @IsString()
  readonly technicalContact: string;

  @ApiModelPropertyOptional({ type: String })
  @IsOptional() @IsString()
  readonly tenantType: string;

  @ApiModelPropertyOptional({ type: String })
  @IsOptional() @IsString()
  readonly updatedBy: string;

  @ApiModelPropertyOptional({ type: String })
  @IsOptional() @IsString()
  readonly updatedDate: string;

  @ApiModelPropertyOptional({ type: String })
  @IsOptional() @IsString()
  readonly volumeName: string;

  @ApiModelPropertyOptional({ type: Object })
  @IsOptional()
  id: any;

}
