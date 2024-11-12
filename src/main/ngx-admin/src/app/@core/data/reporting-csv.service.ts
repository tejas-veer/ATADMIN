import {Injectable} from '@angular/core';
import {ToastingService} from '../utils/toaster.service';
import {BehaviorSubject, Observable} from 'rxjs';
import {DomSanitizer, SafeHtml} from '@angular/platform-browser';
import {CookieService} from './cookie.service';
import {BaseUrl} from './base-url.service';
import {HttpClient, HttpParams} from '@angular/common/http';
import {DateService} from './date.service';
import {
    DrilldownComponent,
} from '../../atreporting/custom-template-for-table/drilldown.component';
import {ReportingConstantsService} from './reporting-constants.service';
import {UtilService} from "../utils/util.service";

@Injectable({
    providedIn: 'root',
})
export class ReportingCsvService {
    validReportingHeaders: String[] = ['Template', 'Template Size', 'Ad Id'];
    private dataSubject = new BehaviorSubject<any>(null);
    data$ = this.dataSubject.asObservable();
    private showSubject = new BehaviorSubject<boolean>(false);
    show$ = this.showSubject.asObservable();
    filters: any = null;
    ongoingReq: any;

    constructor(private toastingService: ToastingService,
                private sanitizer: DomSanitizer,
                private cookieService: CookieService,
                private http: HttpClient,
                private dateService: DateService,
                private reportingConstants: ReportingConstantsService,
                private utilService: UtilService,
    ) {
    }

    sendData(data: any) {
        this.dataSubject.next(data);
    }

    isReportingCsvValid(headers: Array<String>): boolean {
        return this.validReportingHeaders.every(ele => headers.indexOf(ele) !== -1);
    }

    getReportingData(results: any, isCSVData: boolean): Array<any> {
        let data = [];
        if (isCSVData && !this.isReportingCsvValid(results[0])) {
            this.toastingService.error('Column Missing', 'Column missing or column name is not as expected');
        } else
            data = this.getReportingCsvData(results, isCSVData);
        return data;
    }

    isGrandTotalRow(rowResults: any): boolean {
        if (rowResults['Template'] === '' && rowResults['Ad Id'] === '' && rowResults['Template Size'] === '') {
            return true;
        }
        return false;
    }

    extractTemplateIdAndName(source: string): any {
        let match = source.match(/\[(\d+)\]/);
        if (match === undefined || match === null) {
            match = source.match(/(\d+)/);
        }
        if (match === undefined || match === null) {
            return null;
        }
        return {
            templateId: parseInt(match[1]),
            templateNameAndId: source
        };
    }

