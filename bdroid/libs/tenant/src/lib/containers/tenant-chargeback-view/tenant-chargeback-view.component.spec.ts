import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TenantChargebackViewComponent } from './tenant-chargeback-view.component';

describe('TenantChargebackViewComponent', () => {
  let component: TenantChargebackViewComponent;
  let fixture: ComponentFixture<TenantChargebackViewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TenantChargebackViewComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TenantChargebackViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
