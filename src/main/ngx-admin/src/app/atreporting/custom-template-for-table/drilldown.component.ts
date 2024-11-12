import {Component, Input, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {DomSanitizer, SafeHtml} from '@angular/platform-browser';
import {RowAdditionService} from '../../@core/data/row-addition.service';
import {NgSelectComponent} from '@ng-select/ng-select';
import {ConversionMapService} from '../../@core/data/conversion-map.service';
import {AnalyticsService} from '../../@core/utils';
import {GoogleAnalyticsConfig} from '../../@core/data/ga-configs';
import {ReportingConstantsService} from '../../@core/data/reporting-constants.service';

@Component({
    selector: 'ngx-custom-template-for-table',
    template: `
        <div class="drilldown-column" [style.padding-left.px]="getPaddingLeft()">
            <div class="button-html-container">
                <button *ngIf="!disableDrilldownButton" nbButton status="primary" class="drilldown-button"
                        (click)="toggleDropdownForDrilldown();">
                    &raquo;
                </button>
                <div style="margin-right: 10px" [innerHTML]="html"></div>
                <button (click)="removeRows()" *ngIf="showCloseButton" matTooltip="Close Drilldown Rows" class="close-button">&#10006;</button>
            </div>
            <ng-container *ngIf="isDropdownOpen">
                <ng-select [(ngModel)]="selectedOption" appendTo="body"
                           (ngModelChange)="onOptionChange()" [loading]="loader"
                           [items]="dropdownOptions" dropdownPosition="bottom" #mySelect>
                </ng-select>
            </ng-container>
        </div>
    `,
    styleUrls: ['./drilldown.component.css'],
})
export class DrilldownComponent implements OnInit, OnDestroy {
    @Input() rowData: any;
    @Input() html: SafeHtml;
    groupBySelectedOption: Array<string> = ['Template', 'All Assets'];
    isDropdownOpen = false;
    selectedOption: string;
    dropdownOptions: Array<string>;
    ALL_DROPDOWN_OPTIONS: Array<string> = this.reportingConstantsService.ALL_DRILLDOWN_DROPDDOWN_OPTIONS;
    globalFilterHeadings: any;
    disableDrilldownButton: boolean = false;
    @ViewChild('mySelect') mySelect: NgSelectComponent;
    showCloseButton: boolean = false;
    loader: boolean = false;
    googleAnalyticsConfig = GoogleAnalyticsConfig;

    constructor(private sanitizer: DomSanitizer,
                private rowAdditionService: RowAdditionService,
                private conversionMapService: ConversionMapService,
                private userAnalyticsService: AnalyticsService,
                private reportingConstantsService: ReportingConstantsService) {
    }

    private onScroll = (event: any) => {
        if (this.mySelect && this.mySelect.isOpen) {
            const isScrollingInScrollHost = (event.target.className as string).indexOf('ng-dropdown-panel-items') > -1;
            if (isScrollingInScrollHost) { return; }
            this.mySelect.close();
        }
    }

    ngOnInit() {
        if (this.rowData['DisableDrilldownButton']) {
            this.disableDrilldownButton = true;
        }
        if (this.rowData['enableCloseLevelButton'] === true) this.showCloseButton = true;
        const rowKeys = Object.keys(this.rowData);
        let firstKey: string;
        if (rowKeys.includes('Heading')) {
            firstKey = 'Heading';
        } else if (rowKeys.includes('Template')) {
            firstKey = 'Template';
        } else firstKey = rowKeys[0];
        this.html = this.sanitizer.bypassSecurityTrustHtml(this.rowData[firstKey]);
        this.rowAdditionService.groupBySelectedOption$.subscribe((option) => {
            this.groupBySelectedOption = option;
        });
        this.rowAdditionService.globalFilters$.subscribe((filters) => {
            this.globalFilterHeadings = Object.keys(filters).join(',');
        });
        this.loader = false;
        this.initializeDropdownOptions();
    }

    ngOnDestroy() {
        window.removeEventListener('scroll', this.onScroll, true);
    }

    initializeDropdownOptions() {
        const rowDataFiltersToExclude = this.rowData['ParentRowFilters'];
        const currentRowDimension = this.rowData['currentRowDimension'];
        const dropdownOptionsToExclude = [...this.groupBySelectedOption];
        if (rowDataFiltersToExclude) {
            const filterKeys = Object.keys(rowDataFiltersToExclude);
            dropdownOptionsToExclude.push(...filterKeys);
        }
        if (currentRowDimension) dropdownOptionsToExclude.push(currentRowDimension);
        if (dropdownOptionsToExclude.includes('All Assets')) dropdownOptionsToExclude.push('All Assets', 'Title', 'C2A', 'Template',
            'Image', 'Ad Id', 'Display Topic');
        this.dropdownOptions = [...this.ALL_DROPDOWN_OPTIONS];
        dropdownOptionsToExclude.forEach(optionToExclude => {
            for (const key in this.conversionMapService.frontendToDrilldownMultiMap) {
                if (this.conversionMapService.frontendToDrilldownMultiMap[key].includes(optionToExclude)) {
                    const indexToRemove = this.dropdownOptions.indexOf(key);
                    if (indexToRemove !== -1) {
                        this.dropdownOptions.splice(indexToRemove, 1);
                    }
                }
            }
        });
    }

    toggleDropdownForDrilldown() {
        this.isDropdownOpen = !this.isDropdownOpen;
        if (this.isDropdownOpen === true) { setTimeout(() => { this.mySelect.open(); }, 1); }
        window.addEventListener('scroll', this.onScroll, true);
        this.userAnalyticsService.trackEventOnGA(this.googleAnalyticsConfig.RI_BT_DRILLDOWN);
    }

    onOptionChange() {
        this.userAnalyticsService.trackEventOnGA( this.googleAnalyticsConfig.RI_INP_DRILLDOWN);
        this.loader = true;
        this.rowAdditionService.addRowsStartingFrom(this.rowData, this.selectedOption);
    }

    removeRows() {
        this.userAnalyticsService.trackEventOnGA( this.googleAnalyticsConfig.RI_BT_CLOSE_DRILLDOWN);
        this.rowAdditionService.removeRowsStartingFrom(this.rowData);
    }

    getPaddingLeft() {
        const level = this.rowData['Level'];
        if (level !== undefined) {
            return 26 * level;
        }
        return 0;
    }
}
