import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TenantMasterViewComponent } from './tenant-master-view.component';

describe('TenantMasterViewComponent', () => {
  let component: TenantMasterViewComponent;
  let fixture: ComponentFixture<TenantMasterViewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TenantMasterViewComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TenantMasterViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
