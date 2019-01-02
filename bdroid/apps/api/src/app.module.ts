import { Module } from '@nestjs/common';
import { RouterModule } from 'nest-router';
import { CoreModule } from './core';
import { AuthModule } from './auth';
import { AppController } from './app.controller';
import { NotificationsModule } from './notifications';
import { TenantModule } from './tenant/tenant.module';


@Module({
  imports: [
    RouterModule.forRoutes([
      {
        path: '/api',
        children: [
          { path: '/auth', module: AuthModule },
          { path: '/notifications', module: NotificationsModule },
          { path: '/tenants', module: TenantModule },
        ],
      },
    ]),
    CoreModule,
    AuthModule,
    TenantModule,
    NotificationsModule
  ],
  controllers: [AppController],
})
export class AppModule {}
