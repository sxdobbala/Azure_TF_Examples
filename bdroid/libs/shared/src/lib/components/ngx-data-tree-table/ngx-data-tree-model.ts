import { Entity } from '@bdroid/shared';
/**
* Data node data with nested structure.
* Each node has a nameOftheNode, and a type or a list of children.
*/
export class DataNode{
  children: DataNode[];
  nodeName: string;
  type: any;
}


/** Flat node with expandable and level information */
export class DataFlatNode {
  constructor(
    public expandable: boolean, public nodeName: string, public level: number, public type: any) {}
}
