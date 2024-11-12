import {Injectable} from '@angular/core';
import {NbCalendarRange} from "@nebular/theme";
import {BaseUrl} from "./base-url.service";
import {HttpClient, HttpParams} from "@angular/common/http";
import {DateService} from "./date.service";
import {CookieService} from "./cookie.service";
import {ToastingService} from "../utils/toaster.service";
import {DomSanitizer, SafeHtml} from "@angular/platform-browser";
import {BehaviorSubject, Observable} from "rxjs";
import {CmTemplateComponent} from "../../atreporting/cm-reporting/cm-template/cm-template.component";

@Injectable({
    providedIn: 'root'
})
export class CmReportingService {
    validReportingHeaders: String[] = ['Template'];
    private dataSubject = new BehaviorSubject<any>(null);
    data$ = this.dataSubject.asObservable();
    private showSubject = new BehaviorSubject<boolean>(false);
    show$ = this.showSubject.asObservable();
    filters: any = null;

    constructor(private toastingService: ToastingService,
                private sanitizer: DomSanitizer,
                private cookieService: CookieService,
                private http: HttpClient,
                private dateService: DateService) {

    }

    sendData(data: any) {
        this.dataSubject.next(data);
    }

    getReportingCSVData(results: any): Array<any> {
        let data = [];
        if (!this.isReportingCsvValid(results[0])) {
            this.toastingService.error('Column Missing', 'Column missing or column name is not as expected');
        } else
            data = this.getReportingData(results, true);
        return data;
    }

    isReportingCsvValid(headers: Array<String>): boolean {
        return this.validReportingHeaders.every(ele => headers.indexOf(ele) !== -1);
    }

    getReportingDruidData(results: any): Array<any> {
        let data = [];
        data = this.getReportingData(results, false);
        return data;
    }

    getColumnHeadingsAndStartPoint(results: any, isCsvData: boolean): any {
        if (isCsvData) {
            return {columnHeadings: results[0], startPointForIteration: 1};
        }
        return {columnHeadings: Object.keys(results[0]), startPointForIteration: 0};
    }

    isGrandTotalRow(rowResults: any): boolean {
        if (rowResults['Template'] === '' && rowResults['Template Size'] === '') {
            return true;
        }
        return false;
    }

    //same as of reportingCSV // can be copied to a new Common service
    getReportingRow(rowResult: any, columnHeadings: any, isCSVData: boolean): any {
        const rowData = {};
        let templateNameAndId: string;
        let templateId: number;
        let value: any;

        for (let j = 0; j < columnHeadings.length; j++) {
            const columnName = columnHeadings[j];
            if (isCSVData && columnHeadings[j] === 'Template') {
                const match = rowResult[j].match(/\[(.*?)\]/);
                if (match === undefined || match === null)
                    continue;
                templateId = match[1];
                templateNameAndId = rowResult[j];
                continue;
            }
            if (!isCSVData && columnName === 'Template') {
                const match = rowResult[columnName].match(/\[(.*?)\]/);
                if (match === undefined || match === null)
                    continue;
                templateId = parseInt(match[1]);
                templateNameAndId = rowResult[columnName];
                continue;
            }
            value = isCSVData === true ? rowResult[j] : rowResult[columnName];
            if (!isNaN(parseFloat(value))) {
                const valueAsString = value.toString();
                if (valueAsString.includes('.')) {
                    rowData[columnName] = parseFloat(value).toFixed(3);
                } else {
                    rowData[columnName] = value;
                }
            } else {
                rowData[columnName] = value;
            }
        }
        return {
            'templateId': templateId,
            'templateNameAndId': templateNameAndId,
            'rowData': rowData,
        };
    }

    getReportingData(results: any, isCsvData: boolean): Array<any> {
        const columnHeadingsAndStartPoint = this.getColumnHeadingsAndStartPoint(results, isCsvData);
        const columnHeadings = columnHeadingsAndStartPoint.columnHeadings;
        const data = [];

        for (let i = columnHeadingsAndStartPoint.startPointForIteration; i < results.length; i++) {
            let html: any;
            let rowDataWithUrl: any;
            if (isCsvData && this.isGrandTotalRow(results[i]) === true)
                continue;
            const reportingRowData = this.getReportingRow(results[i], columnHeadings, isCsvData);
            const templateIdForMetrics = reportingRowData.templateId;
            const templateNameAndId = reportingRowData.templateNameAndId;
            const rowData = reportingRowData.rowData;
            const templateRenderingUrl = this.getTemplateUrl(templateIdForMetrics, rowData['Template Size'], templateNameAndId);


            if (templateRenderingUrl !== null) {
                html = this.getTemplateIframeHtml(templateRenderingUrl.src, templateRenderingUrl.height,
                    templateRenderingUrl.width, templateRenderingUrl.templateNameAndId);
            } else {
                html = `No Template/Image exists`;
            }
            rowDataWithUrl = {Template: html, ...rowData};
            data.push(rowDataWithUrl);
        }
        return data;
    }

