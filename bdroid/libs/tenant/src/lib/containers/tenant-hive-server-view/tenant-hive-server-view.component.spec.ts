import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TenantHiveServerViewComponent } from './tenant-hive-server-view.component';

describe('TenantHiveServerViewComponent', () => {
  let component: TenantHiveServerViewComponent;
  let fixture: ComponentFixture<TenantHiveServerViewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TenantHiveServerViewComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TenantHiveServerViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
