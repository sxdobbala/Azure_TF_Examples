import { Component, OnInit } from '@angular/core';
import { environment } from '@env/environment';
import { version as appVersion } from '../../../../../../package.json';
import { FormGroup } from "@angular/forms";

@Component({
  selector: 'ngx-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.scss'],
})
export class FooterComponent implements OnInit {
  environment = environment.envName;
  contactForm: FormGroup;

  constructor() {}

  ngOnInit() {}

  get version() {
    if (appVersion) {
      return appVersion;
    }
    return '';
  }
}
