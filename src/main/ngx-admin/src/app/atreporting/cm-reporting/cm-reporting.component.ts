import {Component, OnInit} from '@angular/core';
import {CookieService} from '../../@core/data/cookie.service';
import {RowAdditionService} from '../../@core/data/row-addition.service';
import {ToastingService} from '../../@core/utils/toaster.service';
import {DrilldownRowService} from '../../@core/data/drilldown-row.service';
import {NbCalendarRange} from '@nebular/theme';
import {LocalDataSource} from 'ng2-smart-table';
import {CmReportingService} from '../../@core/data/cm-reporting.service';
import {ActivatedRoute, Router} from '@angular/router';
import {DateService} from '../../@core/data/date.service';
import {Clipboard} from '@angular/cdk/clipboard';
import {MatSnackBar} from '@angular/material/snack-bar';
import {BuInUrlService} from '../../@core/data/bu-in-url.service';
import {FormatMetricsComponent} from '../format-metrics/format-metrics.component';
import {MatDialog} from '@angular/material/dialog';
import {SelectModeComponent} from "../select-mode/select-mode.component";
import {HashForUrlService} from "../../@core/data/hash-for-url.service";
import {AnalyticsService} from "../../@core/utils";
import {GoogleAnalyticsConfig} from "../../@core/data/ga-configs";
import {ReportingConstantsService} from "../../@core/data/reporting-constants.service";

@Component({
    selector: 'cm-reporting',
    templateUrl: './cm-reporting.component.html',
    styleUrls: ['./cm-reporting.component.css'],
})
export class CmReportingComponent implements OnInit {
    metricsOption: string = 'Page Impression';
    isShowTableLoader: boolean = false;
    scrollPosition = window.scrollY;

    // calendar
    isShowCalendar: boolean = false;
    formattedDateRangeForDisplay: string = 'Today';
    selectedRange: NbCalendarRange<Date>;

    // share
    sharableLink: any = window.location.href;
    enableShareButton: boolean = false;

    // Auto Select
    groupByOptions: Array<string> = this.reportingConstants.CM_RENDER_BY_OPTIONS;
    groupBySelectedOption: Array<string> = ['Template'];


    // table data
    settings: any;
    defaultRowPerPage: number = 10;
    source: LocalDataSource = new LocalDataSource();
    tableDataToBeRendered: any[] = [];
    existingFilters: any = {
        'Template': [],
        'Template Size': [],
        'Domain': [],
        'Ad Tag': [],
        'Partner': [],
    };
    googleAnalyticsConfig = GoogleAnalyticsConfig;
    generatedHash: any;

    constructor(private cookieService: CookieService,
                private reportingService: CmReportingService,
                private rowAdditionService: RowAdditionService,
                private toastingService: ToastingService,
                private drilldownService: DrilldownRowService,
                private router: Router,
                private route: ActivatedRoute,
                private dateService: DateService,
                private clipboard: Clipboard,
                private snackBar: MatSnackBar,
                private buInUrlService: BuInUrlService,
                public dialog: MatDialog,
                private hashService: HashForUrlService,
                public userAnalyticsService: AnalyticsService,
                private reportingConstants: ReportingConstantsService) {
        this.selectedRange = {start: null, end: null};
        this.settings = {actions: false};
        this.route.queryParams.subscribe(queryParams => {
            this.handleRouteChanges(queryParams);
        });
        this.buInUrlService.redir$.subscribe(() => {
            const currentParams = this.route.snapshot.queryParams;
            const updatedParams = {bu: this.cookieService.getBUSelectedFromCookie()};
            const queryParams = {...currentParams, ...updatedParams};
            this.router.navigate([this.cookieService.getBUSelectedFromCookie()],
                {relativeTo: this.route, queryParams: queryParams});
        });
    }

