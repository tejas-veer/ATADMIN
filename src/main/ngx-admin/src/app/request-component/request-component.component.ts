import { Component, OnInit, ViewChild } from '@angular/core';
import { MappingService } from '../@core/data/mapping.service';
import { MatTableDataSource } from '@angular/material/table';
import { MatSort, Sort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';

@Component({
  selector: 'request-component',
  templateUrl: './request-component.component.html',
  styleUrls: ['./request-component.component.css']
})
export class RequestComponentComponent implements OnInit {
  requestArray: Array<any>;
  dataSource: MatTableDataSource<any>;
  displayedColumns: Array<String>;
  @ViewChild(MatSort) sort: MatSort;
  @ViewChild('paginator') paginator: MatPaginator;
  private _loader: boolean = true;
  isnothingExists: boolean = false;

  constructor(private mappingService: MappingService) { }

  ngOnInit(): void {
    this.showRequests();
    this.dataSource.sort = this.sort;

  }

  ngAfterViewInit() {
    this.dataSource.sort = this.sort;
  }

  showRequests() {
    this.loader = true;
    this.mappingService.showRequests().then(
      data => {
        if (data.length > 0) {
          this.requestArray = data;
          this.dataSource = new MatTableDataSource(data);
          this.displayedColumns = this.mappingService.requestDisplayedColumns;
          setTimeout(() => {
            this.dataSource.sort = this.sort;
          }, 500);
          this.loader = false;
          this.dataSource.paginator = this.paginator;
        }
        else {
          this.loader = false;
          this.isnothingExists = true;
        }
      }
    )
  }

  refresh() {
    this.showRequests();
  }

  get loader(): boolean {
    return this._loader;
  }

  set loader(value: boolean) {
    this._loader = value;
  }


}
