import { Component, Input } from "@angular/core";
import { MatTreeFlatDataSource } from "@angular/material";
import {
  trigger,
  state,
  style,
  transition,
  animate
} from "@angular/animations";
import { Observable, of as observableOf } from "rxjs";
import { MatTreeFlattener } from "@angular/material/tree";
import { FlatTreeControl } from "@angular/cdk/tree";
import { NgxTreeDataTableDataSource } from "./ngx-data-tree-table-datasource";
import { DataFlatNode, DataNode } from "./ngx-data-tree-model";
@Component({
  selector: "ngx-data-tree-table",
  templateUrl: "./ngx-data-tree-table.component.html",
  providers: [NgxTreeDataTableDataSource],
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
export class NgxDataTreeTableComponent {


  @Input()
  set treeData(_treeData: any) {
    if (_treeData) {
      this._data = _treeData;
    }

  }

  _data: any;
  treeControl: FlatTreeControl<DataFlatNode>;
  treeFlattener: MatTreeFlattener<DataNode, DataFlatNode>;
  dataSource: MatTreeFlatDataSource<DataNode, DataFlatNode>;

  constructor(private database: NgxTreeDataTableDataSource) {
    this.treeFlattener = new MatTreeFlattener(this.transformer, this._getLevel,
      this._isExpandable, this._getChildren);
    this.treeControl = new FlatTreeControl<DataFlatNode>(this._getLevel, this._isExpandable);
    this.dataSource = new MatTreeFlatDataSource(this.treeControl, this.treeFlattener);
    this.init(this._data);
  }

  init(_data: any) {

    this.database.initialize(_data);
    this.database.dataChange.subscribe((data) => this.dataSource.data = data);
  }

  transformer = (node: DataNode, level: number) => {
    return new DataFlatNode(!!node.children, node.nodeName, level, node.type);
  };

  private _getLevel = (node: DataFlatNode) => node.level;

  private _isExpandable = (node: DataFlatNode) => node.expandable;

  private _getChildren = (node: DataNode): Observable<DataNode[]> => observableOf(node.children);

  hasChild = (_: number, _nodeData: DataFlatNode) => _nodeData.expandable;
}

