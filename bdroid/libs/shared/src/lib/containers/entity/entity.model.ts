import { TemplateRef } from "@angular/core";

export abstract class Entity {
  abstract id: number | string;
  [key: string]: any;
  constructor(init?: Partial<Entity>) {
    Object.assign(this, init);
  }
}

export class EntityColumnDef<T> {

  public visible : boolean;
  readonly property: string;
  readonly header = this.property;
  cellTemplate?: TemplateRef<any>;
  readonly details : Array<EntityColumnDef<T>>[];
  readonly displayFn = (entity: T) => entity[this.property];

  public constructor(init?: Partial<EntityColumnDef<T>>) {
    Object.assign(this, init);
  }
}
