import { ApiModelPropertyOptional, ApiModelProperty } from "@nestjs/swagger";
import { IsNotEmpty, IsOptional, IsString } from "class-validator";

export class SearchTenantPackageDto {


  @ApiModelProperty({ type: String })
  @IsString()
  @IsOptional()
  environment?: string;

  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsOptional()
  program?: string;
}
