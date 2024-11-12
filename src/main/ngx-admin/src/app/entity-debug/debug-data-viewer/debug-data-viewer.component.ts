import {ChangeDetectionStrategy, Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {MatSortHeaderIntl} from "@angular/material/sort";
import {NgbActiveModal, NgbModal} from "@ng-bootstrap/ng-bootstrap";
@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'debug-data-viewer',
  templateUrl: './debug-data-viewer.component.html',
  styleUrls: ['./debug-data-viewer.component.scss']
})
export class DebugDataViewerComponent implements OnInit, OnChanges {
  @Input() data: any = {};
  @Input() tsize: string;
  learners: any = null;

  constructor() {
      this.data = {};
  }

  ngOnInit() {
  }

  ngOnChanges(changes: SimpleChanges) {
      if (this.data['learnerWiseDebugDataMap'] && changes['data']) {
          this.learners = Object.keys(this.data['learnerWiseDebugDataMap'])
      }
  }
}