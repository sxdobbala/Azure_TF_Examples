import { Test, TestingModule } from '@nestjs/testing';
import { TenantmasterController } from './tenantmaster.controller';

describe('Tenant Controller', () => {
  let module: TestingModule;
  
  beforeAll(async () => {
    module = await Test.createTestingModule({
      controllers: [TenantmasterController],
    }).compile();
  });
  it('should be defined', () => {
    const controller: TenantmasterController = module.get<TenantmasterController>(TenantmasterController);
    expect(controller).toBeDefined();
  });
});
