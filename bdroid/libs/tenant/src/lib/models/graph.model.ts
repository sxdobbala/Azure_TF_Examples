import { GNode, ClusterNode } from './node.model';
import { Edge } from './edge.model';

export interface Graph {
  edges: Edge[];
  nodes: GNode[];
  clusters?: ClusterNode[];
  edgeLabels?: any;
}
