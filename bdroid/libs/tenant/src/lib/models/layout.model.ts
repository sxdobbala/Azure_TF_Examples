import { Graph } from './graph.model';
import { Edge } from './edge.model';
import { GNode } from './node.model';
import { Observable } from 'rxjs';

export interface Layout {
  settings?: any;
  run(graph: Graph): Graph | Observable<Graph>;
  updateEdge(graph: Graph, edge: Edge): Graph | Observable<Graph>;
  onDragStart?(draggingNode: GNode, $event: MouseEvent): void;
  onDrag?(draggingNode: GNode, $event: MouseEvent): void;
  onDragEnd?(draggingNode: GNode, $event: MouseEvent): void;
}
