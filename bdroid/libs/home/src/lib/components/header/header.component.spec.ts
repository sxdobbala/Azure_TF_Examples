import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { HeaderComponent } from './header.component';
import { SharedModule } from '@bdroid/shared';
import { ThemePickerModule } from '@bdroid/theme-picker';
import { RouterTestingModule } from '@angular/router/testing';
import { CoreModule } from '@bdroid/core';

describe('HeaderComponent', () => {
  let component: HeaderComponent;
  let fixture: ComponentFixture<HeaderComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [SharedModule, CoreModule, ThemePickerModule, RouterTestingModule],
      declarations: [HeaderComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(HeaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // TODO: Add tests is logic is added to navbar class.
  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
