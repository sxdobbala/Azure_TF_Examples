import { Component, OnInit } from '@angular/core';
import { routerTransition } from '@bdroid/animations';

@Component({
  selector: 'ngx-home-layout',
  templateUrl: './home-layout.component.html',
  styleUrls: ['./home-layout.component.scss'],
  animations: [routerTransition],
})
export class HomeLayoutComponent {
  constructor() {}
}
