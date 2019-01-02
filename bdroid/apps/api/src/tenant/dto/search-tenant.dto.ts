import { ApiModelPropertyOptional, ApiModelProperty } from "@nestjs/swagger";
import { IsNotEmpty, IsOptional, IsString } from "class-validator";

export class SearchTenantDto {


  @ApiModelProperty({ type: String })
  @IsString()
  @IsNotEmpty()
  environment?: string;

  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsOptional()
  programName?: string;
}
