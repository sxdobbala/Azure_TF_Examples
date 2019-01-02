import { Injectable } from "@nestjs/common";
import { InjectRepository } from "@iaminfinity/express-cassandra";
import { Observable } from "rxjs";
import { PackageRepository } from "../repository/package.repository";
import { Package } from "../entity/package.entity";

@Injectable()
export class PackageService {
  constructor(
    @InjectRepository(PackageRepository)
    private readonly packageRepository: PackageRepository
  ) {
  }

  findAll(): Observable<Package[]> {
    return this.packageRepository.findAll({}, { raw: true, allow_filtering: true });
  }


}
