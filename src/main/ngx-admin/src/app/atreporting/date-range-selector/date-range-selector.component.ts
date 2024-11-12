import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import {NbCalendarRange} from '@nebular/theme';
import {DateService} from '../../@core/data/date.service';
import {CookieService} from "../../@core/data/cookie.service";
import {AnalyticsService} from "../../@core/utils";
import {GoogleAnalyticsConfig} from "../../@core/data/ga-configs";

@Component({
  selector: 'ngx-date-range-selector',
  templateUrl: './date-range-selector.component.html',
  styleUrls: ['./date-range-selector.component.css'],
})
export class DateRangeSelectorComponent implements OnInit {
  selectedRange: NbCalendarRange<Date>;
  @Output() selectedRangeChange = new EventEmitter<NbCalendarRange<Date>>();
  maxDate: Date = new Date();
  @Output() triggerParentEvent = new EventEmitter<void>();
  isBusinessUnitCM: boolean = false;
  googleAnalyticsConfig = GoogleAnalyticsConfig;

  constructor(private dateService: DateService,
              private cookieService: CookieService,
              private userAnalyticsService: AnalyticsService) {
    this.selectedRange = {
      start: null,
      end: null,
    };
  }

  ngOnInit(): void {
    this.userAnalyticsService.trackEventOnGA( this.googleAnalyticsConfig.RI_BT_DATE_RANGE);
    if (this.cookieService.getBUSelectedFromCookie() === 'CM')
      this.isBusinessUnitCM = true;
  }

  getEndDateForCalendar(range: NbCalendarRange<Date>): string {
    let gmtEnd: string;
    const currentDate = new Date();
    const isToday = currentDate.toDateString() === range.end.toDateString();

    if (!isToday) {
      const endOfDay = new Date(range.end);
      endOfDay.setHours(23, 59, 59, 999);
      gmtEnd = endOfDay.toISOString();
    } else {
      gmtEnd = currentDate.toISOString();
    }
    return gmtEnd;
  }

  setDateRangeForCalendarInput(range: NbCalendarRange<Date>) {
    const gmtStart = range.start ? range.start.toISOString() : null;
    let gmtEnd = range.end ? range.end.toISOString() : null;
    if (range) {
      if (gmtStart && gmtEnd) {
        gmtEnd = this.getEndDateForCalendar(range);
        this.selectedRange = {
          start: new Date(gmtStart),
          end: new Date(gmtEnd),
        };
      }
    }
    this.selectedRangeChange.emit(this.selectedRange);
  }

  setDateRangeForButtonInput(days: number) {
    const dateRangeForButtonInputs = this.dateService.getDateRangeForCertainDuration(days);
    this.selectedRange.start = dateRangeForButtonInputs.start;
    this.selectedRange.end = dateRangeForButtonInputs.end;
    this.selectedRangeChange.emit(this.selectedRange);
  }

  emitButtonClick() {
    this.triggerParentEvent.emit();
  }
}
