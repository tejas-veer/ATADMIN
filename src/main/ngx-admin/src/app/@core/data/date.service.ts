import {Injectable} from '@angular/core';
import {NbCalendarRange} from '@nebular/theme';

@Injectable({
    providedIn: 'root',
})
export class DateService {
    constructor() {
    }

    getEndDateIfNull(selectedDateRange: NbCalendarRange<Date>): string {
        let endDate: string;
        if (!selectedDateRange.end) {
            const now = new Date();
            const start = selectedDateRange.start;
            if (start.getDate() === now.getDate() &&
                start.getMonth() === now.getMonth() &&
                start.getFullYear() === now.getFullYear()) {
                endDate = this.returnDateInGmtIfToday(now).toString();
            } else {
                const endOfDay = new Date(start);
                endOfDay.setHours(23, 59, 59, 999);
                endDate = endOfDay.toString();
            }
        }
        return endDate;
    }

    getDateRangeForCertainDuration(days: number): NbCalendarRange<Date> {
        const selectedRange: NbCalendarRange<Date> = {start: null, end: null};
        const now = new Date();
        if (days === 1) {
            const twentyFourHoursAgo = new Date(now.getTime() - 24 * 60 * 60 * 1000);
            selectedRange.start = this.returnDateInGmtIfToday(twentyFourHoursAgo);
        } else {
            const start = new Date(now.getTime() - days * 24 * 60 * 60 * 1000);
            const startOfStart = new Date(start);
            startOfStart.setHours(0, 0, 0, 0);
            selectedRange.start = startOfStart;
        }
        selectedRange.end = this.returnDateInGmtIfToday(now);
        return selectedRange;
    }

    getEndDate(selectedDateRange: NbCalendarRange<Date>): string {
        let endDate: string;
        if (selectedDateRange.end === undefined || selectedDateRange.end === null)
            endDate = this.getEndDateIfNull(selectedDateRange);
        else endDate = this.returnDateInGmtIfToday(selectedDateRange.end).toString();
        return endDate;
    }

    formatDate(inputDate: string): string {
        const date = new Date(inputDate);
        const formattedDate = date.toDateString() + ' ' + date.toLocaleTimeString('en-US', {hour12: false});
        const dateWithoutDay = formattedDate.replace(/^[A-Za-z]{3}\s+/, '');
        return dateWithoutDay;
    }

    formatDateRange(selectedRange: NbCalendarRange<Date>): string {
        let formattedDateRangeForDisplay: string = 'Select a date range';
        if (selectedRange.start && selectedRange.end) {
            formattedDateRangeForDisplay = `${selectedRange.start.toDateString()} - ${selectedRange.end.toDateString()}`;
        } else if (selectedRange.start) formattedDateRangeForDisplay = `${selectedRange.start.toDateString()}`;
        return formattedDateRangeForDisplay;
    }

    returnDateInGmtIfToday(dateToBeSet5HoursAgo): Date {
        const now = new Date();
            if (
                (dateToBeSet5HoursAgo.getHours() !== 0 && dateToBeSet5HoursAgo.getMinutes() !== 0 && dateToBeSet5HoursAgo.getSeconds() !== 0) &&
                (dateToBeSet5HoursAgo.getHours() !== 23 && dateToBeSet5HoursAgo.getMinutes() !== 59 && dateToBeSet5HoursAgo.getSeconds() !== 59)
            ) {
                const fiveHoursAgo = new Date(dateToBeSet5HoursAgo);
                fiveHoursAgo.setHours(now.getHours() - 5);
                fiveHoursAgo.setMinutes(now.getMinutes() - 30);
                return fiveHoursAgo;
        } else return dateToBeSet5HoursAgo;
    }

    getStartDateAndEndDate(selectedDateRange: NbCalendarRange<Date>): any {
        if (selectedDateRange.start === null && selectedDateRange.end === null) {
            const defaultDate: NbCalendarRange<Date> = this.getDateRangeForCertainDuration(0);
            selectedDateRange.start = defaultDate.start;
            selectedDateRange.end = defaultDate.end;
        }
        const endDate = this.getEndDate(selectedDateRange);
        const startDate = this.returnDateInGmtIfToday(selectedDateRange.start).toString();
        return {startDate: startDate, endDate: endDate};
    }

    convertDateToUTCFromIST(dateString: string): Date {
        const date = new Date(dateString);
        date.setHours(date.getHours() - 5);
        date.setMinutes(date.getMinutes() - 30);
        return date;
    }

}
