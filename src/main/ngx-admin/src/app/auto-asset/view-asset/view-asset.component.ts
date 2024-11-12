import { Component, OnInit } from '@angular/core';
import { LocalDataSource } from 'ng2-smart-table';
import { AutoAssetService } from '../../@core/data/auto_asset/auto-asset.service';
import { AssetImageComponent } from '../shared-component/asset-image/asset-image.component';
import { AssetBlockUnblockComponent } from '../shared-component/asset-block-unblock/asset-block-unblock.component';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ToastingService } from '../../@core/utils/toaster.service';
import { ConversionMapService } from "../../@core/data/conversion-map.service";
import {AutoAssetConstantsService} from '../../@core/data/auto-asset-constants.service';
import { PreviewModalComponent } from '../shared-component/preview-modal/preview-modal.component';
import { UtilService } from '../../@core/utils/util.service';
import { ImageAssetService } from '../../@core/data/auto_asset/image-asset.service';
import { ImageWithToggleButtonComponent } from '../shared-component/image-with-toggle-button/image-with-toggle-button.component';
import { DynamicWidthCellComponent } from '../shared-component/dynamic-width-cell/dynamic-width-cell.component';

@Component({
    selector: 'view-asset',
    templateUrl: './view-asset.component.html',
    styleUrls: ['./view-asset.component.css', '../shared-style.css'],
})
export class ViewAssetComponent implements OnInit {
    dimensionMap: Map<string, any> = new Map();
    filterInput: any;
    entityValueInput: any;

    showLoader = false;
    waitingMessage = '';

    settings = {};
    assetData = [];
    source: LocalDataSource = new LocalDataSource();

    actions: {
        add: false,
        edit: false,
        delete: false,
    }

    selectedEntityValue: any = null;
    selectedAssetId: any = null;
    selectedIsActive: string = null;
    selectedSetIdName: string = null;
    selectedSetId: string = null;
    selectedSize: string = null;
    filterMap = new Map<String, String[]>()

    totalRows: number;
    toggledRows: any[] = [];
    filteredData: any[] = [];
    showMessage: boolean;

    entityNameDropdownMap: any;
    entityNameMap = this.imageAssetService.EntityNameMapForImage;

    entityName: any;
    entityValue: any;
    entityAssetSize: any;

    columns = {
        asset: {
            title: 'Asset',
            type: 'custom',
            filter: false,
            renderComponent: ImageWithToggleButtonComponent,
            valuePrepareFunction: (cell, row) => {
                return row.assetValue;
            },
            onComponentInitFunction: (instance) => {
                instance.toggledEvent.subscribe((data) => {
                    this.toggledRowData(data);
                    this.showPreviewInfoMessage();
                });
            },
        },
        entityName: {
            title: 'Entity Name',
            filter: false,
        },
        entityValue: {
            title: 'Entity Value',
            filter: false,
        },
        basis: {
            title: 'Basis',
            filter: false,
        },
        prompt: {
            title: 'Prompt',
            filter: false,
            type: 'custom',
            renderComponent: DynamicWidthCellComponent,
            valuePrepareFunction: (cell, row) => {
                return { width: '400px', value: row.prompt };
            },
        },
        assetValue: {
            title: 'Asset Value',
            filter: false,
            type: 'html',
            valuePrepareFunction: (cell, row) => {
                return `<a href="${row.assetValue}" style="color:#40dc7e" target="_blank">Click here</a>`;
            },
        },
        assetId: {
            title: 'Asset Id',
            filter: false,
        },
        extAssetId: {
            title: 'External Asset Id',
            filter: false,
            valuePrepareFunction: (cell, row) => {
                return row.hasOwnProperty('extAssetId') ? row.extAssetId : "NULL";
            },
        },
        assetType: {
            title: 'Asset Type',
            filter: false,
        },
        size: {
            title: 'Size',
            filter: false,
        },
        setId: {
            title: 'Set Id',
            filter: false,
            hide:true,
        },
        setIdName: {
            title: 'Set Id Name',
            filter: false,
        },
        keyValue: {
            title: 'Key Value',
            filter: false,
        }
    };

