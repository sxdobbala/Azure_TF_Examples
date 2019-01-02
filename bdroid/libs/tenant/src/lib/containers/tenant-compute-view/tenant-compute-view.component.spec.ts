import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TenantResourceAllocationViewComponent } from './tenant-compute-view.component';

describe('TenantResourceAllocationViewComponent', () => {
  let component: TenantResourceAllocationViewComponent;
  let fixture: ComponentFixture<TenantResourceAllocationViewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TenantResourceAllocationViewComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TenantResourceAllocationViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
