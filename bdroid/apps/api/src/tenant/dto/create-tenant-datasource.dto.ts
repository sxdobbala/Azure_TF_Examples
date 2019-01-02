import { ApiModelPropertyOptional, ApiModelProperty } from "@nestjs/swagger";
import { IsNumber, IsOptional, IsString } from "class-validator";

export class CreateTenantDataSourceDto {


  @ApiModelProperty({ type: String })
  @IsString()
  @IsOptional()
  environment?: string;

  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsOptional()
  comments?: string;
  
  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsOptional()
  dataFrequency?: string;

  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsOptional()
  description?: string;
  
  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsOptional()
  source?: string;
  
  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsOptional()
  sourceType?: string;

  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsOptional()
  subjectArea?: string;

  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsOptional()
  rowKey?: string;


}
