import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MomentModule } from 'ngx-moment';
import { FormlyMaterialModule } from '@ngx-formly/material';
import { MaterialModule } from '@bdroid/material';
import { MaterialDateModule } from '@bdroid/material';
import { BreadcrumbsModule } from '@bdroid/breadcrumbs';
import { ReactiveFormsModule } from '@angular/forms';
import { FlexLayoutModule, LAYOUT_CONFIG } from '@angular/flex-layout';
import { MinValidatorDirective } from './directives/min/min.directive';
import { ClickOutsideDirective } from './directives/click-outside/click-outside.directive';
import {
  PerfectScrollbarModule,
  PerfectScrollbarConfigInterface,
  PERFECT_SCROLLBAR_CONFIG,
} from 'ngx-perfect-scrollbar';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgLetModule, RouterLinkMatchModule } from '@bdroid/ngx-utils';
import { NgxDataTreeTableComponent } from "./components/ngx-data-tree-table/ngx-data-detail-table.component";
import { MatIconModule, MatInputModule, MatTreeModule } from "@angular/material";

const DEFAULT_PERFECT_SCROLLBAR_CONFIG: PerfectScrollbarConfigInterface = {
  suppressScrollX: true,
  wheelPropagation: true,
};

const DIRECTIVES = [MinValidatorDirective, ClickOutsideDirective, NgxDataTreeTableComponent];

@NgModule({
  imports: [CommonModule,  MatTreeModule, MatIconModule, FlexLayoutModule.withConfig({ useColumnBasisZero: false })],
  declarations: [...DIRECTIVES],
  exports: [
    CommonModule,
    ReactiveFormsModule,
    FlexLayoutModule,
    BreadcrumbsModule,
    MaterialModule,
    MaterialDateModule,
    MomentModule,
    NgLetModule,
    RouterLinkMatchModule,
    FontAwesomeModule,
    FormlyMaterialModule,
    PerfectScrollbarModule,
    MatIconModule,
    MatTreeModule,
    ...DIRECTIVES,
  ],
  providers: [{ provide: PERFECT_SCROLLBAR_CONFIG, useValue: DEFAULT_PERFECT_SCROLLBAR_CONFIG }],
})
export class SharedModule {}
