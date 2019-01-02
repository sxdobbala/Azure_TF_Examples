import { Body, Controller, Get, HttpCode, HttpStatus, Post } from '@nestjs/common';
import { CrudController } from '../core';
import { ApiOAuth2Auth, ApiOperation, ApiResponse, ApiUseTags } from '@nestjs/swagger';
import { Notification } from './notification.entity';
import { CreateNotificationDto } from './dto/create-notification.dto';
import { NotificationsService } from './notifications.service';

@ApiOAuth2Auth(['read'])
@ApiUseTags('Bdroid', 'Notifications')
@Controller()
export class NotificationsController extends CrudController<Notification> {
  constructor(private readonly notificationsService: NotificationsService) {
    super(notificationsService);
  }

  @ApiOperation({ title: 'Create new record' })
  @ApiResponse({
    status: HttpStatus.CREATED,
    description: 'The record has been successfully created.',
    type: Notification,
  })
  @ApiResponse({
    status: HttpStatus.BAD_REQUEST,
    description: 'Invalid input, The response body may contain clues as to what went wrong',
  })
  @Post()
  async create(@Body() entity: CreateNotificationDto): Promise<Notification> {
    return super.create(entity);
  }
}
