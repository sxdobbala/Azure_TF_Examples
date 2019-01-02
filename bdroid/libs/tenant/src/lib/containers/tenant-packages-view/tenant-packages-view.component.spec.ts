import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TenantPackagesViewComponent } from './tenant-packages-view.component';

describe('TenantPackagesViewComponent', () => {
  let component: TenantPackagesViewComponent;
  let fixture: ComponentFixture<TenantPackagesViewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TenantPackagesViewComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TenantPackagesViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
