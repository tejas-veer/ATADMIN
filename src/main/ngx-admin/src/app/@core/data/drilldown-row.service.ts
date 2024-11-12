import {Injectable} from '@angular/core';
import {ReportingCsvService} from './reporting-csv.service';
import {NbCalendarRange} from '@nebular/theme';
import {ToastingService} from '../utils/toaster.service';
import {BehaviorSubject} from 'rxjs';
import {ConversionMapService} from './conversion-map.service';
import {DateService} from './date.service';
import {ReportingConstantsService} from "./reporting-constants.service";
import {UtilService} from "../utils/util.service";

@Injectable({
    providedIn: 'root',
})
export class DrilldownRowService {
    private numberOfRowsPerPage = new BehaviorSubject<number>(10);
    numberOfRowsPerPage$ = this.numberOfRowsPerPage.asObservable();
    filtersToBeRenderedInTemplate: any;

    constructor(private reportingCsvService: ReportingCsvService,
                private toastingService: ToastingService,
                private conversionMapService: ConversionMapService,
                private dateService: DateService,
                private reportingConstants: ReportingConstantsService,
                private utilService: UtilService) {
    }

    setNumberOfRowsPerPage(value: number) {
        this.numberOfRowsPerPage.next(value);
    }

    getLevel(startingRowData: any): number {
        if (startingRowData.hasOwnProperty('Level')) return startingRowData['Level'] + 1;
        else return 1;
    }

    getTemplateId(startingRowData: any): number | string {
        let templateFilterValue: string = startingRowData['Template'];
        if (templateFilterValue) {
            const regex = /(.+) - \[(\d+)\]/;
            const match = regex.exec(templateFilterValue);
            if (match && match[2]) templateFilterValue = match[2];
        }
        if (startingRowData['Template Size'] == '1x1') {
            templateFilterValue = '';
        }
        return templateFilterValue;
    }

    getFiltersForChildRows(startingRowData: any): any {
        const parentRowFilters = startingRowData['ParentRowFilters'];
        let currentRowFilters: Record<string, any> = {};
        this.reportingConstants.MAX_FILTER_CHIPS_LIST.forEach(key => {
            if (key === 'Template')
                currentRowFilters[key] = this.getTemplateId(startingRowData);
            else
                currentRowFilters[key] = startingRowData[key];
        });
        currentRowFilters = this.utilService.removeEmptyValuesInDrilldown(currentRowFilters);
        let filters: any = {
            ...(parentRowFilters || {}),
            ...(currentRowFilters || {}),
        };
        this.filtersToBeRenderedInTemplate = {
            ...(parentRowFilters || {}),
            ...(currentRowFilters || {}),
        };
        this.filtersToBeRenderedInTemplate = this.flattenFiltersForRenderingInTemplate(this.filtersToBeRenderedInTemplate);
        filters = this.utilService.removeEmptyValuesInDrilldown(filters);
        const filtersAlongWithStates: any = {};
        for (const [key, value] of Object.entries(filters)) {
            if (typeof value === 'string') {
                filtersAlongWithStates[key] = [{
                    state: 'Equal',
                    values: [value],
                }];
            } else {
                filtersAlongWithStates[key] = value;
            }
        }
        return filtersAlongWithStates;
    }

    flattenFiltersForRenderingInTemplate(obj: any) {
        const result: any = {};
        for (const key in obj) {
            if (Array.isArray(obj[key]) && obj[key].length === 1 && obj[key][0].values !== undefined) {
                result[this.conversionMapService.frontendToBackendEnumMap[key]] = obj[key][0].values[0];
            } else {
                result[this.conversionMapService.frontendToBackendEnumMap[key]] = obj[key];
            }
        }
        return result;
    }

    getDruidResponse(filters, metricsOption, selectedGroupByOption, selectedRange, globalFilters): Promise<any> {
        const filtersWithGlobalFilters: any = {
            ...globalFilters,
            ...(filters || {}),
        };
        const payload = {
            'filters': filtersWithGlobalFilters,
            'metrics': metricsOption,
            'dimensions': [selectedGroupByOption],
            'queryLevel': 2,
        };
        const date = this.dateService.getStartDateAndEndDate(selectedRange);
        return this.reportingCsvService.getToptemplatesFromDruid(JSON.stringify(payload), date.startDate,
            date.endDate, this.filtersToBeRenderedInTemplate);
    }