    getReportingRow(rowResult: any, columnHeadings: any, isCSVData: boolean): any {
        const rowData = {};
        let templateNameAndId: string;
        let templateId: number;
        let value: any;

        for (let j = 0; j < columnHeadings.length; j++) {
            const columnName = columnHeadings[j];

            if (columnName === 'Template') {
                const source = isCSVData ? rowResult[j] : rowResult[columnName];
                const templateInfo = this.extractTemplateIdAndName(source);
                if (templateInfo !== null) {
                    templateId = templateInfo.templateId;
                    templateNameAndId = templateInfo.templateNameAndId;
                }
                continue;
            }

            value = isCSVData === true ? rowResult[j] : rowResult[columnName];
            if (this.utilService.isNumber(value)) {
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

    getColumnHeadingsAndStartPoint(results: any, isCsvData: boolean): any {
        if (isCsvData) {
            return {columnHeadings: results[0], startPointForIteration: 1};
        }
        return {columnHeadings: Object.keys(results[0]), startPointForIteration: 0};
    }

    getColumnHeadingForTemplateOrImage(columnHeadings: Array<string>): string {
        if (columnHeadings.includes('Template')) return 'Template';
        else if (columnHeadings.includes('AUTO ASSET IMAGE ID')) return 'Image';
        else return null;
    }

    getReportingCsvData(results: any, isCSVData: boolean): Array<any> {
        const columnHeadingsAndStartPoint = this.getColumnHeadingsAndStartPoint(results, isCSVData);
        const columnHeadings = columnHeadingsAndStartPoint.columnHeadings;
        const data = [];
        const columnHeadingForTemplateOrImage = this.getColumnHeadingForTemplateOrImage(columnHeadings);

        for (let i = columnHeadingsAndStartPoint.startPointForIteration; i < results.length; i++) {
            let html: any;
            let rowDataWithUrl: any;
            if (this.isGrandTotalRow(results[i]) === true) // no need to check always
                continue;
            const reportingRowData = this.getReportingRow(results[i], columnHeadings, isCSVData);
            const templateIdForMetrics = reportingRowData.templateId;
            const templateNameAndId = reportingRowData.templateNameAndId;
            const rowData = reportingRowData.rowData;
            rowData["Template Id"] = reportingRowData["templateId"];
            rowData["Template Name And Id"] = reportingRowData["templateNameAndId"];
            const templateRenderingUrl = this.getTemplateUrl(templateIdForMetrics, rowData['Template Size'],
                templateNameAndId, rowData['Ad Id'], rowData['Display Topic'], rowData['AUTO ASSET IMAGE ID'],
                rowData['AUTO ASSET C2A ID'], rowData['Display Topic']);

            if (columnHeadingForTemplateOrImage !== null) {
                if (templateRenderingUrl !== null) {
                    if (templateRenderingUrl.template === 'Image')
                        html = this.getTemplateIframeHtml(templateRenderingUrl.src, templateRenderingUrl.height,
                            templateRenderingUrl.width, rowData['AUTO ASSET IMAGE ID']);
                    else
                        html = this.getTemplateIframeHtml(templateRenderingUrl.src, templateRenderingUrl.height,
                            templateRenderingUrl.width, templateRenderingUrl.template);
                } else
                    html = `No Template/Image exists`;
                if (columnHeadingForTemplateOrImage === 'Template')
                    rowDataWithUrl = {Template: html, ...rowData};
                else
                    rowDataWithUrl = {Image: html, ...rowData};
            } else
                rowDataWithUrl = {...rowData};
            data.push(rowDataWithUrl);
        }
        return data;
    }

    getTemplateIframeHtml(src: string, height: string, width: string, template: string): string {
        if (template === 'Image') {
            const url = new URL(src);
            template = url.searchParams.get('imgUrl');
        }
        const divWidth = parseFloat(width) + 10;
        const divHeight = parseFloat(height) + 10;
        return `<div style="width: ${divWidth}px; height: ${divHeight}px; border: 1px solid lightgray; padding: 5px; box-sizing: content-box; display: flex; align-items: center; justify-content: center; background-color: #eee; opacity: 1; background-image: repeating-linear-gradient(45deg, #c2c2c2 25%, transparent 25%, transparent 75%, #c2c2c2 75%, #c2c2c2), repeating-linear-gradient(45deg, #c2c2c2 25%, #eee 25%, #eee 75%, #c2c2c2 75%, #c2c2c2); background-position: 0 0, 8px 8px; background-size: 16px 16px;">
                    <iframe src="${src}" width="${width}" height="${height}" marginheight="0" marginwidth="0" frameborder="1.5" scrolling="NO" style="border: none;"></iframe>
                </div>
                <p><b>${template}</b></p>`;
    }

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

    getTemplateUrl(templateId: number, size: string, template: string, adid: string, title: string,
                   image: string, c2a: string, displayTopic: string): any {
        const heightAndWidth = this.getHeightAndWidthForTemplateOrImageUrl(size);
        let width = heightAndWidth.width;
        let height = heightAndWidth.height;
        let src: string;
        if (this.filters) {
            if (!title && this.filters.AUTO_ASSET_TITLE_ID) title = this.filters.AUTO_ASSET_TITLE_ID;
            if (!image && this.filters.IMAGE_ID) image = this.filters.IMAGE_ID;
            if (!c2a && this.filters.C2A_ID) c2a = this.filters.C2A_ID;
            if (!displayTopic && this.filters.Provider_Topic) displayTopic = this.filters.Provider_Topic;
        }
        if (template === null || template === undefined) {
            if (size === '1x1') {
                template = this.reportingConstants.PURE_NATIVE_REFERENCE_TEMPLATE.name;
                templateId = this.reportingConstants.PURE_NATIVE_REFERENCE_TEMPLATE.id;
                width = this.reportingConstants.PURE_NATIVE_REFERENCE_TEMPLATE.width;
                height = this.reportingConstants.PURE_NATIVE_REFERENCE_TEMPLATE.height;
            } else if (image !== null && image !== undefined && image !== 'null') {
                src = this.getAssetAfterRemovalOfAssociatedNumber(image);
                template = 'Image';
                templateId = this.reportingConstants.ONLY_IMAGE_TEMPLATE;
            } else return null;
        }
        src = this.cookieService.getCurrentApiPrefix() + `template_test.jsp?tid=${templateId}&width=${width}&height=${height}`;
        if ((image !== 'null' || c2a !== 'null' || displayTopic !== 'null' || title !== 'null')
            && (image !== undefined || c2a !== undefined || displayTopic !==
                undefined || title !== undefined)) {
            src = this.getTemplateUrlBasedOnAssets(image, c2a, displayTopic, src, adid, title);
        } else if (adid !== 'null' && adid !== null && adid !== undefined)
            src = this.cookieService.getCurrentApiPrefix() + `template_test.jsp?tid=${templateId}&width=${width}&height=${height}&adid=${adid}`;
        return {
            'src': src,
            'height': height,
            'width': width,
            'template': template,
        };
    }

    getAssetAfterRemovalOfAssociatedNumber(asset: string): string {
        const pattern = / - \[\d+\]/g;
        if (asset !== 'null' && asset !== undefined && asset !== null) {
            if (pattern.test(asset)) {
                asset = asset.replace(pattern, '');
            }
        }
        return asset;
    }

    equalsIgnoreCase(str1: string, str2: string): boolean {
        return str1.toUpperCase() === str2.toUpperCase();
    }

    getTemplateUrlBasedOnAssets(image: string, c2a: string, displayTopic: string, src: string, adid: string,
                                title: string): string {
        console.log('template' , title);
        const templateUrlBasedOnAssets = new URL(src);
        let isOv1: boolean = false;
        if (image !== undefined && !this.equalsIgnoreCase(image, 'null') && !this.equalsIgnoreCase(image, 'na')) {
            templateUrlBasedOnAssets.searchParams.append('imgUrl',
                this.getAssetAfterRemovalOfAssociatedNumber(image));
            isOv1 = true;
        }
        if (c2a !== undefined && !this.equalsIgnoreCase(c2a, 'null') && !this.equalsIgnoreCase(c2a, 'na')) {
            templateUrlBasedOnAssets.searchParams.append('c2a',
                this.getAssetAfterRemovalOfAssociatedNumber(c2a));
            isOv1 = true;
        }
        if (title !== undefined && !this.equalsIgnoreCase(title, 'null') && !this.equalsIgnoreCase(title, 'na')) {
            templateUrlBasedOnAssets.searchParams.append('title',
                this.getAssetAfterRemovalOfAssociatedNumber(title));
            isOv1 = true;
        }
        if (displayTopic !== undefined && !this.equalsIgnoreCase(displayTopic, 'null')) {
            templateUrlBasedOnAssets.searchParams.append('it',
                this.getAssetAfterRemovalOfAssociatedNumber(displayTopic));
            isOv1 = true;
        }
        if (adid !== undefined && !this.equalsIgnoreCase(adid, 'null')) {
            templateUrlBasedOnAssets.searchParams.append('adid', adid);
        }
        if (isOv1 === true) templateUrlBasedOnAssets.searchParams.append('ov', '1');
        return templateUrlBasedOnAssets.toString();
    }

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
        if (!Array.isArray(col_heading)) {
            console.error('col_heading is not iterable. Expected array, but got:', col_heading);
            return {}; // Return an empty object or handle the error appropriately
        }
        const columns = {};
        let firstColumn: boolean = true;
        for (const heading of col_heading) {
            if (heading === 'Template' || heading === 'Image' || firstColumn === true) {
                columns[heading] = {
                    title: heading,
                    type: 'custom',
                    renderComponent: DrilldownComponent,
                    filter: false,
                    valuePrepareFunction: (cell, row) => {
                        const safeHtml: SafeHtml = this.sanitizer.bypassSecurityTrustHtml(cell);
                        return {html: safeHtml};
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

    setShow(value: boolean) {
        this.showSubject.next(value);
    }

    handleInvalidOrEmptyResponse(response: any): void {
        if (response === null || response === 'null' || response === undefined) {
            this.toastingService.error('API Query Failed', 'Failed to fetch data from the API. Please try again.');
            this.setShow(false);
        }
        if (Array.isArray(response) && response.length === 0) {
            this.toastingService.warning('No data exists for selected filters', 'Please enter valid filters');
            this.setShow(false);
        }
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

    getUpdatedFilterValues(dimension: string, selectedFilters: Array<any>,
                           existingFilters: any): any {
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

    getToptemplatesFromDruid(payload: Object, startDate: string, endDate: string, filters: any):
        Promise<any> {
        const url = BaseUrl.get() + '/druid/query/topTemplatesForSelectedFilters';
        if (filters)
            this.filters = filters;
        const params = new HttpParams()
            .set('startDate', startDate)
            .set('endDate', endDate);
        return this.http.post(url, payload, {params}).toPromise()
            // todo : reporting service should not behave according to caller
            // todo : can we pass two event one for success and one for error from caller itself
            .then((response: any) => {
                this.handleInvalidOrEmptyResponse(response);
                const dataToBeRendered = this.getReportingData(response, false);
                console.log()
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

    async getTopDemandBasis(payload: Object): Promise<any> {
        const url = BaseUrl.get() + '/druid/query/topDemandBasis';
        const params = new HttpParams();

        return await this.http.post(url, payload, {params}).toPromise()
            .then((response: any) => {
                return response;
            })
            .catch(error => {
                this.toastingService.error('Failed to fetch', `Unable to fetch top Demand Basis`);
                console.error('Error:', error);
                throw error;
            });
    }

    insertRelevantFiltersOnlyInDropdown(filters: any, search_dimension: string,
                                        search_input: string): Promise<any> {
        const url = BaseUrl.get() + '/search/connectFilters/value';
        const params = new HttpParams()
            .set('search_dimension', search_dimension)
            .set('search_input', search_input)
            .set('buSelected', this.cookieService.getBUSelectedFromCookie());

        return this.http.post(url, filters, {params}).toPromise()
            .then(response => response)
            .catch(error => {
                console.error('Error:', error);
                throw error;
            });
    }

    getPayloadFromHash(hash: string): Observable<any> {
        const url = BaseUrl.get() + '/druid/query/hash';
        const hashObject = {hash: hash};
        return this.http.post(url, hashObject);
    }
}
