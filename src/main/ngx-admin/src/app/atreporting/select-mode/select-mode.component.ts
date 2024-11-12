import {AfterViewInit, Component, Inject, OnInit, ViewChild} from '@angular/core';
import {AutoAssetService} from '../../@core/data/auto_asset/auto-asset.service';
import {MatDialogRef, MAT_DIALOG_DATA} from '@angular/material/dialog';
import {ReportingCsvService} from '../../@core/data/reporting-csv.service';
import {ConversionMapService} from '../../@core/data/conversion-map.service';
import {NgSelectComponent} from '@ng-select/ng-select';
import {ReportingConstantsService} from "../../@core/data/reporting-constants.service";

@Component({
    selector: 'select-mode',
    templateUrl: './select-mode.component.html',
    styleUrls: ['./select-mode.component.css'],
})
export class SelectModeComponent implements OnInit, AfterViewInit {
    filterDropdownList: Array<string>;
    selectTypeList = ['Equal', 'Not Equal', 'Contains'];
    dropdownSuggestionOptions = [];
    selectedSelectType = 'Equal';
    selectedFilterDropdown: string;
    selectedFilterList = [];

    entityInput = '';
    entityLoader = false;
    globalFilters: any = {};
    bu: string;
    value: any;
    @ViewChild('mySelect') mySelect: NgSelectComponent;

    constructor(private autoAssetService: AutoAssetService,
                @Inject(MAT_DIALOG_DATA) public data: any,
                public dialogRef: MatDialogRef<SelectModeComponent>,
                private reportingService: ReportingCsvService,
                private conversionMapService: ConversionMapService,
                private reportingConstants: ReportingConstantsService,
    ) {
        if (data && data.globalFilters) {
            this.globalFilters = data.globalFilters;
            this.bu = data.bu;
            this.filterDropdownList = this.getFilterList();
            this.filterDropdownList = this.filterDropdownList.filter(key => !this.globalFilters.hasOwnProperty(key));
            this.selectedFilterDropdown = this.filterDropdownList[0];
            if (data.currentDimension) {
                this.selectedFilterDropdown = data.currentDimension;
                const filtersForSelectedEntity = this.globalFilters[this.selectedFilterDropdown];
                for (const filter of filtersForSelectedEntity) {
                    this.selectedSelectType = filter.state;
                    for (const value of filter.values) {
                        this.addItem(value);
                    }
                }
            }
        }
    }

    ngOnInit(): void {
    }

    ngAfterViewInit() {
        this.getDropdownOptionSuggestion({ target: { value: '' } }, this.selectedFilterDropdown);
    }

    clearValues() {
        this.selectedFilterList = [];
        this.dropdownSuggestionOptions = [];
        this.getDropdownOptionSuggestion({ target: { value: '' } }, this.selectedFilterDropdown);
    }

    addItem(item) {
        if (item) {
            this.dropdownSuggestionOptions = this.dropdownSuggestionOptions.filter(
                option => option !== item
            );
            const newItem = {selectType: this.selectedSelectType, value: item};
            if (!this.isDuplicate(newItem)) {
                this.selectedFilterList.push(newItem);
            }
        }
    }

    removeItem(item) {
        if (item) {
            this.dropdownSuggestionOptions.push(item.value);
            const index = this.selectedFilterList.indexOf(item, 0);
            if (index > -1) {
                this.selectedFilterList.splice(index, 1);
            }
        }
    }

    isDuplicate(newItem: { selectType: string; value: string }): boolean {
        return this.selectedFilterList.some(item =>
            item.value === newItem.value,
        );
    }

    handleEnterKeyForFilters($event) {
        this.addItem($event.target.value);
    }

    async getDropdownOptionSuggestion(event, selectedDimension) {
        this.entityInput = event.target.value;
        this.entityLoader = true;
        const data = await this.reportingService.insertRelevantFiltersOnlyInDropdown(this.globalFilters,
            selectedDimension, this.entityInput)
            .then((options) => {
                this.entityLoader = true;
                const dimension = this.conversionMapService.frontendToBackendEnumMap[selectedDimension]
                    .toLowerCase();
                const convertKeysToLowerCase = (obj) => {
                    const converted = {};
                    for (const key in obj) {
                        if (Object.prototype.hasOwnProperty.call(obj, key)) {
                            converted[key.toLowerCase()] = obj[key];
                        }
                    }
                    return converted;
                };
                const filteredDropdownOptions: string[] = [];
                this.dropdownSuggestionOptions = Object.values(options).map((item) => {
                    const itemLowerCaseKeys = convertKeysToLowerCase(item);
                    const dropdownValue = itemLowerCaseKeys[dimension];
                    if (!this.selectedFilterList.some(filter => filter.value === dropdownValue)) {
                        filteredDropdownOptions.push(dropdownValue);
                        return dropdownValue;
                    }
                    return '';
                });
                this.dropdownSuggestionOptions = filteredDropdownOptions;
            })
            .catch(error => {
                console.error('Error:', error);
            });
        this.entityLoader = false;
    }

    getSelectTypeClass(selectType: string): string {
        switch (selectType) {
            case 'Equal':
                return 'selected-item';
            case 'Not Equal':
                return 'not-selected-item';
            case 'Contains':
                return 'contains-item';
            default:
                return '';
        }
    }

    getFilterList() {
        if (this.bu === 'MAX')
            return this.reportingConstants.MAX_FILTER_CHIPS_LIST;
        else return this.reportingConstants.CM_FILTER_CHIPS_LIST;
    }

    addFilter(entity) {
        this.dialogRef.close({selectedFilterHeading: this.selectedFilterDropdown, selectedFilterValues: this.selectedFilterList});
        this.selectedFilterList = [];
    }

}
