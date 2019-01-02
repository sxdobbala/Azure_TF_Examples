import { AfterViewInit, Component, OnDestroy, OnInit } from "@angular/core";
import { ActivatedRoute } from "@angular/router";
import { fadeAnimation } from "@bdroid/animations";
import { BehaviorSubject, Subscription } from "rxjs";
import { TenantMaster } from "../../models/tenant.model";
import { TenantMasterService } from "../../services/tenantmaster.service";
import { Graph } from "../../models/graph.model";
import * as shape from "d3-shape";
import { colorSets } from "../../models/color.model";
import { Edge } from "../../models/edge.model";
import { GNode } from "../../models/node.model";

const cache = {};

@Component({
  selector: "ngx-tenantmaster-detail",
  templateUrl: "./tenantmaster-detail.component.html",
  styleUrls: ["./tenantmaster-detail.component.scss"],
  animations: [fadeAnimation]
})
export class TenantMasterDetailComponent implements OnInit, OnDestroy, AfterViewInit {

  tenantMaster: TenantMaster;
  sub: Subscription;
  animationTrigger$ = new BehaviorSubject<string>("");


  theme = "dark";
  chartType = "directed-graph";
  chartTypeGroups: any;
  chart: any;
  graph: Graph;

  view: any[];
  width: number;
  height: number;
  fitContainer: boolean;
  autoZoom: boolean;
  panOnZoom: boolean;
  enableZoom: boolean;
  autoCenter: boolean;
  showLegend: false;
  orientation: string;
  layoutId: string;
  curveType: string;
  colorSchemes: any;
  colorScheme: any;
  curve: any = shape.curveCardinal;
  schemeType: string;
  selectedColorScheme: string;
  chartGroups = [
    {
      name: "Other Charts",
      charts: [
        {
          name: "Directed Graph",
          selector: "directed-graph",
          inputFormat: "graph",
          options: ["colorScheme", "showLegend"]
        }
      ]
    }
  ];

  constructor(private tenantMasterService: TenantMasterService, private route: ActivatedRoute) {

  }

  ngOnInit() {

    this.sub = this.route.params.subscribe(params => {
      this.tenantMasterService.getById(params["id"]).subscribe(data => {
        this.animationTrigger$.next(params["id"]);
        this.tenantMaster = data;
        this.initGraph();

      });
    });
  }

  initGraph() {
    this.theme = "dark";
    this.chartType = "directed-graph";

    this.fitContainer = true;
    this.autoZoom = false;
    this.panOnZoom = false;
    this.enableZoom = false;
    this.autoCenter = false;
    this.showLegend = false;
    this.orientation = "BT";
    this.layoutId = "dagre";
    this.curveType = "Cardinal";
    this.curve = shape.curveCardinal;
    this.schemeType = "ordinal";

    Object.assign(this, {
      colorSchemes: colorSets,
      chartTypeGroups: this.chartGroups,
      graph: this.generateGraph(6)
    });

    this.setColorScheme("ocean");
    this.setInterpolationType("Bundle");
    this.selectChart(this.chartType);


  }

  ngAfterViewInit() {

  }

  applyDimensions() {
    this.view = [this.width, this.height];
  }

  id() {
    let newId = ("0000" + (Math.random() * Math.pow(36, 4) << 0).toString(36)).slice(-4);

    // append a 'a' because neo gets mad
    newId = `a${newId}`;

    // ensure not already used
    if (!cache[newId]) {
      cache[newId] = true;
      return newId;
    }

    return this.id();
  }


  selectChart(chartSelector) {
    this.chartType = chartSelector;

    for (const group of this.chartTypeGroups) {
      for (const chart of group.charts) {
        if (chart.selector === chartSelector) {
          this.chart = chart;
          return;
        }
      }
    }
  }

  select(data) {
    console.log("Item clicked", data);
  }

  generateGraph(nodeCount: number): Graph {
    const nodes: GNode[] = [];
    const edges: Edge[] = [];
    const masterNodeId = this.id();
    for (const key in this.tenantMaster) {
      if(key !== "id") {
        nodes.push({
          id: ((key === "programName") ? masterNodeId : this.id()),
          label: key,
          data: {
            value: this.tenantMaster[key]
          }
        });
      }
    }

    for (let j = 0; j < nodes.length; j++) {
      if (nodes[j].label !== "programName") {
        edges.push({
          id: this.id(),
          source: masterNodeId,
          target: nodes[j].id,
          label: nodes[j].label
        });
      }
    }

    return { edges, nodes };
  }

  setColorScheme(name) {
    this.selectedColorScheme = name;
    this.colorScheme = this.colorSchemes.find(s => s.name === name);
  }

  setInterpolationType(curveType) {
    this.curveType = curveType;
    if (curveType === "Bundle") {
      this.curve = shape.curveBundle.beta(1);
    }
    if (curveType === "Cardinal") {
      this.curve = shape.curveCardinal;
    }
    if (curveType === "Catmull Rom") {
      this.curve = shape.curveCatmullRom;
    }
    if (curveType === "Linear") {
      this.curve = shape.curveLinear;
    }
    if (curveType === "Monotone X") {
      this.curve = shape.curveMonotoneX;
    }
    if (curveType === "Monotone Y") {
      this.curve = shape.curveMonotoneY;
    }
    if (curveType === "Natural") {
      this.curve = shape.curveNatural;
    }
    if (curveType === "Step") {
      this.curve = shape.curveStep;
    }
    if (curveType === "Step After") {
      this.curve = shape.curveStepAfter;
    }
    if (curveType === "Step Before") {
      this.curve = shape.curveStepBefore;
    }
  }

  ngOnDestroy() {
    this.sub.unsubscribe();
  }
}

