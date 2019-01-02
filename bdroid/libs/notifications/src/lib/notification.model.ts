import { Moment } from 'moment';
import { Entity } from '@bdroid/shared';

export class Notification extends Entity {
  public id: string;
  public icon: 'notifications' | 'notifications_active' | 'shopping_basket' | 'eject' | 'cached' | 'code';
  public message: string;
  public createdAt: Moment;
  public read: boolean;
  public color: 'warn' | 'accent' | 'primary';
}
