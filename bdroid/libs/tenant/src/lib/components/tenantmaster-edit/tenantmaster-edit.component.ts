import { Component, Inject } from "@angular/core";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material";
import { FormBuilder, Validators } from "@angular/forms";

import { EntityFormComponent } from "@bdroid/shared";

import { TenantMaster } from "../../models/tenant.model";
import { EnvironmentType, StatusType } from "../../models/form.model";

@Component({
  selector: "ngx-tenantmaster-edit",
  templateUrl: "./tenantmaster-edit.component.html",
  styleUrls: ["./tenantmaster-edit.component.scss"]
})
export class TenantMasterEditComponent extends EntityFormComponent<TenantMaster> {

  /** form filled data*/

  environmentOptions = Object.keys(EnvironmentType);
  statusOptions = Object.keys(StatusType);

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: { title: string; payload: TenantMaster },
    public dialogRef: MatDialogRef<TenantMasterEditComponent>,
    private fb: FormBuilder
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

  buildForm(item: TenantMaster) {
    this.entityForm = this.fb.group(
      {
        programName: [item.programName || "", Validators.required],
        environment: [item.environment || "", Validators.required],
        volumeName: [item.volumeName || "", Validators.required],
        serviceAccount: [item.serviceAccount || "", Validators.required],
        primaryGroup: [item.primaryGroup || "", Validators.required],
        maprfsPath: [item.maprfsPath || "", Validators.required],
        queue: [item.queue || "", Validators.required],
        quota: [item.quota || ""],
        globalGroup: [item.globalGroup || ""],
        dataSummary: [item.dataSummary || ""],
        gl: [item.gl || ""],
        status: [item.status || ""],
        businessContact: [item.businessContact || "", Validators.required],
        technicalContact: [item.technicalContact || "", Validators.required],
        businessSponsor: [item.businessSponsor || ""],
        projectNumber: [item.projectNumber || ""],
       // programShortName: [item.programShortName || "", Validators.required],
        segment: [item.segment || "", Validators.required],
        tenantType: [item.tenantType || ""],
        description: [item.description || ""],
        serviceDeliveryDate: [item.serviceDeliveryDate || ""],
        serviceRequestNumber: [item.serviceRequestNumber || ""],
        busAppSearchCode: [item.busAppSearchCode || ""],
        clientAcceptance: [item.clientAcceptance || ""],
        classification: [item.classification || ""],
        dataLakeUser: [item.dataLakeUser || ""],

      },
      { updateOn: "blur" }
    );
  }
}
