import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TenantmasterDetailComponent } from './tenantmaster-detail.component';

describe('TenantmasterDetailComponent', () => {
  let component: TenantmasterDetailComponent;
  let fixture: ComponentFixture<TenantmasterDetailComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TenantmasterDetailComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TenantmasterDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