    //similar to that of reportingCSV // can be copied to a new Common service
    getTemplateIframeHtml(src: string, height: string, width: string, template: string): string {
        let divHeight = parseInt(height) + 1;
        let divWidth = parseInt(width) + 1;
        return `<div style="width: ${divWidth}px; height: ${divHeight}px;border: 2px solid black; box-sizing: content-box;">
                    <iframe src="${src}" width="${width}" height="${height}" marginheight="0" marginwidth="0"
                     frameborder="1.5" scrolling="NO" style="border: none;"></iframe>
                </div>
                <p><b>${template}</b></p>`;
    }

    getTemplateUrl(templateId: number, size: string, templateNameAndId: string): any {
        const heightAndWidth = this.getHeightAndWidthForTemplateOrImageUrl(size);
        const width = heightAndWidth.width;
        const height = heightAndWidth.height;

        let src: string;
        src = this.cookieService.getCurrentApiPrefix() + `template.jsp?tid=${templateId}&width=${width}&height=${height}`;
        return {
            'src': src,
            'height': height,
            'width': width,
            'type': 'TEMPLATE',
            'templateNameAndId': templateNameAndId,
        };
    }

    //same as of reportingCSV // can be copied to a new Common service
    getHeightAndWidthForTemplateOrImageUrl(size: string): any {
        let width: string = '300';
        let height: string = '250';
        if (size !== null && size !== undefined) {
            const sizeParts = size.split(/x|X/);
            width = sizeParts[0];
            height = sizeParts[1];
        }
        return {width: width, height: height};
    }


    getAutoSelectContentBasedOnFilters(filterJsonFromSelectedFilters: any, search_dimension: string,
                                       search_input: string): Promise<any> {
        const url = BaseUrl.get() + '/search/connectFiltersV2/value';
        const payload = {
            'search_dimension': search_dimension,
            'search_input': search_input,
            'filters': filterJsonFromSelectedFilters,
            'buSelected': this.cookieService.getBUSelectedFromCookie()
        };

        return this.http.post(url, payload, {}).toPromise()
            .then(response => response)
            .catch(error => {
                console.error('Error:', error);
                throw error;
            });
    }

    getFilterJsonFromSelectedFiltersForSearch(dimensionWiseSelectedFilters: any): any {
        const filters: { [key: string]: string } = {};
        for (const key in dimensionWiseSelectedFilters) {
            if (dimensionWiseSelectedFilters.hasOwnProperty(key)) {
                if (dimensionWiseSelectedFilters[key] && dimensionWiseSelectedFilters[key].length > 0) {
                    if (Array.isArray(dimensionWiseSelectedFilters[key])) {
                        filters[key] = dimensionWiseSelectedFilters[key].join(',');
                    } else {
                        filters[key] = dimensionWiseSelectedFilters[key]
                    }
                }
            }
        }
        return filters;
    }

    getFilterJsonFromSelectedFilters(dimensionWiseSelectedFilters: any): any {
        const filters: { [key: string]: any } = {};
        for (const key in dimensionWiseSelectedFilters) {
            if (dimensionWiseSelectedFilters.hasOwnProperty(key)) {
                if (dimensionWiseSelectedFilters[key] && dimensionWiseSelectedFilters[key].length > 0) {
                    filters[key] = dimensionWiseSelectedFilters[key]
                }
            }
        }
        return filters;
    }

    cancelRequest(payload: Object): Promise<any> {
        const url = BaseUrl.get() + '/druid/query/cancelQuery';
        const params = new HttpParams();
        return this.http.post(url, payload, {params}).toPromise()
            .then((response: any) => {
                return response;
            })
            .catch(error => {
                console.error('Error:', error);
                throw error;
            });
    }

    //same as of reportingCSV // can be copied to a new Common service
    getReportingDruidResponse(payload: any, startDate: string, endDate: string, filters: any):
        Promise<any> {
        const url = BaseUrl.get() + '/druid/query/topTemplatesForSelectedFilters';
        if (filters)
            this.filters = filters;

        const params = new HttpParams()
            .set('startDate', startDate)
            .set('endDate', endDate);

        return this.http.post(url, payload, {params}).toPromise()
            .then((response: any) => {
                this.handleInvalidOrEmptyResponse(response);
                const dataToBeRendered = this.getReportingDruidData(response);
                return {
                    dataToBeRenderedInTable: dataToBeRendered,
                    startDate: this.dateService.formatDate(startDate.toString()),
                    endDate: this.dateService.formatDate(endDate),
                    lengthOfData: dataToBeRendered.length,
                };
            })
            .catch(error => {
                console.error('Error:', error);
                throw error;
            });
    }

