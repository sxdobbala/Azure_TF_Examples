import { Component, Input, ViewChild } from "@angular/core";
import {
  trigger,
  state,
  style,
  transition,
  animate
} from "@angular/animations";
import { Entity } from "@bdroid/shared";
import { MatDialog, MatSnackBar, MatTableDataSource } from "@angular/material";
import { Store } from "@ngxs/store";

@Component({
  selector: "ngx-data-detail-table",
  templateUrl: "./ngx-data-detail-table.component.html",
  styleUrls: ["./ngx-data-detail-table.component.scss"],
  animations: [
    trigger("detailExpand", [
      state(
        "collapsed",
        style({ height: "0px", minHeight: "0", visibility: "hidden" })
      ),
      state("expanded", style({ height: "*", visibility: "visible" })),
      transition(
        "expanded <=> collapsed",
        animate("225ms cubic-bezier(0.4, 0.0, 0.2, 1)")
      )
    ])
  ]
})
export class NgxDataDetailTableComponent {


  panelOpenState = false;
  _nodes: any[];

  dataSource: Array<MatTableDataSource<Entity>>;
  _displayedColumns: Array<any[]>;


  constructor() {
  }

  init(nodes: any[]) {
    this._nodes = nodes;
    let idx=0;
    this.dataSource = new Array<MatTableDataSource<Entity>>();
    this._displayedColumns = new Array<String[]>();
    this._nodes.forEach(node =>{
      this.dataSource[idx] = new MatTableDataSource<Entity>(node.rows);
      this._displayedColumns[idx++] = node.columns
        //.filter(column => column.visible)
        .map(x => x.property);
    })


  }


}

