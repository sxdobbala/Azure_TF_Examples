import { Component, Inject, LOCALE_ID } from "@angular/core";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material";
import { FormBuilder, Validators } from "@angular/forms";

import { EntityFormComponent } from "@bdroid/shared";

import { TenantPackage } from "../../models/tenantpackage.model";
import { EnvironmentType } from "../../models/form.model";
import { TenantMasterService } from "../../services/tenantmaster.service";

import { MAT_MOMENT_DATE_FORMATS, MomentDateAdapter } from "@angular/material-moment-adapter";
import { DateAdapter, MAT_DATE_FORMATS, MAT_DATE_LOCALE } from "@angular/material/core";
import * as _moment from "moment";

const moment = _moment;

@Component({
  selector: "ngx-tenant-packages-edit",
  templateUrl: "./tenant-packages-edit.component.html",
  styleUrls: ["./tenant-packages-edit.component.scss"],
  providers: [
    { provide: DateAdapter, useClass: MomentDateAdapter, deps: [MAT_DATE_LOCALE] },
    { provide: MAT_DATE_FORMATS, useValue: MAT_MOMENT_DATE_FORMATS },

  ]
})
export class TenantPackagesEditComponent extends EntityFormComponent<TenantPackage> {

  /** form filled data*/

  environmentOptions = Object.keys(EnvironmentType);
  programs: string[];
  volumes: string[];
  packages: string[];

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: { title: string; payload: TenantPackage },
    public dialogRef: MatDialogRef<TenantPackagesEditComponent>,
    private fb: FormBuilder,
    public tenantMasterService: TenantMasterService
  ) {
    super(data, dialogRef);
  }

  /* Optional */

  // tslint:disable-next-line
  ngOnInit() {
    super.ngOnInit();
  }

  private filterStates(name: string) {
    //  return this.states.filter(state => state.toLowerCase().indexOf(name.toLowerCase()) === 0);
  }

  loadProgramNames($event: any) {
    this.tenantMasterService.getAllPrograms($event.value).subscribe(data => {
      this.programs = data;
    });
  }

  loadVolumes(env: string, program: string) {

    this.tenantMasterService.getAllVolumes(env || this.entityForm.get("environment").value, program || this.entityForm.get("program").value)
      .subscribe(data => {
        this.volumes = data;
        this.loadPackages();
      });
  }

  loadPackages() {
    this.packages = this.tenantMasterService.getPackageNames();
  }

  getEnumKeyFromValue(val: string) {
    return Object.keys(EnvironmentType).filter(key => EnvironmentType[key] === val);
  }

  getEnvironmentValue(key: string){
    return EnvironmentType[key];
  }

  buildForm(item: TenantPackage) {

    if (this.title.startsWith("Update")) {
      this.loadProgramNames({ value: item.environment });
      this.loadVolumes(item.environment, item.program);
    }
    this.entityForm = this.fb.group(
      {
        program: [item.program || "", Validators.required],
        environment: [item.environment || "", Validators.required],
        volume: [item.volume || "", Validators.required],
        chargebackProgram: [item.chargebackProgram || "", Validators.required],
        packageName: [item.packageName || "", Validators.required],
        units: [item.units || "", Validators.required],
        startDate: [moment(item.startDate,"MM/DD/YY") || "", Validators.required],
        endDate: [moment(item.endDate,"MM/DD/YY") || "", Validators.required],
        comments: [item.comments || ""]

      },
      { updateOn: "blur" }
    );
  }

  submit() {
    this.entityForm.patchValue({ "startDate": moment(this.entityForm.get("startDate").value).format("MM/DD/YY") });
    this.entityForm.patchValue({ "endDate": moment(this.entityForm.get("endDate").value).format("MM/DD/YY") });
    super.submit();
  }
}
