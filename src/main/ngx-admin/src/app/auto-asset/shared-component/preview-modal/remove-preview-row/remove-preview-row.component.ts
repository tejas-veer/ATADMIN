import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'remove-preview-row',
  templateUrl: './remove-preview-row.component.html',
  styleUrls: ['./remove-preview-row.component.css']
})
export class RemovePreviewRowComponent implements OnInit {
  @Input() rowData: any;
  @Output() removeRow = new EventEmitter<any>();

  constructor() { }

  ngOnInit(): void {
  }

  onClick() {
    this.removeRow.emit(this.rowData);
  }

}
