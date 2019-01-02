export interface NodePosition {
  x: number;
  y: number;
}

export interface NodeDimension {
  width: number;
  height: number;
}

export interface GNode {
  id: string;
  position?: NodePosition;
  dimension?: NodeDimension;
  transform?: string;
  label?: string;
  data?: any;
}

export interface ClusterNode extends GNode {
  childNodeIds: string[];
}
