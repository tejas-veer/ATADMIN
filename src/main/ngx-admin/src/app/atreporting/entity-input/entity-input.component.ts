import {Component, OnInit, ViewChild} from '@angular/core';
import {ModalPageComponent} from '../../atmapping/input-box/modal-page/modal-page.component';
import {LocalDataSource} from 'ng2-smart-table';
import {CookieService} from '../../@core/data/cookie.service';
import {ReportingCsvService} from '../../@core/data/reporting-csv.service';
import {NbCalendarRange} from '@nebular/theme';
import {RowAdditionService} from '../../@core/data/row-addition.service';
import {ToastingService} from '../../@core/utils/toaster.service';
import {DrilldownRowService} from '../../@core/data/drilldown-row.service';
import {ActivatedRoute, Router} from '@angular/router';
import {DateService} from '../../@core/data/date.service';
import {Clipboard} from '@angular/cdk/clipboard';
import {MatSnackBar} from '@angular/material/snack-bar';
import {BuInUrlService} from '../../@core/data/bu-in-url.service';
import {MatDialog, MatDialogRef} from '@angular/material/dialog';
import {FormatMetricsComponent} from '../format-metrics/format-metrics.component';
import {SelectModeComponent} from '../select-mode/select-mode.component';
import {HashForUrlService} from '../../@core/data/hash-for-url.service';
import {AnalyticsService} from '../../@core/utils';
import {GoogleAnalyticsConfig} from '../../@core/data/ga-configs';
import {UtilService} from "../../@core/utils/util.service";
import {ReportingConstantsService} from "../../@core/data/reporting-constants.service";

@Component({
    selector: 'ngx-autocomplete-entity-input',
    templateUrl: './entity-input.component.html',
    styleUrls: ['./entity-input.component.css'],
})
export class EntityInputComponent implements OnInit {
    @ViewChild('bulkModalPage') bulkModalPage: ModalPageComponent;
    sector: string = 'Reporting';
    tableDataToBeRendered: any[] = [];
    source: LocalDataSource = new LocalDataSource();
    settings: any;
    defaultRowPerPage: number = 10;
    allowCsvUpload: boolean = false;
    isShowTableLoader: boolean = false;
    sizeList;
    metricsOption: string = 'Valid Impressions';

    selectedRange: NbCalendarRange<Date>;
    formattedDateRangeForDisplay: string = 'Today';
    isShowCalendar: boolean = false;
    groupByOptions: Array<string> = this.reportingConstants.MAX_RENDER_BY_OPTIONS;
    groupBySelectedOption: Array<string> = ['Template', 'All Assets'];
    sharableLink: any = window.location.href;
    enableShareButton: boolean = false;
    enableFormatMetricsButton: boolean = false;
    dialogRef: MatDialogRef<FormatMetricsComponent>;
    scrollPosition = window.scrollY;
    existingFilters: Record<string, any> = {
        'Campaign Type': [
            {
                'state': 'Equal',
                'values': ['native'],
            },
        ]};
    googleAnalyticsConfig = GoogleAnalyticsConfig;
    generatedHash: any;

