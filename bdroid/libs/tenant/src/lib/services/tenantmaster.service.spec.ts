import { TestBed } from '@angular/core/testing';

import { TenantMasterService } from './tenantmaster.service';

describe('TenantService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: TenantMasterService = TestBed.get(TenantMasterService);
    expect(service).toBeTruthy();
  });
});
