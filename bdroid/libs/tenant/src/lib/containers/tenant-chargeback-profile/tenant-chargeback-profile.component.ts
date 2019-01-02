import { Component, OnInit } from "@angular/core";
import { TenantChargebackProfileService } from "../../services/tenantchargebackprofile.service";
import { Crumb } from "@bdroid/breadcrumbs";

@Component({
  selector: "ngx-tenant-chargeback-profile",
  templateUrl: "./tenant-chargeback-profile.component.html",
  styleUrls: ["./tenant-chargeback-profile.component.scss"]
})
export class TenantChargebackProfileComponent implements OnInit {

  crumbs: ReadonlyArray<Crumb> = [
    { name: "Dashboard", link: "/dashboard" },
    { name: "Tenants", link: "/dashboard/tenants" },
    { name: "Tenant Chargeback Profile" }
  ];

  readonly title = "Tenant Chargeback Profile";

  cbChartDataModel: any;

  view: any[];
  chartType = "bar-vertical";
  colorScheme = "cool";
  schemeType = "ordinal";
  vCoreSingle: any[];
  vMemorySingle: any[];
  animations = true;
  showXAxis = true;
  showYAxis = true;
  gradient = false;
  showLegend = true;
  legendTitle = "Programs";
  legendPosition = "right";
  showXAxisLabel = true;
  showYAxisLabel = true;
  tooltipDisabled = false;
  xVCoreAxisLabel = "VCore Units";
  yAxisLabel = "Program Name";
  xMemoryAxisLabel = "Memory Units";
  showGridLines = true;
  barPadding = 8;
  groupPadding = 16;
  roundDomains = false;
  roundEdges = true;
  yScaleMax: number;
  showDataLabel = true;

  width = 700;
  height = 500;
  fitContainer = true;
  selectedEnvironment = "Production";

  // pie
  showLabels = true;
  explodeSlices = true;
  doughnut = false;
  arcWidth = 0.25;
  displayChartType = 'bar';

  constructor(
    public tenantChargebackProfileService: TenantChargebackProfileService
  ) {
  }

  ngOnInit() {

    this.tenantChargebackProfileService.getAll().subscribe(dataModel => {

      this.cbChartDataModel = dataModel[0];


      this.visualizeChart(this.selectedEnvironment);

      if (!this.fitContainer) {
        this.applyDimensions();
      }

    });
  }

  visualizeChart(type: string) {
    this.vCoreSingle = [];
    this.vMemorySingle = [];
    let packageUnits = [];

    if (type === "Production") {
      packageUnits = this.cbChartDataModel.prodUnits;
    } else {
      packageUnits = this.cbChartDataModel.nonProdUnits;
    }
    packageUnits.forEach(packageUnit => {
      this.vCoreSingle.push({
        name: packageUnit.name,
        value: packageUnit.vcores
      });
      this.vMemorySingle.push({
        name: packageUnit.name,
        value: packageUnit.memory
      });
    });
    if (!this.fitContainer) {
      this.applyDimensions();
    }
  }

  select(data) {
    console.log("Item clicked", data);
  }


  onLegendLabelClick(entry) {
    console.log("Legend clicked", entry);
  }

  formatLabel(label: any): string {
    if (label instanceof Date) {
      label = label.toLocaleDateString();
    } else {
      label = label.toLocaleString();
    }

    return label;
  }

  pieTooltipText({ data }) {
    const label = data.name.toLocaleString();
    const val = data.value.toLocaleString();

    return `
      <span class="tooltip-label">${label}</span>
      <span class="tooltip-val">$${val}</span>
    `;
  }

  dblclick(event) {
    console.log('Doube click', event);
  }

  onEnvironmentChange($event: any) {
    this.selectedEnvironment = $event.value;
    this.visualizeChart($event.value);
  }

  onChartType($event: any) {
    this.displayChartType = $event.value;
    this.visualizeChart(this.selectedEnvironment);
  }
  applyDimensions() {
    this.view = [this.width, this.height];
  }

}
