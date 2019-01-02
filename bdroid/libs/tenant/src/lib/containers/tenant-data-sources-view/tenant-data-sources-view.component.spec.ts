import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TenantDataSourcesViewComponent } from './tenant-data-sources-view.component';

describe('TenantDataSourcesViewComponent', () => {
  let component: TenantDataSourcesViewComponent;
  let fixture: ComponentFixture<TenantDataSourcesViewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TenantDataSourcesViewComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TenantDataSourcesViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
