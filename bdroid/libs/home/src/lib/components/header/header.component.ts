import { Component, ElementRef, OnInit, Renderer2, ViewChild } from "@angular/core";
import { Store, Select } from "@ngxs/store";
import { Observable } from "rxjs";
import { Login, Logout } from "@bdroid/auth";
import { AuthState } from "@bdroid/auth";

@Component({
  selector: "ngx-home-header",
  templateUrl: "./header.component.html",
  styleUrls: ["./header.component.scss"]
})
export class HeaderComponent implements OnInit {
  @Select(AuthState.isLoggedIn)
  isLoggedIn$: Observable<boolean>;

  @ViewChild("header")
  header: ElementRef;

  navigation = [
    { link: "#banner", label: "Home", active: "active" },
    { link: "#about", label: "About", active: ""  },
    { link: "#services", label: "Services", active: "" },
    { link: "#features", label: "Features", active: ""  },
    { link: "#contact", label: "Contact", active: ""  }
  ];

  constructor(private store: Store, private renderer: Renderer2) {
  }

  ngOnInit() {
    this.renderer.listen("window", "scroll", event => {
      const number = window.scrollY;
      if (number > 600) {
        // add logic
        this.header.nativeElement.classList.remove("alt");
        this.header.nativeElement.classList.add("navbar-background");
      } else {
        // remove logic
        this.header.nativeElement.classList.remove("navbar-background");
        this.header.nativeElement.classList.add("alt");
      }
    });
  }

  public login() {
    this.store.dispatch(new Login());
  }

  public logout() {
    this.store.dispatch(new Logout());
  }

  public signup() {
  }
}
