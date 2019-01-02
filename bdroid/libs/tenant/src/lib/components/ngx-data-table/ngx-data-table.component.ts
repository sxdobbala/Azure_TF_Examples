import { Component, ViewChild, Input, ElementRef, TemplateRef } from "@angular/core";
import { MatDialog, MatPaginator, MatSnackBar, MatSort, MatTableDataSource } from "@angular/material";
import {
  trigger,
  state,
  style,
  transition,
  animate
} from "@angular/animations";
import { Entity, EntityColumnDef, EntityService } from "@bdroid/shared";
import { NgxDataDetailTableComponent } from "../ngx-data-detail-table/ngx-data-detail-table.component";
import { concatMap, filter, map } from "rxjs/operators";
import { Store } from "@ngxs/store";
import { Angular5Csv } from "angular5-csv/Angular5-csv";
import { NgxDataTableDataSource } from "./ngx-data-table-datasource";

@Component({
  selector: "ngx-data-table",
  templateUrl: "./ngx-data-table.component.html",
  styleUrls: ["./ngx-data-table.component.scss"],
  animations: [
    trigger("detailExpand", [
      state(
        "collapsed",
        style({ height: "0px", minHeight: "0", visibility: "hidden" })
      ),
      state("expanded", style({ height: "*", visibility: "visible" })),
      transition(
        "expanded <=> collapsed",
        animate("225ms cubic-bezier(0.4, 0.0, 0.2, 1)")
      )
    ])
  ]
})
export class NgxDataTableComponent {


  @Input()
  set showActionColumn(showActionColumn: boolean) {
    this.displayActionColumn = showActionColumn;
  }

  @Input()
  set showColumnFilter(showColumnFilter: boolean) {
    this._showColumnFilter = showColumnFilter;
  }

  @Input()
  set showDetails(showDetails: boolean) {
    this._displayDetails = showDetails;
  }

  @Input()
  set showToolbar(showToolbar: boolean) {
    this._showToolbar = showToolbar;
  }

  @Input()
  set defaultColumns(defaultColumns: any[]) {
    this._defaultColumns = defaultColumns;
  }

  @Input()
  set service(service: EntityService<Entity>) {
    this.entityService = service;
  }

  @Input()
  set dialogComponent(dialogComponent: any) {
    this._dialogComponent = dialogComponent;
  }

  @Input()
  set dialogEntity(dialogEntity: any) {
    this._dialogEntity = dialogEntity;
  }

  @Input()
  set title(title: string) {
    this._title = title;
  }


  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;
  @ViewChild("TABLE") table: ElementRef;
  @ViewChild(NgxDataDetailTableComponent) dataTable: NgxDataDetailTableComponent;

  dataSource: any;
  //dataSource: MatTableDataSource<Entity>;
  entityService: EntityService<Entity>;

  /** Columns displayed in the table. Columns IDs can be added, removed, or reordered. */

  _title: string;
  _displayDetails = true;
  _displayedColumns: Array<string>;
  _defaultColumns: any[];
  _allDataColumns: Array<EntityColumnDef<Entity>>;
  _dataColumns: Array<EntityColumnDef<Entity>>;
  expandedElement: Array<string>;
  displayActionColumn = true;

  readonly actionColumn?: string = "Actions";
  _showColumnFilter? = true;
  _showToolbar? = true;
  _headerColumnNames = null;
  _dialogComponent: any;
  _dialogEntity: any;

  isExpansionDetailRow = (i: number, row: Object) =>
    row.hasOwnProperty("detailRow");


  constructor(
    private store: Store,
    private snack: MatSnackBar,
    private dialog: MatDialog) {

  }

  /**
   * reload data
   */
  refresh() {

    this.entityService.getAll().subscribe(res => {
      this._allDataColumns = [];

      for (let idx = 0; idx < res.length; idx++) {
        const entity = res[idx];
        const keys = Object.keys(entity);
        keys.forEach(key => {
          const isColumnExist = this._allDataColumns
            .filter(column => (column.property === key));
          if (isColumnExist.length <= 0) {
            let visible = false;
            let headerName = null;
            let cellTemplate = null;
            const isPropertyExist = this._defaultColumns.filter(entityColumnDef => (entityColumnDef.property === key));

            if (isPropertyExist.length > 0) {
              visible = true;
              headerName = isPropertyExist[0].header;
              cellTemplate = isPropertyExist[0].cellTemplate;
            }
            this._allDataColumns.push(new EntityColumnDef<Entity>({
              property: key,
              visible: visible,
              cellTemplate: cellTemplate,
              header: headerName || this.convertToHeaderField(key),
              displayFn: (dataEntity) => `${dataEntity[key] === null ? "" : dataEntity[key]}`
            }));
          }
        });

        entity.details = [];
      }
      if (this._displayDetails) {
        this.dataSource = new NgxDataTableDataSource(
          this.paginator,
          res,
          this.sort);
      } else {
        this.dataSource = new MatTableDataSource<Entity>(res);
        this.dataSource.sort = this.sort;
        this.dataSource.paginator = this.paginator;
      }

      this.displayedColumns();

    });


  }

