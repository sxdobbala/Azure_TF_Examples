import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TenantChargebackSummaryViewComponent } from './tenant-chargeback-summary-view.component';

describe('TenantChargebackSummaryViewComponent', () => {
  let component: TenantChargebackSummaryViewComponent;
  let fixture: ComponentFixture<TenantChargebackSummaryViewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TenantChargebackSummaryViewComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TenantChargebackSummaryViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