    constructor(private cookieService: CookieService,
                private reportingCsvService: ReportingCsvService,
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
                private userAnalyticsService: AnalyticsService,
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
            this.router.navigate([this.cookieService.getBUSelectedFromCookie()], {
                relativeTo: this.route,
                queryParams: queryParams,
            });
        });
        this.reportingConstants.MAX_FILTER_CHIPS_LIST.forEach( key => {
                this.existingFilters[key] = [];
        });
    }

    handleRouteChanges(queryParams: any): any {
        if (queryParams.hash && this.formattedDateRangeForDisplay === 'Today' &&
            this.cookieService.getBUSelectedFromCookie() === queryParams.bu.toUpperCase()) {
            this.resetTableAndEnableShareButton(true);
            console.log("queryParams.hash", queryParams.hash);
            this.isShowTableLoader = true;
            console.log("this.isShowTableLoader", this.isShowTableLoader);
            this.reportingCsvService.getPayloadFromHash(queryParams.hash).subscribe(
                (arr) => {
                    const queryParamsBody = arr[0];
                    if (queryParams.bu.toLowerCase() === queryParamsBody.bu.toLowerCase()) {
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
                        this.rowAdditionService.setGroupBySelectedOption(this.groupBySelectedOption,
                            this.existingFilters);
                        this.renderDataAndAckWithToaster(JSON.stringify(payload), startDateFromParams,
                            endDateFromParams);
                    }
                },
                (error) => {
                    console.error('Error fetching data:', error);
                },
            );
        }
        this.cookieService.setBUSelectedCookie(queryParams.bu.toUpperCase());
    }

    ngOnInit(): void {
        this.reportingCsvService.show$.subscribe(showLoader => {
            console.log("showLoader", showLoader);
            this.isShowTableLoader = showLoader || this.isShowTableLoader;
            console.log("this.isShowTableLoader", this.isShowTableLoader);
        });
        this.reportingCsvService.data$.subscribe(data => {
            if (data !== null) {
                let col_heading: any;
                if (data['inputPayload'] !== undefined) {
                    data = data['inputPayload'];
                }
                col_heading = Object.keys(data[0]);
                const metricHeadingsFromCookie = this.cookieService.getMaxMetricsSettingCookie();
                let metricsToBeShown = col_heading;
                if (metricHeadingsFromCookie) {
                    metricsToBeShown = JSON.parse(metricHeadingsFromCookie);
                    metricsToBeShown.unshift(col_heading[0]);
                    const replaceCount = col_heading.includes('enableCloseLevelButton') ? this.reportingConstants.MAX_REPORTING_METRICS.length + 1 : this.reportingConstants.MAX_REPORTING_METRICS.length;
                    col_heading.splice(-replaceCount, replaceCount, ...metricsToBeShown);
                }
                this.settings = this.reportingCsvService.getSettingsConfigForTable(metricsToBeShown,
                    this.defaultRowPerPage);
                this.tableDataToBeRendered = data;
                this.isShowTableLoader = false;
                this.tableDataToBeRendered = this.drilldownService.addEnableCloseLevelKey(this.tableDataToBeRendered);
                this.source.load(this.tableDataToBeRendered);
                this.enableFormatMetricsButton = true;
            }
        });

        // todo remove business unit based decisions as we have created a separate component for that
        if (this.cookieService.getBUSelectedFromCookie() === 'MAX') this.allowCsvUpload = true;
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
                this.reportingCsvService.sendData(newData);
            }
        });
        this.drilldownService.numberOfRowsPerPage$.subscribe(data => {
            this.defaultRowPerPage = data;
            this.setPager();
        });
    }

    resetTableAndEnableShareButton(isShowTableLoaderValue: boolean): void {
        this.isShowTableLoader = isShowTableLoaderValue;
        this.source.reset();
        this.tableDataToBeRendered = null;
        this.enableShareButton = true;
    }

    getPayload(): any {
        return  {
            'filters': this.reportingCsvService.removeEmptyValuesInPayload(JSON.parse(JSON.stringify(this.existingFilters))),
            'metrics': this.metricsOption,
            'dimensions': this.groupBySelectedOption,
            'bu': this.cookieService.getBUSelectedFromCookie(),
            'queryLevel': 1,
        };
    }


    setPager() {
        this.source.setPaging(1, this.defaultRowPerPage, true);
        this.settings = Object.assign({}, this.settings);
    }

    openModal(): void {
        this.userAnalyticsService.trackEventOnGA(this.googleAnalyticsConfig.RI_BT_CSV_UPLOAD);
        this.bulkModalPage.openModal(status => { });
    }

    onSearch(query: string = '') {
        this.userAnalyticsService.trackEventOnGA(this.googleAnalyticsConfig.RI_INP_TABLE_SEARCH);
        if (query.length > 0) {
            this.source.setFilter([
                {field: 'Template', search: query},
                {field: 'Template Size', search: query},
                {field: 'Ad Id', search: query},
                {field: 'AdGroup', search: query},
                {field: 'AUTO ASSET IMAGE ID', search: query},
                {field: 'AUTO ASSET C2A ID', search: query},
                {field: 'Demand basis', search: query},
            ], false);
        } else this.source.reset();
    }

    abortPreviousRequest() {
        const promise = this.reportingCsvService.cancelRequest({hash: this.generatedHash});
        promise.then(data => {
        }).catch(error => {
            this.isShowTableLoader = false;
            console.error('Error:', error);
        });
    }

    generateReports() {
        this.userAnalyticsService.trackEventOnGA(this.googleAnalyticsConfig.RI_BT_GENERATE_REPORTS);
        this.enableFormatMetricsButton = false;
        this.isShowCalendar = false;
        this.resetTableAndEnableShareButton(true);
        const date = this.dateService.getStartDateAndEndDate(this.selectedRange);
        this.selectedRange = {start: new Date(date.startDate), end: new Date(date.endDate)};
        this.formattedDateRangeForDisplay = this.dateService.formatDateRange(this.selectedRange);
        const payload = this.getPayload();
        payload['date'] = this.selectedRange;
        this.generatedHash = this.hashService.generateHash(JSON.stringify(payload));
        payload['hash'] = this.generatedHash;
        delete payload['date'];
        this.rowAdditionService.setGroupBySelectedOption(this.groupBySelectedOption,
                this.reportingCsvService.removeEmptyValuesInPayload(JSON.parse(JSON.stringify(this.existingFilters))));
        this.renderDataAndAckWithToaster(JSON.stringify(payload), date.startDate, date.endDate);
        this.redirForReporting(this.generatedHash);
    }

    renderDataAndAckWithToaster(payloadJson: string, startDate: string, endDate: string):
    any {
        if (this.cookieService.getBUSelectedFromCookie() === 'CM') return;
        const promise = this.reportingCsvService.getToptemplatesFromDruid(payloadJson, startDate,
            endDate, null);
            promise.then(data => {
                    this.toastingService.success('Generating Report', `Top <b>${data.lengthOfData}</b> records for ${data.startDate} to ${data.endDate}.`);
                    this.reportingCsvService.sendData(data.dataToBeRenderedInTable);
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

    handleSelectedRangeChange(newRange: NbCalendarRange<Date>) {
        this.selectedRange = newRange;
        this.formattedDateRangeForDisplay = this.dateService.formatDateRange(newRange);
    }

    showCalendar(booleanVal: boolean) { this.isShowCalendar = booleanVal; }

    closeModal(event: MouseEvent) { if (event.target === event.currentTarget) this.showCalendar(false); }

    preventClose(event: MouseEvent) { event.stopPropagation(); }

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
            if (col_heading) {
                this.settings = Object.assign({}, this.reportingCsvService.getSettingsConfigForTable(col_heading,
                    this.defaultRowPerPage));
            }
        });
        this.userAnalyticsService.trackEventOnGA( this.googleAnalyticsConfig.RI_BT_METRICS_MANIPULATION);
    }

    openFiltersModal(currentDimension) {
        if (this.isDisableFilterChipClick(currentDimension)) return;
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
                const selectedFilterValue = data.selectedFilterValues;
                this.existingFilters = this.reportingCsvService.getUpdatedFilterValues(dimension,
                    selectedFilterValue, this.existingFilters);
            }
        });
    }

    removeFilterChip(filterKey: string): void {
        if (this.existingFilters.hasOwnProperty(filterKey)) {
            delete this.existingFilters[filterKey];

        }
    }

    isDisableFilterChipClick(filter: string): boolean {
        return filter === 'Campaign Type';
    }

    isShowAddFiltersButton(): boolean {
        return Object.keys(this.existingFilters).length < this.reportingConstants.MAX_FILTER_CHIPS_LIST.length + 2;
    }
}