import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TenantedgeViewComponent } from './tenantedge-view.component';

describe('TenantedgeViewComponent', () => {
  let component: TenantedgeViewComponent;
  let fixture: ComponentFixture<TenantedgeViewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TenantedgeViewComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TenantedgeViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
