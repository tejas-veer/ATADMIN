import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {ReportingCsvService} from '../../@core/data/reporting-csv.service';
import {AnalyticsService} from "../../@core/utils";
import {GoogleAnalyticsConfig} from "../../@core/data/ga-configs";

@Component({
    selector: 'ngx-filters',
    templateUrl: './filters.component.html',
    styleUrls: ['./filters.component.css'],
})
export class FiltersComponent implements OnInit {
    scrollPosition = window.scrollY;
    @Input() existingFilters: any;
    @Input() isShowAddFiltersButton: boolean;
    @Output() openFiltersModalEvent: EventEmitter<string> = new EventEmitter<string>();
    @Output() removeFiltersEvent: EventEmitter<string> = new EventEmitter<string>();

    formattedFilterForChipsHover: any[];
    activeFilter: string | null = null;
    filterOverlayLeftPosition: number | null = null;
    filterOverlayTopPosition: number | null = null;
    iconColors: { [key: string]: string } = {};
    googleAnalyticsConfig = GoogleAnalyticsConfig;

    constructor(private reportingCsvService: ReportingCsvService,
                private userAnalyticsService: AnalyticsService) {
    }

    ngOnInit(): void {
    }

    openFiltersModal(filterKey: string) {
        this.userAnalyticsService.trackEventOnGA( this.googleAnalyticsConfig.RI_BT_FILTERS_MODAL);
        this.openFiltersModalEvent.emit(filterKey);
    }

    onChipHover(event: MouseEvent, filterName: string): void {
        window.scrollTo(0, this.scrollPosition);
        this.formattedFilterForChipsHover = this.reportingCsvService.formatFilterChipsData(
            this.existingFilters[filterName]);
        if (this.formattedFilterForChipsHover.length > 0) {
            this.activeFilter = filterName;
            const chipElement = event.currentTarget as HTMLElement;
            this.filterOverlayLeftPosition = chipElement.getBoundingClientRect().left + window.scrollX - 600;
            this.filterOverlayTopPosition = chipElement.getBoundingClientRect().top - 265;
        }
    }

    removeFilterChip(filterKey: string): void {
        this.closeOverlay();
        this.removeFiltersEvent.emit(filterKey);
    }

    closeOverlay(): void {
        this.activeFilter = null;
        this.filterOverlayLeftPosition = null;
        this.filterOverlayTopPosition = null;
    }

    getNumberOfValues(filter: string): number {
        const formattedChips = this.reportingCsvService.formatFilterChipsData(this.existingFilters[filter]);
        const numberOfValues = formattedChips.length;
        this.setIconColor(filter, formattedChips);
        return numberOfValues;
    }

    setIconColor(filter: string, formattedChips: any[]): void {
        const equalValues = formattedChips.filter(value => value.state === 'Equal');
        const notEqualValues = formattedChips.filter(value => value.state === 'Not Equal');
        const containsValues = formattedChips.filter(value => value.state === 'Contains');

        let color: string = '#f4d660';
        if (equalValues.length === formattedChips.length) color = '#40dc7e';
        else if (notEqualValues.length === formattedChips.length) color = 'red';
        else if (containsValues.length === formattedChips.length) color = '#804674';
        if (!this.iconColors) this.iconColors = {};
        this.iconColors[filter] = color;
    }

    getBorderColor(state: string): string {
        switch (state) {
            case 'Equal':
                return '#40dc7e';
            case 'Not Equal':
                return 'red';
            case 'Contains':
                return '#804674';
        }
    }

    isDisableFilterChipClick(filter: string): boolean {
        return filter === 'Campaign Type';
    }
}