  exportToCSV() {
    let data = null;
    if (this._displayDetails) {
      data = this.dataSource.getData();
    } else {
      data = this.dataSource.data;
    }

    const options = {
      fieldSeparator: ",",
      showLabels: true,
      showTitle: true,
      title: this._title,
      useBom: true,
      noDownload: false,
      headers: this._displayedColumns.slice(0, this._displayedColumns.length - 1)
    };
    const filteredData = [];
    for (let idx = 0; idx < data.length; idx++) {
      const displayedData = {};
      for (let idxx = 0; idxx < this._displayedColumns.length - 1; idxx++) {
        displayedData[this._displayedColumns[idxx]] = data[idx][this._displayedColumns[idxx]];
      }
      filteredData.push(displayedData);
    }

    const csvData = new Angular5Csv(filteredData, this._title, options);

  }

  displayedColumns() {

    this._dataColumns = this._allDataColumns
      .filter(column => column.visible);

    this._displayedColumns = this._dataColumns
      .map(x => x.property);
    if (this.displayActionColumn) {
      this._displayedColumns = this._displayedColumns.concat([this.actionColumn]);
    }
    return this._displayedColumns;
  }

  convertToHeaderField(key: string) {
    return key.replace("/([A-Z])/g", " $1").replace(/^./, function(str) {
      return str.toUpperCase();
    });
  }

  /**
   * expand collapse a row
   * @param row
   */
  async toggleRow(row) {
    if (this._displayDetails) {
      if (this.expandedElement === row) {
        this.expandedElement = null;
      } else {
        this.expandedElement = row;
        console.log( row )
        const nodes = await this.entityService.getDetails(row);
        this.dataTable.init(nodes);
      }
    } else {
      this.expandedElement = null;
    }
  }

  toggleColumnVisibility(column) {
    this._allDataColumns.forEach(searchColumn => {
      if (column.property === searchColumn.property) {
        searchColumn.visible = !column.visible;
      }
    });
    this.displayedColumns();
  }

  applyFilter(filterValue: string) {
    this.dataSource.filter = filterValue;
  }


  updateOrCreate(entity: Entity, isNew: boolean) {
    if (isNew) {
      return this.entityService.post(entity).pipe(map(_ => this.refresh()));
    } else {
      return this.entityService.put(entity).pipe(map(_ => this.refresh()));
    }
  }

  /**
   *  openPopUp() is used in entity.component.html
   *  if you want different implantation (e.g., add-new-line instead of popup, inline edit)
   *  make a copy of entity.component.html as <entity>.component.html and implement your own add/edit logic.
   **/
  openPopUp(entity: Entity) {
    let isNew = false;
    if (!entity) {
      isNew = true;
      entity = this._dialogEntity;
    }
    const title = isNew ? "Add " + this._title : "Update " + this._title;
    const dialogRef = this.dialog.open(this._dialogComponent, {
      width: "60%",
      disableClose: true,
      data: { title: title, payload: entity }
    });

    dialogRef
      .afterClosed()
      .pipe(
        filter(res => res !== false),
        // tap(res => console.log(res)),
        map((res: Entity) => {
          if (!isNew) {
            //res.id = unparse.unparse(entity.id["buffer"]);

            res.id = entity.id;
          }
          return res;
        }),
        concatMap((res: Entity) => this.updateOrCreate(res, isNew))
      )
      .subscribe(
        _ => {
          this.snack.open(isNew ? this._title + " Created!" : this._title + " Updated!", "OK", { duration: 5000 });
          //this.store.dispatch(new Navigate([`/dashboard/tenants`]));
          this.refresh();
        },
        error => this.snack.open(error, "OK", { duration: 10000 })
      );
  }
}
