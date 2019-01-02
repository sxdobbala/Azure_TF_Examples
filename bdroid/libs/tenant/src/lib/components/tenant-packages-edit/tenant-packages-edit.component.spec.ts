import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TenantPackagesEditComponent } from './tenant-packages-edit.component';

describe('TenantPackagesEditComponent', () => {
  let component: TenantPackagesEditComponent;
  let fixture: ComponentFixture<TenantPackagesEditComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TenantPackagesEditComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TenantPackagesEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
