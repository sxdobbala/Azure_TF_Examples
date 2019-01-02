import { BehaviorSubject } from "rxjs";
import { Injectable } from "@angular/core";
import { DataNode } from "./ngx-data-tree-model";

@Injectable()
export class NgxTreeDataTableDataSource {
  dataChange = new BehaviorSubject<DataNode[]>([]);

  get data(): DataNode[] {
    return this.dataChange.value;
  }

  constructor() {
  }

  initialize(treeData: any) {
    if (treeData) {
      // Parse the string to json object.
      const dataObject = JSON.parse(treeData);

      // Build the tree nodes from Json object. The result is a list of `DataNode` with nested
      //     file node as children.
      const data = this.buildFileTree(dataObject, 0);

      // Notify the change.
      this.dataChange.next(data);
    }
  }

  /**
   * Build the file structure tree. The `value` is the Json object, or a sub-tree of a Json object.
   * The return value is the list of `DataNode`.
   */
  buildFileTree(obj: { [key: string]: any }, level: number): DataNode[] {
    return Object.keys(obj).reduce<DataNode[]>((accumulator, key) => {
      const value = obj[key];
      const node = new DataNode();
      node.nodeName = key;

      if (value != null) {
        if (typeof value === "object") {
          node.children = this.buildFileTree(value, level + 1);
        } else {
          node.type = value;
        }
      }

      return accumulator.concat(node);
    }, []);
  }
}