    ngOnInit(): void {
        this.reportingService.show$.subscribe(showLoader => {
            this.isShowTableLoader = showLoader;
        });
        this.reportingService.data$.subscribe(data => {
            if (data !== null) {
                let col_heading: any;
                if (data['inputPayload'] !== undefined) {
                    data = data['inputPayload'];
                }
                col_heading = Object.keys(data[0]);
                const metricHeadingsFromCookie = this.cookieService.getCmMetricsSettingsCookie();
                if (metricHeadingsFromCookie) {
                    const metricsToBeShown = JSON.parse(metricHeadingsFromCookie);
                    const replaceCount = col_heading.includes('enableCloseLevelButton') ? 9 : 8;
                    col_heading.splice(-replaceCount, replaceCount, ...metricsToBeShown);
                }
                this.tableDataToBeRendered = data;
                this.settings = this.reportingService.getSettingsConfigForTable(col_heading, this.defaultRowPerPage);
                this.isShowTableLoader = false;
                this.tableDataToBeRendered = this.drilldownService.addEnableCloseLevelKey(this.tableDataToBeRendered);
                this.source.load(this.tableDataToBeRendered);
            }
        });
        this.rowAdditionService.rowToAdd$.subscribe((request) => {
            if (request) {
                this.drilldownService.updateChildRowsInTable(request.rowData, request.selectedGroupByOption,
                    this.existingFilters, this.metricsOption, this.selectedRange, this.tableDataToBeRendered,
                    this.defaultRowPerPage);
            }
        });
        this.rowAdditionService.rowToRemove$.subscribe(data => {
            if (data) {
                const newData = this.drilldownService.deleteChildRowsIfSameStartingRow(this.tableDataToBeRendered, data);
                this.reportingService.sendData(newData);
            }
        });
        this.drilldownService.numberOfRowsPerPage$.subscribe(data => {
            this.defaultRowPerPage = data;
            this.setPager();
        });
    }

    handleRouteChanges(queryParams: any): any {
        if (queryParams.hash && this.formattedDateRangeForDisplay === 'Today' &&
            this.cookieService.getBUSelectedFromCookie() === queryParams.bu.toUpperCase()) {
            this.resetTableAndEnableShareButton();
            this.isShowTableLoader = true;
            this.reportingService.getPayloadFromHash(queryParams.hash).subscribe(
                (arr) => {
                    const queryParamsBody = arr[0];
                    if(queryParams.bu.toLowerCase() === queryParamsBody.bu.toLowerCase()) {
                        this.groupBySelectedOption = queryParamsBody.dimensions;
                        this.existingFilters = {  ...this.existingFilters, ...queryParamsBody.filters };
                        this.metricsOption = queryParamsBody.metrics;
                        const startDateFromParams = queryParamsBody.start;
                        const endDateFromParams = queryParamsBody.end;
                        this.selectedRange = {
                            start: this.dateService.convertDateToUTCFromIST(startDateFromParams),
                            end: this.dateService.convertDateToUTCFromIST(endDateFromParams)
                        };
                        this.formattedDateRangeForDisplay = this.dateService.formatDateRange(this.selectedRange);
                        const payload = this.getPayload();
                        this.rowAdditionService.setGroupBySelectedOption(this.groupBySelectedOption, this.existingFilters);
                        this.renderDataAndAckWithToaster(JSON.stringify(payload), startDateFromParams, endDateFromParams);
                    }
                },
                (error) => {
                    console.error('Error fetching data:', error);
                },
            );
        }
        this.cookieService.setBUSelectedCookie(queryParams.bu.toUpperCase());
    }

    resetTableAndEnableShareButton(): void {
        this.isShowTableLoader = true;
        this.source.reset();
        this.tableDataToBeRendered = null;
        this.sharableLink = window.location.href;
        this.enableShareButton = true;
    }

    showCalendar(booleanVal: boolean) {
        this.isShowCalendar = booleanVal;
    }

    handleSelectedRangeChange(newRange: NbCalendarRange<Date>) {
        this.selectedRange = newRange;
        this.formattedDateRangeForDisplay = this.dateService.formatDateRange(newRange);
    }

    closeModal(event: MouseEvent) {
        if (event.target === event.currentTarget)
            this.showCalendar(false);
    }

    preventClose(event: MouseEvent) {
        event.stopPropagation();
    }

    abortPreviousRequest() {
        const promise = this.reportingService.cancelRequest({hash: this.generatedHash});
        promise.then(data => {
        }).catch(error => {
            this.isShowTableLoader = false;
            console.error('Error:', error);
        });
    }