    viewAssetPreviewModalColumns = {
        entityName: {
            title: 'Entity Name',
            filter: false,
        },
        entityValue: {
            title: 'Entity Value',
            filter: false,
        },
        basis: {
            title: 'Basis',
            filter: false,
        },
        assetType: {
            title: 'Asset Type',
            filter: false,
        },
        assetId: {
            title: 'Asset Id',
            filter: false,
        },
        extAssetId: {
            title: 'External Asset Id',
            filter: false,
            valuePrepareFunction: (cell, row) => {
                return row.hasOwnProperty('extAssetId') ? row.extAssetId : "NULL";
            },
        },
        assetValue: {
            title: 'Asset Value',
            filter: false,
            type: 'html',
            valuePrepareFunction: (cell, row) => {
                return `<a href="${row.assetValue}" style="color:#40dc7e" target="_blank">Click here</a>`;
            },
        },
        setIdName: {
            title: 'Set Id Name',
            filter: false,
        },
        size: {
            title: 'Size',
            filter: false,
        },
        keyValue: {
            title: 'Key Value',
            filter: false,
        }
    };

    constructor(public autoAssetService: AutoAssetService,
                public imageAssetService: ImageAssetService,
                public modalService: NgbModal,
                private toastingService: ToastingService,
                private configConstantService: AutoAssetConstantsService,
                private conversionMapService: ConversionMapService,
                private utilService: UtilService) {

        const entityNameOptions = Object.keys(this.entityNameMap);

        this.dimensionMap.set("EntityName", {
            options: entityNameOptions,
            selectedValue: entityNameOptions[0],
            showLoader: false,
        });
        this.dimensionMap.set("EntityValue", {
            options: [],
            selectedValue: [],
            showLoader: false,
        });
        this.dimensionMap.set("EntityAssetSize", {
            options: [],
            selectedValue: null,
            showLoader: false,
        });

        this.entityName = this.dimensionMap.get("EntityName");
        this.entityValue = this.dimensionMap.get("EntityValue");
        this.entityAssetSize = this.dimensionMap.get("EntityAssetSize");

        this.settings = {
            actions: false,
            noDataMessage: ''
        }
        this.showMessage = true;
    }

    ngOnInit(): void {
    }

    getSelectedEntityName() {
        return this.entityName.selectedValue;
    }

    getSelectedEntityNameValue() {
        return this.entityNameMap[this.entityName.selectedValue].value;
    }

    getSelectedEntityNamePlaceholderValue() {
        return this.entityNameMap[this.getSelectedEntityName()].placeholder;
    }

    getSelectedEntityNameDropdownValue() {
        return this.entityNameMap[this.getSelectedEntityName()].dropDownValue;
    }

    getSelectedEntityValue() {
        let entityValueList = [];
        if (this.imageAssetService.isSizeSelectVisible(this.getSelectedEntityNameValue())) {
            entityValueList = this.entityValue.selectedValue.map((item) =>
                this.autoAssetService.extractIdUsingRegex(item) + this.configConstantService.DOUBLE_DOLLAR + this.getSelectedEntityAssetSize()
            );
        } else if(this.entityName.selectedValue === 'Keyword Cluster') {
            entityValueList = this.entityValue.selectedValue.map((item) => this.autoAssetService.extractName(item));
        }else{
            entityValueList = this.entityValue.selectedValue.map((item) => this.autoAssetService.extractIdUsingRegex(item));
        }
        return entityValueList;
    }

    getJoinedSelectedEntityValue() {
        return this.getSelectedEntityValue().join(",");
    }

    getSelectedEntityAssetSize() {
        return this.entityAssetSize.selectedValue;
    }

    getTopDomainBasis() {
        if (this.showLoader) {
            return;
        } else if (this.toggledRows.length > 0) {
            this.toastingService.warning("Unsaved Changes", "Please review your pending modifications in the Preview")
        } else if (this.getSelectedEntityNameDropdownValue() !== "Demand basis") {
            this.toastingService.warning("Invalid Entity Name", "Please select valid 'Entity Name'")
        } else {
            this.waitingMessage = "Fetching...";
            this.showLoader = true;
            this.autoAssetService.getTopDemandBasis()
                .then((data) => {
                    data = data.filter(item => this.utilService.isStringSetAndNotNull(item));
                    this.entityValue.selectedValue = data;
                    this.getMappedAsset();
                })
                .catch(() => {
                    this.showLoader = false;
                    this.resetWaitingMessage()
                })
        }
    }

    async getAsset() {
        if (this.showLoader) {
            return;
        } else if (this.getSelectedEntityValue().length <= 0) {
            this.toastingService.warning("Incomplete Input", "Please Enter Demand Basis to Show Mapped Assets")
        } else if (this.toggledRows.length > 0) {
            this.toastingService.warning("Unsaved Changes", "Please review your pending modifications in the Preview")
        } else {
            this.resetFilters();
            this.getMappedAsset();
        }
    }

