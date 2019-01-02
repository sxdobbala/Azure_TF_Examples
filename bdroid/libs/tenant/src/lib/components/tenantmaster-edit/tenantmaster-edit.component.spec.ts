import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TenantmasterEditComponent } from './tenantmaster-edit.component';

describe('TenantmasterEditComponent', () => {
  let component: TenantmasterEditComponent;
  let fixture: ComponentFixture<TenantmasterEditComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TenantmasterEditComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TenantmasterEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
