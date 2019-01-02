import { MiddlewareConsumer, Module, NestModule } from "@nestjs/common";
import { ConfigModule, ConfigService } from "../config";
import { TypeOrmModule } from "@nestjs/typeorm";
import { APP_INTERCEPTOR } from "@nestjs/core";
import { TransformInterceptor } from "./interceptors";
import { RequestContextMiddleware } from "./context";
import {
  ExpressCassandraModule
} from "@iaminfinity/express-cassandra";

@Module({
  imports: [
    ConfigModule.forRoot(),
    ExpressCassandraModule.forRoot({

      clientOptions: {
        contactPoints: [process.env.TYPE_CASSANDRAORM_HOST],
        keyspace: process.env.TYPE_CASSANDRAORM_DATABASE,
        protocolOptions: {
          port: Number(process.env.TYPE_CASSANDRAORM_PORT)
        },
        queryOptions: {
          consistency: 1
        }
      },
      ormOptions: {
        createKeyspace: false,
        createTable: true,
        udts: {
         serverentity: {
           active : "text",
           "hostName" : "text",
           port : "text",
           "metaPort" : "text",
           "homePath" : "text",
           "userId" : "text",
           type : "text"
          }
        },
        defaultReplicationStrategy: {
          class: "SimpleStrategy",
          replication_factor: 1
        },
        migration: "safe"
      },
      retryAttempts: 1,
      retryDelay: 1000

    }),
    TypeOrmModule.forRoot({
      type: "postgres",
      host: process.env.TYPEORM_HOST,
      port: Number(process.env.TYPEORM_PORT),
      database: process.env.TYPEORM_DATABASE,
      username: process.env.TYPEORM_USERNAME,
      password: process.env.TYPEORM_PASSWORD,
      entities: [process.env.TYPEORM_ENTITIES],
      keepConnectionAlive: true,
      logging: process.env.TYPEORM_LOGGING ? JSON.parse(process.env.TYPEORM_LOGGING) : false,
      synchronize: process.env.TYPEORM_SYNCHRONIZE ? JSON.parse(process.env.TYPEORM_SYNCHRONIZE) : false
    })
  ],
  controllers: [],
  providers: [
    // Enable for debugging in Dev env.
    // {
    //   provide: APP_INTERCEPTOR,
    //   useClass: LoggingInterceptor,
    // },
    {
      provide: APP_INTERCEPTOR,
      useClass: TransformInterceptor
    }
  ]
})
export class CoreModule implements NestModule {
  configure(consumer: MiddlewareConsumer) {
    consumer.apply(RequestContextMiddleware).forRoutes("*");
  }
}