    async getMappedAsset() {
        this.waitingMessage = "Fetching...";
        this.showLoader = true;
        const selectedEntityName = this.getSelectedEntityNameValue();
        const selectedEntityValueJoined = this.getJoinedSelectedEntityValue();

        await this.autoAssetService.getMappedAssetListOnKey(selectedEntityName, selectedEntityValueJoined,
            this.configConstantService.ASSET_TYPE_IMAGE).then((assetData) => {
                this.assetData = this.addColumns(assetData);
                this.toggledRows = [];
                this.loadNg2Table(this.assetData);
                this.extractFilterOptions(this.assetData);
                this.toastingService.success('Fetch Assets', `Successfully Fetched ${this.assetData.length} Assets`);
            })
            .catch((error) => {
            })
            .finally(() => {
                this.showLoader = false;
                this.resetWaitingMessage();
            })
    }

    addColumns(data) {
        data.map((item) => {
            item['isToggled'] = false;
            if (this.imageAssetService.SetIdToImageSourceMap.hasOwnProperty(item['setId'])) {
                item['setIdName'] = this.imageAssetService.SetIdToImageSourceMap[item['setId']];
            } else {
                item['setIdName'] = item['setId'];
            }
        })
        return data
    }

    async getDropdownOptionSuggestion(event, selectedDimension) {
        const dimension = this.dimensionMap.get(selectedDimension);
        dimension.showLoader = true;
        this.entityValueInput = event.target.value;

        if (selectedDimension == "EntityAssetSize") {
            selectedDimension = "Template Size"
        } else {
            selectedDimension = this.getSelectedEntityNameDropdownValue();
        }

        await this.autoAssetService.getAutoSelectSuggestions(
            selectedDimension,
            this.entityValueInput,
        )
            .then((data) => {
                const dimensionEnumName = this.conversionMapService.frontendToBackendEnumMap[selectedDimension]
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
                dimension.options = Object.values(data).map((item) => {
                    const itemLowerCaseKeys = convertKeysToLowerCase(item);
                    if (itemLowerCaseKeys[dimensionEnumName] !== undefined && itemLowerCaseKeys[dimensionEnumName] !== null) {
                        return itemLowerCaseKeys[dimensionEnumName];
                    }
                    return '';
                });

            })
            .finally(() => {
                dimension.showLoader = false;
            })
    }

    loadNg2Table(data) {
        if (data && data.length > 0) {
            this.settings = {
                columns: this.columns,
                actions: this.actions,
                pager: {
                    perPage: 10,
                    display: true,
                }
            }
            this.loadFilterData();
        }
    }

    loadFilterData() {
        this.filteredData = this.assetData.filter(item => {
            const conditionEntityValue = !this.selectedEntityValue || item['entityValue'] === this.selectedEntityValue;
            const conditionAssetId = !this.selectedAssetId || item['assetId'] === this.selectedAssetId;
            const conditionIsActive = this.selectedIsActive == null || (item['isActive'] && this.selectedIsActive) || (!item['isActive'] && !this.selectedIsActive);
            const conditionSetIdName = !this.selectedSetIdName || item['setIdName'] === this.selectedSetIdName;
            const conditionSize = !this.selectedSize || item['size'] === this.selectedSize;
            return conditionEntityValue && conditionAssetId && conditionSetIdName && conditionSize && conditionIsActive;
        });
        this.extractFilterOptions(this.filteredData);
        this.source.load(this.filteredData);
    }

    extractFilterOptions(data: any[]) {
        const columnsToExtract = ["entityValue", "assetId", "isActive", "setIdName", "size"]
        columnsToExtract.forEach((heading) => {
            let value = [...new Set(data.map(item => item[heading]))];
            value = value.filter(option => this.utilService.isSet(option));
            this.filterMap.set(heading, value);
        })
    }

    getFilterOptions(selectedSelect: String) {
        let options = [];
        if (this.filterMap.has(selectedSelect)) {
            options = this.filterMap.get(selectedSelect);
        }
        return options;
    }

    handleCommaSeparatedQueryValue(event: any) {
        const inputArray = this.entityValueInput.split(",").map((value) => value.trim());
        const uniqueValues = new Set([...this.entityValue.selectedValue, ...inputArray]);
        this.entityValue.selectedValue = Array.from(uniqueValues);
        if (inputArray.length > 1 && !this.entityValue.selectedValue) {
            const index = this.entityValue.selectedValue.lastIndexOf(this.entityValueInput);
            this.entityValue.selectedValue.splice(index, 1);
        }
    }

