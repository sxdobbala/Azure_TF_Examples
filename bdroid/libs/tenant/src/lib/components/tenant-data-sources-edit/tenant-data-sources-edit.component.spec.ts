import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TenantDataSourcesEditComponent } from './tenant-data-sources-edit.component';

describe('TenantDataSourcesEditComponent', () => {
  let component: TenantDataSourcesEditComponent;
  let fixture: ComponentFixture<TenantDataSourcesEditComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TenantDataSourcesEditComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TenantDataSourcesEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
