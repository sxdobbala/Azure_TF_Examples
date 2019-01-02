import { Component, Inject } from "@angular/core";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material";
import { FormBuilder, Validators } from "@angular/forms";

import { EntityFormComponent } from "@bdroid/shared";

import { TenantDataSource } from "../../models/tenantdatasource.model";
import { EnvironmentType, SourceType } from "../../models/form.model";
import { TenantDataSourceService } from "../../services/tenantdatasource.service";
import * as _moment from "moment";

const moment = _moment;

@Component({
  selector: "ngx-tenant-data-sources-edit",
  templateUrl: "./tenant-data-sources-edit.component.html",
  styleUrls: ["./tenant-data-sources-edit.component.scss"]

})
export class TenantDataSourcesEditComponent extends EntityFormComponent<TenantDataSource> {

  /** form filled data*/

  environmentOptions = Object.keys(EnvironmentType);


  constructor(
    @Inject(MAT_DIALOG_DATA) public data: { title: string; payload: TenantDataSource },
    public dialogRef: MatDialogRef<TenantDataSourcesEditComponent>,
    private fb: FormBuilder,
    public tenantDataSourceService: TenantDataSourceService
  ) {
    super(data, dialogRef);
  }

  getEnvironmentValue(key: string) {
    return EnvironmentType[key];
  }


  getEnumKeyValues() {
    return Object.keys(SourceType).map(key => SourceType[key]);
  }

  buildForm(item: TenantDataSource) {

    this.entityForm = this.fb.group(
      {
        sourceType: [item.sourceType || "", Validators.required],
        environment: [item.environment || "", Validators.required],
        source: [item.source || "", Validators.required],
        subjectArea: [item.subjectArea || "", Validators.required],
        dataFrequency: [item.dataFrequency || "", Validators.required],
        description: [item.description || "", Validators.required],
        comments: [item.comments || ""],
        rowKey: [item.rowKey || ""]

      },
      { updateOn: "blur" }
    );
  }

  submit() {

    this.entityForm.patchValue({
      "rowKey": this.entityForm.get("environment").value + ":" + this.entityForm.get("sourceType").value
        + ":" + this.entityForm.get("source").value
    });
    super.submit();
  }
}