    toggledRowData(rowData: any) {
        rowData.isToggled = !rowData.isToggled;
        rowData.isActive = !rowData.isActive;
        const index = this.toggledRows.findIndex(item => item.id == rowData.id);
        if (index !== -1) {
            this.toggledRows.splice(index, 1);
        } else {
            this.toggledRows.push(rowData);
        }
    }

    removeAndRefreshRowData(rowData) {
        this.toggledRowData(rowData);
        this.loadFilterData();
    }


    resetDimensionData(selectedDimension) {
        this.dimensionMap.get(selectedDimension).options = [];
        this.dimensionMap.get(selectedDimension).selectedValue = [];
        this.resetEntityAssetSize();
    }

    resetEntityAssetSize() {
        if (!this.imageAssetService.isSizeSelectVisible(this.getSelectedEntityNameValue())) {
            this.entityAssetSize.selectedValue = null;
        }
    }

    resetWaitingMessage() {
        this.waitingMessage = '';
    }

    unMapAll(event) {
        this.filteredData.forEach((item) => {
            if (event.target.checked == true) {
                if (item.isToggled && item.isActive) {
                    item.isToggled = false;
                    item.isActive = false;
                } else if (item.isActive == true) {
                    item.isToggled = true;
                    item.isActive = false;
                }
            } else {
                if (item.isToggled && !item.isActive) {
                    item.isToggled = false;
                    item.isActive = !item.isActive;
                }
            }
            this.showPreviewInfoMessage();
        })
        this.toggledRows = this.assetData.filter((item) => item.isToggled);
        this.source.load(this.filteredData);
    }

    updateAsset() {
        this.waitingMessage = 'Updating...';
        this.showLoader = true;
        this.autoAssetService.blockUnblockAssets(this.toggledRows)
            .then(() => {
                this.resetAfterUpdate();
                this.getMappedAsset();
            })
            .catch(() => {
                this.showLoader = false;
                this.resetWaitingMessage()
            })
    }

    resetAfterUpdate() {
        this.toggledRows = [];
        if (this.source.count() <= 1) {
            this.selectedIsActive = null;
        }
    }

    resetFilters() {
        this.selectedEntityValue = null;
        this.selectedAssetId = null;
        this.selectedSetId = null;
        this.selectedSetIdName = null;
        this.selectedSize = null;
        this.selectedIsActive = null;
    }

    isSizeNeededAndNotNull() {
        return this.imageAssetService.isSizeSelectVisible(this.getSelectedEntityNameValue()) && this.getSelectedEntityAssetSize() == null;
    }

    isFetchAssetButtonDisabled(): boolean {
        return this.getSelectedEntityValue().length <= 0 || this.toggledRows.length > 0 || this.isSizeNeededAndNotNull();
    }

    isFetchTopDemandBasisButtonDisabled(): boolean {
        return this.toggledRows.length > 0 || this.getSelectedEntityNameDropdownValue() !== "Demand basis" || this.isSizeNeededAndNotNull();
    }

    showPreviewInfoMessage() {
        if (this.showMessage) {
            this.toastingService.info("Don't Forget to Save", "Click on 'Preview & Save' to see changes and save.");
        }
        this.showMessage = false;
    }

    previewModal() {
        if (this.toggledRows.length <= 0) {
            this.toastingService.warning("Nothing to Preview", "There's No Change In Assets")
        } else {
            const previewModal = this.modalService.open(PreviewModalComponent, {
                size: "lg",
                scrollable: true,
            });
            previewModal.componentInstance.previewData = this.toggledRows;
            previewModal.componentInstance.previewModalName = this.configConstantService.VIEW_ASSET_PREVIEW_MODAL;
            previewModal.componentInstance.receivedColumns = this.viewAssetPreviewModalColumns;
            previewModal.componentInstance.imageUrlKeyName = "assetValue";
            const childInstance: PreviewModalComponent = previewModal.componentInstance as PreviewModalComponent;
            childInstance.removeRowEvent.subscribe((removedPreviewAssetRow) => {
                this.removeAndRefreshRowData(removedPreviewAssetRow);
                if (this.toggledRows.length <= 0) {
                    previewModal.dismiss();
                }
            });
            childInstance.saveButtonEvent.subscribe(() => {
                this.updateAsset()
            });
        }

    }

}






