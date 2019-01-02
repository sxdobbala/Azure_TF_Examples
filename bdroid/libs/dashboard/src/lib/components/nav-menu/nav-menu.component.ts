import { Component, OnDestroy, OnInit } from "@angular/core";
import { untilDestroy } from "@bdroid/ngx-utils";
import { MenuItem, MenuService } from "@bdroid/navigator";

@Component({
  selector: 'ngx-nav-menu',
  templateUrl: './nav-menu.component.html',
  styleUrls: ['./nav-menu.component.scss']
})
export class NavMenuComponent implements OnInit, OnDestroy {



  menus = [
    {
      title: "Tenant Management",
      icon: "group",
      name: 'Tenant'
    },
    {
      title: "Tenant Chargeback",
      icon: "wb_incandescent",
      name: 'Chargeback'
    } ];

  constructor(private menuService: MenuService) {
  }

  ngOnInit() {

  }

  ngOnDestroy() {
  }

}
