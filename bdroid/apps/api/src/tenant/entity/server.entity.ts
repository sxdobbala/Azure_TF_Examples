import { ApiModelPropertyOptional } from "@nestjs/swagger";
import { IsOptional, IsString } from "class-validator";

export class ServerEntity {

  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsOptional()
  active: string;

  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsOptional()
  hostName: string;

  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsOptional()
  port: string;

  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsOptional()
  metaPort: string;

  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsOptional()
  homePath: string;

  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsOptional()
  type: string;

  @ApiModelPropertyOptional({ type: String })
  @IsString()
  @IsOptional()
  userId: string;

}
