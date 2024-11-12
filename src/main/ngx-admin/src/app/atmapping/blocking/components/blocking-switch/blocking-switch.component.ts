import {Component, OnInit} from '@angular/core';
import {ViewCell} from 'ng2-smart-table';
import {BlockingStatusService} from '../../service/blocking-status.service';
import {UtilService} from '../../util/util.service';

@Component({
  selector: 'ngx-blocking-switch',
  templateUrl: './blocking-switch.component.html',
  styleUrls: ['./blocking-switch.component.scss']
})
export class BlockingSwitchComponent implements OnInit, ViewCell {

  value: string | number;
  rowData: any;

  constructor(public blockingStatus: BlockingStatusService) {
  }

  ngOnInit() {
  }

  public toggle(item) {
    this.blockingStatus.put(UtilService.getCreativeType(this.rowData), item, this.rowData);
  }

}
