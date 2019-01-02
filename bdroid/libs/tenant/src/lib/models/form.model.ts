export enum EnvironmentType {
  Prod = "datalake_prod",
  NonProd = "datalake_test",
}
export enum SourceType {
  UHGDataLake = "UHG Datalake",
  ARAVolume = "ARA Volume",
  DLAVolume = "DLA Volume",
  RAVolume = "R&A Volume",
  Others = "Others",
}


export enum StatusType {
  Active = "Active",
  Inactive = "Inactive",
  PoC = "PoC",
  Onetime = "Inactive",
  Sandbox = "Sandbox"
}

export enum PackageType {
  Analytics = "Analytics Package",
  Basic = "Basic Package",
}
