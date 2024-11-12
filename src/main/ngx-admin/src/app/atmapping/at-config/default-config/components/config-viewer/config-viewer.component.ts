import { MatSort } from '@angular/material/sort';
import {AfterViewInit, Component, Input, OnChanges, OnInit, SimpleChanges, ViewChild} from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
@Component({
  selector: 'config-viewer',
  templateUrl: './config-viewer.component.html',
  styleUrls: ['./config-viewer.component.scss']
})
export class ConfigViewerComponent implements OnInit, AfterViewInit, OnChanges {
  @Input() data = [];
  displayedColumns = ['alias', 'value', 'level', 'updation_date'];
  dataSource = new MatTableDataSource([]);
  @ViewChild(MatSort) sort: MatSort;

  constructor() {
  }

  ngOnChanges(changes: SimpleChanges): void {
    console.log("Changes in Config viewer", changes);
    if (changes.data && changes.data.currentValue) {
      this.dataSource = new MatTableDataSource(changes.data.currentValue);
      this.dataSource.sort = this.sort;
    }
  }

  ngOnInit() {
  }

  /**
   * Set the sort after the view init since this component will
   * be able to query its view for the initialized sort.
   */
  ngAfterViewInit() {
    this.dataSource.sort = this.sort;
  }
}

export interface DisplayConfig {
  alias: string;
  value: string;
  level: string;
  type: string;
  updation_date: string;
}
