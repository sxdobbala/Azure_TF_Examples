import { DeepPartial, FindManyOptions, FindOneOptions } from 'typeorm';

export interface ICrudService<T> {

  findAll(filter?: FindManyOptions<T>): Promise<T[]>;
  getOne(id: any | FindOneOptions<T>): Promise<T>;
  create(entity: DeepPartial<T>): Promise<T>;
  update(id: any, entity: DeepPartial<T>): Promise<any>;
  delete(id: any): Promise<any>;
}
