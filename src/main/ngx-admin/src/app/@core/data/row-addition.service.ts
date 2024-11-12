import {Injectable} from '@angular/core';
import {BehaviorSubject} from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class RowAdditionService {
    private rowToAddSubject = new BehaviorSubject<any>(null);
    rowToAdd$ = this.rowToAddSubject.asObservable();

    addRowsStartingFrom(rowData: any, selectedGroupByOption: string) {
        this.rowToAddSubject.next({rowData, selectedGroupByOption});
    }

    private groupBySelectedOptionSubject = new BehaviorSubject<any>(null);
    groupBySelectedOption$ = this.groupBySelectedOptionSubject.asObservable();
    private globalFiltersSubject = new BehaviorSubject<any>({});
    globalFilters$ = this.globalFiltersSubject.asObservable();

    setGroupBySelectedOption(option: Array<string>, globalFilters: any) {
        this.groupBySelectedOptionSubject.next(option);
        this.globalFiltersSubject.next(globalFilters);
    }

    private removeRows = new BehaviorSubject<any>(null);
    rowToRemove$ = this.removeRows.asObservable();

    removeRowsStartingFrom(rowToRemove: any) {
        this.removeRows.next(rowToRemove);
    }

}
