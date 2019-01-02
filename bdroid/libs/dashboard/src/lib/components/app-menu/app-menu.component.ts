import { ChangeDetectionStrategy, Component, Input, OnDestroy, OnInit } from "@angular/core";
import { Logout } from "@bdroid/auth";
import { Store } from "@ngxs/store";
import { untilDestroy } from "@bdroid/ngx-utils";
import { MenuItem, MenuService } from "@bdroid/navigator";


@Component({
  selector: "ngx-app-menu",
  templateUrl: "./app-menu.component.html",
  styleUrls: ["./app-menu.component.scss"]
})
export class AppMenuComponent implements OnInit, OnDestroy {
  isOpen: boolean;

  @Input()
  title = null;
  @Input()
  name = null;
  @Input()
  icon = null;

  menus: any[];

  constructor(private store: Store, private menuService: MenuService) {
  }

  ngOnInit() {
    this.menuService.items$.pipe(untilDestroy(this)).subscribe((items: MenuItem[]) => {
      this.menus = items.filter(menu => menu.name === this.name)
        .map(item => item.children)[0];
    });
  }

  ngOnDestroy() {
  }

  toggleDropdown() {
    this.isOpen = !this.isOpen;
  }

  onClickOutside() {
    this.isOpen = false;
  }

  public logout() {
    this.store.dispatch(new Logout());
  }
}
