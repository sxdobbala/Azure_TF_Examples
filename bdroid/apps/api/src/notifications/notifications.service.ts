import { Injectable, NotFoundException } from '@nestjs/common';
import { CrudService } from '../core';
import { InjectRepository } from '@nestjs/typeorm';
import { Notification } from './notification.entity';
import { User } from '../auth';
import { Repository } from 'typeorm';

@Injectable()
export class NotificationsService extends CrudService<Notification> {
  constructor(
    @InjectRepository(Notification) private readonly notificationsRepository: Repository<Notification>,
  ) {
    super(notificationsRepository);
  }

  async onModuleInit() {
  }
  onModuleDestroy() {
  }

  public async getUserNotifications(user: User): Promise<[Notification[], number]> {
    const records = await this.repository.findAndCount({ userId: user.userId });
    if (records[1] === 0) {
      throw new NotFoundException(`The requested records were not found`);
    }
    return records;
  }
}
