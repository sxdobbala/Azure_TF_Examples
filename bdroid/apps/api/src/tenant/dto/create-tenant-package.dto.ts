import { ApiModelPropertyOptional, ApiModelProperty } from "@nestjs/swagger";
import { IsNumber, IsOptional, IsString } from "class-validator";

export class CreateTenantPackageDto {


  @ApiModelProperty({ type: String })
  @IsString()
  @IsOptional()
  environment?: string;

  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsOptional()
  program?: string;
  
  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsOptional()
  active?: string;

  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsOptional()
  chargebackProgram?: string;
  
  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsOptional()
  comments?: string;
  
  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsOptional()
  createdBy?: string;

  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsOptional()
  createdDate?: string;
  
  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsOptional()
  endDate?: string;

  @ApiModelPropertyOptional({ type: Object })
  @IsOptional()
  id?: any; 

  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsOptional()
  packageName?: string; 

  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsOptional()
  rowKey?: string;

  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsOptional()
  startDate?: string;

  @ApiModelPropertyOptional({ type: Number })
  @IsNumber()
  @IsOptional()
  units?: number;
  
  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsOptional()
  updatedBy?: string;

  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsOptional()
  updatedDate?: string;
  
  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsOptional()
  volume?: string;

}
