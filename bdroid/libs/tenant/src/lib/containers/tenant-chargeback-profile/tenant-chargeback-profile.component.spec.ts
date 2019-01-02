import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TenantChargebackProfileComponent } from './tenant-chargeback-profile.component';

describe('TenantChargebackProfileComponent', () => {
  let component: TenantChargebackProfileComponent;
  let fixture: ComponentFixture<TenantChargebackProfileComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TenantChargebackProfileComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TenantChargebackProfileComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
