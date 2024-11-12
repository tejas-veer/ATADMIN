import { Component, Input, OnInit, ViewEncapsulation } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';

@Component({
    selector: 'mapping-table',
    templateUrl: './mapping-table.component.html',
    styleUrls: ['./mapping-table.component.scss'],
})

export class MappingTableComponent implements OnInit {
    private _columns: Array<string>;
    private _data: Array<any> = [];
    @Input() sizeList: Array<any> = [];
    displayedColumns: Array<string>;

    constructor() {
    }

    ngOnInit() {
    }


    add(item: any): void {
        this.data = Object.assign([], this.data.concat([item]));
    }

    deleteItem(item: any): void {
        const index: number = this.data.indexOf(item);
        this.data.splice(index, 1);
    }

    clearData(): void {
        this.data = [];
    }

    get columns(): Array<string> {
        return this._columns;
    }

    set columns(value: Array<string>) {
        setTimeout(() => {
            this.displayedColumns = ['Templates', 'Sizes'].concat(value).concat(['Action']);
        }, 100);
        this._columns = value;
    }

    get data(): Array<any> {
        return this._data;
    }

    set data(value: Array<any>) {
        this._data = value;
    }


}