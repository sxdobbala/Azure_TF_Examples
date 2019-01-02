import { Repository, EntityRepository } from "@iaminfinity/express-cassandra";
import { Observable } from "rxjs";
import { Package } from "../entity/package.entity";

@EntityRepository(Package)
export class PackageRepository extends Repository<Package> {


  findAll(query: any, options?: any): Observable<Package[]> {
    return this.find({}, { raw: true, allow_filtering: true });
  }


}