    generateReports() {
        this.userAnalyticsService.trackEventOnGA(this.googleAnalyticsConfig.RI_BT_GENERATE_REPORTS);
        this.isShowCalendar = false;
        this.resetTableAndEnableShareButton();
        this.rowAdditionService.setGroupBySelectedOption(this.groupBySelectedOption, this.existingFilters);
        const date = this.dateService.getStartDateAndEndDate(this.selectedRange);
        this.selectedRange = {start: new Date(date.startDate), end: new Date(date.endDate)};
        this.formattedDateRangeForDisplay = this.dateService.formatDateRange(this.selectedRange);
        const payload = this.getPayload();
        payload['date'] = this.selectedRange;
        this.generatedHash = this.hashService.generateHash(JSON.stringify(payload));
        payload['hash'] = this.generatedHash;
        delete payload['date'];
        this.rowAdditionService.setGroupBySelectedOption(this.groupBySelectedOption, this.existingFilters);
        this.renderDataAndAckWithToaster(JSON.stringify(payload), date.startDate, date.endDate);
        this.redirForReporting(this.generatedHash);
    }

    getPayload(): any {
        return  {
            'filters': this.reportingService.removeEmptyValuesInPayload(
                JSON.parse(JSON.stringify(this.existingFilters))),
            'metrics': this.metricsOption,
            'dimensions': this.groupBySelectedOption,
            'bu': this.cookieService.getBUSelectedFromCookie(),
            'queryLevel': 1,
        };
    }

    renderDataAndAckWithToaster(payloadJson: string, startDate: string, endDate: string): any {
        const promise = this.reportingService.getReportingDruidResponse(payloadJson, startDate, endDate, null);
        promise.then(data => {
            this.toastingService.success('Generating Report', `Top <b>${data.lengthOfData}</b> records for ${data.startDate} to ${data.endDate}.`);
            this.reportingService.sendData(data.dataToBeRenderedInTable);
        }).catch(error => {
            this.isShowTableLoader = false;
            console.error('Error:', error);
        });
    }

    redirForReporting(hash: string) {
        const queryParams = {
            hash: hash,
            bu: this.cookieService.getBUSelectedFromCookie(),
        };
        this.router.navigate([this.cookieService.getBUSelectedFromCookie()],
            {
                relativeTo: this.route,
                queryParams: queryParams,
            });
    }

    setPager() {
        this.source.setPaging(1, this.defaultRowPerPage, true);
        this.settings = Object.assign({}, this.settings);
    }

    onSearch(query: string = '') {
        this.userAnalyticsService.trackEventOnGA(this.googleAnalyticsConfig.RI_INP_TABLE_SEARCH);
        if (query.length > 0) {
            this.source.setFilter([
                {field: 'Template', search: query},
                {field: 'Template Size', search: query},
            ], false);
        } else this.source.reset();
    }

    copyToClipboard() {
        this.sharableLink = window.location.href;
        this.clipboard.copy(this.sharableLink);
        this.snackBar.open('URL Copied!', 'Dismiss', {duration: 2000});
        this.userAnalyticsService.trackEventOnGA( this.googleAnalyticsConfig.RI_BT_SHARE);
    }

    openFormatMetricsModal() {
        window.scrollTo(0, this.scrollPosition);
        const dialogRef = this.dialog.open(FormatMetricsComponent, {
            width: '500px',
            data: {
                currentColumns: this.settings.columns,
            },
        });
        dialogRef.afterClosed().subscribe(col_heading => {
            this.settings = Object.assign({}, this.reportingService.getSettingsConfigForTable(col_heading,
                this.defaultRowPerPage));
        });
        this.userAnalyticsService.trackEventOnGA( this.googleAnalyticsConfig.RI_BT_METRICS_MANIPULATION);
    }

    openFiltersModal(currentDimension) {
        window.scrollTo(0, this.scrollPosition);
        const dialogRef = this.dialog.open(SelectModeComponent, {
            data: {
                globalFilters: this.existingFilters,
                currentDimension: currentDimension,
                bu: this.cookieService.getBUSelectedFromCookie(),
            },
        });
        dialogRef.afterClosed().subscribe(data => {
            if (data) {
                const dimension = data.selectedFilterHeading;
                const selectedFilters = data.selectedFilterValues;
                this.existingFilters = this.reportingService.getUpdatedFilterValues(dimension,
                    selectedFilters, this.existingFilters);
            }
        });
    }

    removeFilterChip(filterKey: string): void {
        if (this.existingFilters.hasOwnProperty(filterKey)) {
            delete this.existingFilters[filterKey];
        }
    }

    isShowAddFiltersButton(): boolean {
        return Object.keys(this.existingFilters).length < 5;
    }
}
