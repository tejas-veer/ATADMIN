import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'asset-block-unblock',
  templateUrl: './asset-block-unblock.component.html',
  styleUrls: ['./asset-block-unblock.component.css']

})
export class AssetBlockUnblockComponent implements OnInit {
  @Input() rowData: any;
  @Output() toggledEvent = new EventEmitter<any>();

  ngOnInit(): void {
  }

  onToggle() {
    this.toggledEvent.emit(this.rowData);
  }

  isDisabled() {
    return this.rowData['entityName'] == 'GLOBAL'
  }
}
