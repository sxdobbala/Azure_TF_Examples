import { async, TestBed } from '@angular/core/testing';
import { TenantModule } from './tenant.module';

describe('TenantModule', () => {
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [TenantModule],
    }).compileComponents();
  }));

  it('should create', () => {
    expect(TenantModule).toBeDefined();
  });
});