    updateRowsForTable(dataToBeRenderedInTable: any, currentTableData: any, filters: any, startingRowData: any,
                       defaultRowPerPage: number, selectedGroupByOption: string) {
        let firstColumnKey = null;
        const rowIndex = currentTableData.findIndex((row) => row === startingRowData);
        let childRowsLength: number = 10;

        const newDataToBeRendered = dataToBeRenderedInTable.map(item => {

            const modifiedItem = {...item};
            for (const key in modifiedItem) {
                if (modifiedItem.hasOwnProperty(key) && firstColumnKey === null)
                    firstColumnKey = key;
            }

            if (firstColumnKey) {
                modifiedItem['Heading'] = modifiedItem[firstColumnKey];
                modifiedItem['Template Id'] = startingRowData['Template Id'] || modifiedItem['Template Id'];
                modifiedItem['Template Size'] = startingRowData['Template Size'] || modifiedItem['Template Size'];
                modifiedItem['Template Name And Id'] = startingRowData['Template Name And Id'] || modifiedItem['Template Name And Id'];
                if (selectedGroupByOption === "All Assets") {
                    firstColumnKey = "All Assets"
                    let html: any = `No Template/Image exists`;
                    const templateRenderingUrl = this.reportingCsvService.getTemplateUrl(modifiedItem["Template Id"], modifiedItem["Template Size"],
                        modifiedItem["Template Name And Id"], item['Ad Id'], item['Display Topic'], item['AUTO ASSET IMAGE ID'],
                        item['AUTO ASSET C2A ID'], item['Display Topic'])
                    if (this.utilService.isSet(templateRenderingUrl)) {
                        html = this.reportingCsvService.getTemplateIframeHtml(templateRenderingUrl.src, templateRenderingUrl.height,
                            templateRenderingUrl.width, templateRenderingUrl.template);
                    }
                    modifiedItem['Heading'] = html;
                }
            }

            const currentRowDimension = this.conversionMapService.frontendToBackendEnumMap[firstColumnKey] || firstColumnKey;
            return {
                ...modifiedItem, ParentRowFilters: filters, Level: this.getLevel(startingRowData),
                currentRowDimension: currentRowDimension,
            };
        });
        if (newDataToBeRendered.length < 10)
            childRowsLength = newDataToBeRendered.length;

        currentTableData.splice(rowIndex + 1, 0, {
            Heading: `<b>${firstColumnKey} (${childRowsLength})</b>`,
            Level: this.getLevel(startingRowData),
            DisableDrilldownButton: true,
        });
        currentTableData.splice(rowIndex + 2, 0, ...newDataToBeRendered.slice(0, 10));
        this.reportingCsvService.sendData(currentTableData);
        this.setNumberOfRowsPerPage(Math.max(defaultRowPerPage, defaultRowPerPage + childRowsLength + 1));
    }

    updateChildRowsInTable(startingRowData: any, selectedGroupByOption: string, globalFilters: any,
                           metricsOption: string, selectedRange: NbCalendarRange<Date>, currentTableData: any[],
                           defaultRowPerPage: number) {
        currentTableData = this.deleteChildRowsIfSameStartingRow(currentTableData, startingRowData);
        const filters = this.getFiltersForChildRows(startingRowData);
        const promise = this.getDruidResponse(filters, metricsOption, selectedGroupByOption,
            selectedRange, globalFilters);
        promise.then(data => {
            if (data.dataToBeRenderedInTable.length > 0) {
                this.updateRowsForTable(data.dataToBeRenderedInTable, currentTableData, filters, startingRowData,
                    defaultRowPerPage, selectedGroupByOption);
            } else this.toastingService.info('Selected filters dont exist', 'Please choose valid filters');
        });
    }

    deleteChildRowsIfSameStartingRow(currentTableData: any[], startingRowData: any): any[] {
        const startingRowIndex = currentTableData.indexOf(startingRowData);
        let nextRowIndex = startingRowIndex + 1;
        const startingLevel = startingRowData['Level'];

        while (nextRowIndex < currentTableData.length) {
            const nextRow = currentTableData[nextRowIndex];
            const nextLevel = nextRow['Level'];
            if (nextLevel === undefined || nextLevel < startingLevel || nextLevel === startingLevel) {
                break;
            }
            nextRowIndex += 1;
        }
        if (startingRowIndex + 1 !== nextRowIndex) {
            currentTableData.splice(startingRowIndex + 1, nextRowIndex - startingRowIndex - 1);
        }
        return currentTableData;
    }

    addEnableCloseLevelKey(currentTableData: any[]): any[] {
        for (let i = 0; i < currentTableData.length - 1; i++) {
            const currentRow = currentTableData[i];
            const nextRow = currentTableData[i + 1];

            if (currentRow.Level && nextRow && nextRow.Level > currentRow.Level) {
                currentRow.enableCloseLevelButton = true;
            } else if (!currentRow.Level && nextRow && nextRow.Level) {
                currentRow.enableCloseLevelButton = true;
            } else {
                currentRow.enableCloseLevelButton = false;
            }
        }
        return currentTableData;
    }
}