    //same as of reportingCSV // can be copied to a new Common service
    handleInvalidOrEmptyResponse(response: any): void {
        if (response === null || response === 'null' || response === undefined) {
            this.toastingService.error('API Query Failed', 'Failed to fetch data from the API. Please try again.');
            this.setShow(false);
        }
        if (Array.isArray(response) && response.length === 0) {
            this.toastingService.error('No data exists for selected filters', 'Please enter valid filters');
            this.setShow(false);
        }
    }

    setShow(value: boolean) {
        this.showSubject.next(value);
    }

    //same as of reportingCSV // can be copied to a new Common service
    compareValues(direction: any, a: any, b: any): number {
        const numericRegex = /^-?\d+(\.\d+)?$/;

        if (numericRegex.test(a) && numericRegex.test(b)) {
            const first = parseFloat(a);
            const second = parseFloat(b);

            if (first < second) {
                return -1 * direction;
            }
            if (first > second) {
                return 1 * direction;
            }
            return 0;
        } else {
            const first = a.toString();
            const second = b.toString();

            if (first < second) {
                return -1 * direction;
            }
            if (first > second) {
                return 1 * direction;
            }
            return 0;
        }
    }

    calculateColumnsForTable(col_heading: string[]): any {
        const columns = {};
        let firstColumn: boolean = true;
        for (const heading of col_heading) {
            if (heading === 'Template' || heading === 'Image' || firstColumn === true) {
                columns[heading] = {
                    title: heading,
                    type: 'custom',
                    renderComponent: CmTemplateComponent,
                    filter: false,
                    valuePrepareFunction: (cell, row) => {
                        const safeHtml: SafeHtml = this.sanitizer.bypassSecurityTrustHtml(cell);
                        return { html: safeHtml };
                    },
                };
            } else {
                columns[heading] = {
                    title: heading,
                    sort: true,
                    filter: false,
                    type: 'html',
                    valuePrepareFunction: (cell, row) => {
                        const numericRegex = /^-?\d+(\.\d+)?$/;
                        if (numericRegex.test(cell)) {
                            const numericValue = parseFloat(cell);
                            return '<div class="text-right">' + numericValue + '</div>';
                        } else {
                            return '<div class="text-left">' + cell + '</div>';
                        }
                    },
                    compareFunction: this.compareValues.bind(this)
                };
            }
            firstColumn = false;
        }
        return columns;
    }

    getSettingsConfigForTable(col_heading: any, defaultRowPerPage: number): any {
        const newColumns = this.calculateColumnsForTable(col_heading);
        return {
            columns: newColumns,
            actions: false,
            filter: false,
            pager: {
                display: true,
                perPage: defaultRowPerPage
            },
            rowClassFunction: (row) => {
                let level = row.getData().Level;
                if (!level) level = 0;
                if (level > 8) level = 8;
                return 'level-' + level.toString() + '-row';
            },
        };
    }

    removeEmptyValuesInPayload(payload: any): any {
        for (const key in payload) {
            if (payload.hasOwnProperty(key) &&
                (payload[key] === '' || payload[key] === null || payload[key] === 'null' || payload[key] === undefined
                    || (Array.isArray(payload[key]) && payload[key].length === 0))) {
                delete payload[key];
            }
        }
        return payload;
    }

    getUpdatedFilterValues(dimension: string, selectedFilters: Array<any>,
                           existingFilters: any): any  {
        if (existingFilters.hasOwnProperty(dimension)) {
            delete existingFilters[dimension];
        }
        selectedFilters.forEach(filter => {
            const {selectType, value} = filter;
            if (!existingFilters[dimension]) {
                existingFilters[dimension] = [];
            }
            const index = existingFilters[dimension].findIndex(
                filterObj => filterObj.state === selectType,
            );
            if (index !== -1) {
                existingFilters[dimension][index].values.push(value);
            } else {
                existingFilters[dimension].push({
                    state: selectType,
                    values: [value],
                });
            }
        });
        return existingFilters;
    }

    formatFilterChipsData(data: any[]): any[] {
        const formattedData = [];

        for (const value of data) {
            for (const singleValue of value.values) {
                const formattedValue = {
                    value: singleValue,
                    state: value.state,
                };
                formattedData.push(formattedValue);
            }
        }
        return formattedData;
    }

    getPayloadFromHash(hash: string): Observable<any> {
        const url = BaseUrl.get() + '/druid/query/hash';
        const hashObject = {hash: hash};
        return this.http.post(url, hashObject);
    }
}
