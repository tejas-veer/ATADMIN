import { ThemeColorService } from './../../../@core/data/theme-color.service';
import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'ngx-datetime',
  templateUrl: './datetime.component.html',
  styleUrls: ['./datetime.component.scss']
})
export class DatetimeComponent implements OnInit {
  @Input() colorr = '';
  datetime: number;

  constructor() {
    this.datetime = Date.now();
    setInterval(() => {
      this.datetime = Date.now();
    }, 1000);
  }

  ngOnInit(): void {
  }

}
