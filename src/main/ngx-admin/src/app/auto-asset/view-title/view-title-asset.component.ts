import { Component, OnInit, ViewChild } from '@angular/core';
import { LocalDataSource } from 'ng2-smart-table';
import { AssetBlockUnblockComponent } from '../shared-component/asset-block-unblock/asset-block-unblock.component';
import { AutoAssetService } from '../../@core/data/auto_asset/auto-asset.service';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { ToastingService } from '../../@core/utils/toaster.service';
import { AutoAssetConstantsService } from '../../@core/data/auto-asset-constants.service';
import { ConversionMapService } from '../../@core/data/conversion-map.service';
import { PreviewModalComponent } from '../shared-component/preview-modal/preview-modal.component';
import { UtilService } from "../../@core/utils/util.service";
import { CheckBoxComponent } from "../shared-component/check-box/check-box.component";
import { BulkUploadModalComponent } from '../shared-component/bulk-upload-modal/bulk-upload-modal.component';
import { SidePanelComponent } from '../shared-component/side-panel/side-panel.component';
import { ActivatedRoute } from '@angular/router';
import { BulkUploadService } from '../../@core/data/auto_asset/bulk-upload.service';
import { TitleAssetService } from '../../@core/data/auto_asset/title-asset.service';

@Component({
    selector: 'view-title',
    templateUrl: './view-title-asset.component.html',
    styleUrls: ['./view-title-asset.component.css','../shared-style.css'],
})
export class ViewTitleAssetComponent implements OnInit {
    @ViewChild(SidePanelComponent) sidePanel!: SidePanelComponent;

    dimensionMap: Map<string, any> = new Map();
    entityValueInput: any;

    showLoader = false;
    showSidePanel = false;

    settings = {};
    assetData = [];
    source: LocalDataSource = new LocalDataSource();
    columns = {
        blockUnblock: {
            title: 'Map/Unmap',
            type: 'custom',
            filter: false,
            renderComponent: AssetBlockUnblockComponent,
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
        assetValue: {
            title: 'Asset Value',
            filter: false,
        },
        assetId: {
            title: 'Asset Id',
            filter: false,
        },
        basis: {
            title: 'Basis',
            filter: false,
        },
        setId: {
            title: 'Set Id',
            filter: false,
        },
        isActive: {
            title: 'Is Active',
            filter: false,
        },
    };
    actions: {
        add: false,
        edit: false,
        delete: false,
    };

    selectedEntityValue: any = null;
    selectedAssetId: any = null;
    selectedIsActive: string = null;
    selectedSetId: string = null;
    selectedSize: string = null;
    filterMap = new Map<String, String[]>();

    toggledRows: any[] = [];
    filteredData: any[] = [];
    showMessage: boolean;

    entityNameMap = this.titleAssetService.EntityNameMapForTitle;
    entityName: any;
    entityValue: any;
    publisherUrl: any;
    publisherPageTitle: any;
    demandBasisValue: any;
    manualTitleMappings: any;
    demandKwd: any;
    multiple: boolean = true;

    viewAssetPreviewModalColumns = {
        entityName: {
            title: 'Entity Name',
            filter: false,
        },
        entityValue: {
            title: 'Entity Value',
            filter: false,
        },
        assetValue: {
            title: 'Asset Value',
            filter: false,
        },
        assetId: {
            title: 'Asset Id',
            filter: false,
        },
        basis: {
            title: 'Basis',
            filter: false,
        },
        setId: {
            title: 'Set Id',
            filter: false,
        },
        size: {
            title: 'Size',
            filter: false,
        },
    };
    existingMappedAssetListOnEntityValue = [];
    mapTitlePreviewModalColumns = {
        entity_name: {
            title: 'Entity Name',
            filter: false,
        },
        entity_value: {
            title: 'Entity Value',
            filter: false,
        },
        key_value: {
            title: 'Key Value',
            filter: false,
        },
        path: {
            title: "Asset Value",
            filter: false,
        },
        keyword: {
            title: "Basis",
            filter: false,
        },
        set_id: {
            title: "Set Id",
            filter: false,
        },
        is_active: {
            title: "Is Active",
            filter: false,
        },
    };
    previewAssetData = [];
    promptForOzil: string = this.configConstantService.OZIL_GENERATED_TITLE_DEFAULT_PROMPT;
    generatedTitlesFromOzil: Array<string>;
    loaderForButtons: any = {
        generateTitleFromOzilButton: false,
        queueLoader: false,
        bulkUploadLoader: false,
        previewAndMapButton: false,
        viewLoader: false,
    };
    loader = {
        mapTitleLoader: false,
    }
    columnHeadingsForGeneratedOrManualMappedTitles: any = {
        asset_value: {
            title: 'Ad Title',
            filter: false,
        },
        demand_keyword: {
            title: 'Demand Keyword',
            filter: false,
        },
        publisher_page_title: {
            title: 'Publisher Page Title',
            filter: false,
        },
        publisher_page_url: {
            title: 'Publisher Page Url',
            filter: false,
        },
        checkBox: {
            title: 'Map Title',
            type: 'custom',
            filter: false,
            renderComponent: CheckBoxComponent,
            onComponentInitFunction: (instance) => {
                instance.clickEvent.subscribe((rowData) => {
                });
            },
        },
    };
    settingForGeneratedOrManualMappedTitles: any = {
        columns: this.columnHeadingsForGeneratedOrManualMappedTitles,
        actions: false
    };
    dataForGeneratedOrManualMappedTitles: Array<any> = [];
    isCollapsed: boolean = false;
    waitingMessage: string = '';

    constructor(public autoAssetService: AutoAssetService,
        public titleAssetService: TitleAssetService,
        public modalService: NgbModal,
        private bulkUploadService: BulkUploadService,
        private toastingService: ToastingService,
        private configConstantService: AutoAssetConstantsService,
        private route: ActivatedRoute,
        private conversionMapService: ConversionMapService,
        private utilService: UtilService) {

        const entityNameOptions = Object.keys(this.entityNameMap);

        this.dimensionMap.set('EntityName', {
            options: entityNameOptions,
            selectedValue: entityNameOptions[0],
            showLoader: false,
        });
        this.dimensionMap.set('EntityValue', {
            options: [],
            selectedValue: [],
            showLoader: false,
        });
        this.dimensionMap.set('Demand basis', {
            options: [],
            selectedValue: null,
            showLoader: false,
        });
        this.dimensionMap.set('Demand Kwd', {
            options: [],
            selectedValue: [],
            showLoader: false,
        });
        this.dimensionMap.set('PublisherURL', {
            options: [],
            selectedValue: null,
            showLoader: false,
        });
        this.dimensionMap.set('PublisherPageTitle', {
            options: [],
            selectedValue: null,
            showLoader: false,
        });
        this.dimensionMap.set('Manual Title Mappings', {
            options: [],
            selectedValue: null,
            showLoader: false,
        });

        this.entityName = this.dimensionMap.get('EntityName');
        this.entityValue = this.dimensionMap.get('EntityValue');
        this.demandBasisValue = this.dimensionMap.get('Demand basis');
        this.demandKwd = this.dimensionMap.get('Demand Kwd');
        this.publisherUrl = this.dimensionMap.get('PublisherURL');
        this.publisherPageTitle = this.dimensionMap.get('PublisherPageTitle');
        this.manualTitleMappings = this.dimensionMap.get('Manual Title Mappings');

        this.settings = {
            actions: false,
            noDataMessage: '',
        };
        this.showMessage = true;
        this.handleQueryParameters();
    }

    ngOnInit(): void {
    }

    handleQueryParameters() {
        this.route.queryParams.subscribe(params => {
            if (this.hasRequestIdParams(params)) {
                this.handleRequestIdParams(params);
            }

        });
    }

    hasRequestIdParams(params): boolean {
        return params.hasOwnProperty('request_id')
    }

    handleRequestIdParams(params): void {
        const requestId = decodeURIComponent(params['request_id']);
        this.viewAsset(requestId);
    }

    toggleSidePanel() {
        this.showSidePanel = !this.showSidePanel;
    }

    closeSidePanel() {
        this.showSidePanel = false;
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
        if (this.titleAssetService.isMultipleEntityValueInputBox(this.getSelectedEntityNameValue())) {
            entityValueList = this.entityValue.selectedValue.map((item) =>
                this.autoAssetService.extractIdUsingRegex(item) + this.configConstantService.DOUBLE_DOLLAR +
                this.getDemandBasisValue(),
            );
        } else {
            entityValueList = this.entityValue.selectedValue.map((item) =>
                this.autoAssetService.extractIdUsingRegex(item),
            );
        }
        return entityValueList;
    }

    getJoinedSelectedEntityValue() {
        return this.getSelectedEntityValue().join(',');
    }

    getDemandBasisValue() {
        return this.demandBasisValue.selectedValue;
    }

    viewAsset(requestId) {
        this.closeSidePanel();
        this.isCollapsed = false;
        this.resetFetchedData();
        this.loader.mapTitleLoader = true;
        this.waitingMessage = "Fetching Generated Assets For Request Id: " + requestId;
        this.autoAssetService.getGeneratedAssetListByRequestId(requestId)
            .then((response: any) => {
                this.dataForGeneratedOrManualMappedTitles = response;
                this.settingForGeneratedOrManualMappedTitles = {
                    columns: this.columnHeadingsForGeneratedOrManualMappedTitles,
                    actions: false
                };

            })
            .finally(() => {
                this.loader.mapTitleLoader = false;
                this.resetWaitingMsg();
            });
    }

    async fetchTitleAsset() {
        if (this.isEntityValueNotSet()) {
            this.toastingService.warning('Incomplete Input',
                'Please Enter Selected Entity Value(s)');
        } else if (this.toggledRows.length > 0) {
            this.toastingService.warning('Unsaved Changes', 'Please review your pending modifications in the Preview');
        } else {
            this.manualTitleMappings.selectedValue = null;
            this.isCollapsed = true;
            this.dataForGeneratedOrManualMappedTitles = [];
            this.showLoader = true;
            const selectedEntityName = this.getSelectedEntityNameValue();
            const selectedEntityValueJoined = this.getJoinedSelectedEntityValue();

            await this.autoAssetService.getMappedAssetListOnKey(selectedEntityName, selectedEntityValueJoined,
                this.configConstantService.ASSET_TYPE_TITLE)
                .then((response) => {
                    this.assetData = response;
                    this.updateNg2Table();
                })
                .catch((error) => {
                    this.showLoader = false;
                });
        }
    }

    updateNg2Table(): void {
        this.assetData.map((item) => {
            item['isToggled'] = false;
        });
        this.toggledRows = [];
        this.loadNg2Table(this.assetData);
        this.extractFilterOptions(this.assetData);
        this.showLoader = false;
        this.toastingService.success('Fetch Assets', `Successfully Fetched ${this.assetData.length} Assets`);
    }

    async getDropdownOptionSuggestion(event, selectedDimension) {
        const dimension = this.dimensionMap.get(selectedDimension);
        dimension.showLoader = true;
        this.entityValueInput = event.target.value;
        dimension.options = [];
        if (this.entityValueInput?.length > 0) {
            dimension.options = [this.entityValueInput];
        }
        if (selectedDimension === 'Demand basis' || selectedDimension === 'Manual Title Mappings' ||
            selectedDimension === 'Demand Kwd') {
            selectedDimension = 'Demand basis';
        } else {
            selectedDimension = this.getSelectedEntityNameDropdownValue();
        }

        await this.autoAssetService.getAutoSelectSuggestions(
            selectedDimension,
            this.entityValueInput,
        )
            .then((data) => {
                const dimensionEnumName = this.conversionMapService
                    .frontendToBackendEnumMap[selectedDimension].toLowerCase();

                const convertKeysToLowerCase = (obj) => {
                    const converted = {};
                    for (const key in obj) {
                        if (Object.prototype.hasOwnProperty.call(obj, key)) {
                            converted[key.toLowerCase()] = obj[key];
                        }
                    }
                    return converted;
                };
                dimension.options = Array.from(new Set([...dimension.options, ...Object.values(data).map((item) => {
                    const itemLowerCaseKeys = convertKeysToLowerCase(item);
                    if (itemLowerCaseKeys[dimensionEnumName] !== undefined &&
                        itemLowerCaseKeys[dimensionEnumName] !== null) {
                        return itemLowerCaseKeys[dimensionEnumName];
                    }
                    return '';
                })]));

            })
            .finally(() => {
                dimension.showLoader = false;
            });
    }

    loadNg2Table(data) {
        if (data && data.length > 0) {
            this.settings = {
                columns: this.columns,
                actions: this.actions,
                pager: {
                    perPage: 10,
                    display: true,
                },
            };
            this.loadFilterData();
        }
    }

    loadFilterData() {
        this.filteredData = this.assetData.filter(item => {
            const conditionEntityValue = !this.selectedEntityValue || item['entityValue'] === this.selectedEntityValue;
            const conditionAssetId = !this.selectedAssetId || item['assetId'] === this.selectedAssetId;
            const conditionIsActive = this.selectedIsActive == null || (item['isActive'] && this.selectedIsActive) || (!item['isActive'] && !this.selectedIsActive);
            const conditionSetId = !this.selectedSetId || item['setId'] === this.selectedSetId;
            const conditionSize = !this.selectedSize || item['size'] === this.selectedSize;
            return conditionEntityValue && conditionAssetId && conditionSetId && conditionSize && conditionIsActive;
        });
        this.extractFilterOptions(this.filteredData);
        this.source.load(this.filteredData);
    }

    extractFilterOptions(data: any[]) {
        const columnsToExtract = ['entityValue', 'assetId', 'isActive', 'setId', 'size'];
        columnsToExtract.forEach((heading) => {
            const value = [...new Set(data.map(item => item[heading]))];
            this.filterMap.set(heading, value);
        });
    }

    handleCommaSeparatedQueryValue(event: any) {
        const inputArray = this.entityValueInput.split(',').map((value) => value.trim());
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
        const index = this.toggledRows.findIndex(item => item.id === rowData.id);
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
        this.toggledRows = [];
        this.dimensionMap.get(selectedDimension).options = [];
        this.dimensionMap.get(selectedDimension).selectedValue = [];
        this.resetDemandBasisValue();
    }

    resetDemandBasisValue() {
        if (!this.titleAssetService.isMultipleEntityValueInputBox(this.getSelectedEntityNameValue())) {
            this.demandBasisValue.selectedValue = null;
        }
    }

    resetWaitingMsg() {
        this.waitingMessage = '';
    }

    unMapAll(event) {
        this.filteredData.forEach((item) => {
            if (event.target.checked === true) {
                if (item.isToggled && item.isActive) {
                    item.isToggled = false;
                    item.isActive = false;
                } else if (item.isActive === true) {
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
        });
        this.toggledRows = this.assetData.filter((item) => item.isToggled);
        this.source.load(this.filteredData);
    }

    updateAsset() {
        this.showLoader = true;
        this.autoAssetService.blockUnblockAssets(this.toggledRows)
            .then(() => {
                this.resetAfterUpdate();
                this.resetFilters();
                this.fetchTitleAsset();
            })
            .catch(() => {
                this.showLoader = false;
            });

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
        this.selectedSize = null;
        this.selectedIsActive = null;
    }

    isEntityValueNotSet(): boolean {
        const entityValuesArray = this.getSelectedEntityValue();
        return (entityValuesArray.some(value => value.endsWith('$$') || value.endsWith('null')) ||
            this.getSelectedEntityValue().length <= 0 || this.toggledRows.length > 0);
    }

    showPreviewInfoMessage() {
        if (this.showMessage) {
            this.toastingService.info('Don\'t Forget to Save', 'Click on \'Preview & Save\' to see changes and save.');
        }
        this.showMessage = false;
    }

    previewModal() {
        if (this.toggledRows.length <= 0) {
            this.toastingService.warning('Nothing to Preview', 'There\'s No Change In Assets');
        } else {
            const previewModal = this.openModal();
            previewModal.componentInstance.previewData = this.toggledRows;
            previewModal.componentInstance.previewModalName = this.configConstantService.KBB_MAP_TITLE_ASSET_PREVIEW_MODAL;
            previewModal.componentInstance.receivedColumns = this.viewAssetPreviewModalColumns;
            previewModal.componentInstance.imageUrlKeyName = 'assetValue';
            const childInstance: PreviewModalComponent = previewModal.componentInstance as PreviewModalComponent;
            childInstance.removeRowEvent.subscribe((removedPreviewAssetRow) => {
                this.removeAndRefreshRowData(removedPreviewAssetRow);
                if (this.toggledRows.length <= 0) {
                    previewModal.dismiss();
                }
            });
            childInstance.saveButtonEvent.subscribe(() => {
                this.updateAsset();
            });
        }
    }

    convertToPreviewData(data) {
        const list = [];
        let tempId = 1;

        data.forEach(obj => {
            if (this.utilService.isSet(obj['isSelected']) && obj['isSelected'] === true) {
                const entityValues = this.getSelectedEntityValue();

                entityValues.forEach(entityValue => {
                    const newObj = {};
                    newObj['temp_id'] = tempId;
                    newObj['entity_name'] = this.getSelectedEntityNameValue();
                    newObj['entity_value'] = entityValue;
                    newObj['key_value'] = (this.configConstantService.ASSET_TYPE_TITLE + this.configConstantService.KEY_VALUE_SEPARATOR +
                        this.getSelectedEntityNameValue() + this.configConstantService.KEY_VALUE_SEPARATOR + entityValue).toLowerCase();
                    if (!this.utilService.isSet(obj['demand_kwd']))
                        newObj['set_id'] = this.configConstantService.MANUAL_TITLE_SET_ID;
                    else
                        newObj['set_id'] = this.configConstantService.AUTO_GENERATED_TITLE_SET_ID;
                    newObj['size'] = null;
                    newObj['keyword'] = obj['demand_keyword'];
                    newObj['path'] = obj['asset_value'];
                    newObj['is_active'] = 1;
                    list.push(newObj);
                    tempId++;
                });
            }
        });


        return list;
    }

    async previewAndMapTitles() {
        if (!this.utilService.isSet(this.getSelectedEntityValue()))
            this.toastingService.warning('Entity value not selected', 'Please select Entity value to proceed');
        else if (!this.utilService.isSet(this.dataForGeneratedOrManualMappedTitles)) {
            this.toastingService.warning('Nothing to preview',
                'Please select rows from table to preview');
        } else if (this.toggledRows.length > 0) {
            this.toastingService.warning('Unsaved Changes', 'Please review your pending modifications in the Preview');
        } else {
            this.loaderForButtons['previewAndMapButton'] = true;
            this.existingMappedAssetListOnEntityValue = await this.getMappedAssetListOnKey();
            this.openPreviewAndMapModal();
        }
    }

    openPreviewAndMapModal() {
        this.previewAssetData = this.convertToPreviewData(this.dataForGeneratedOrManualMappedTitles);
        if (!this.utilService.isSet(this.previewAssetData)) {
            this.toastingService.warning('No rows selected', 'Please select rows to Map');
            this.loaderForButtons['previewAndMapButton'] = false;
            return;
        }
        const previewModal = this.openModal();
        previewModal.componentInstance.previewModalName = this.configConstantService.KBB_MAP_TITLE_ASSET_PREVIEW_MODAL;
        previewModal.componentInstance.receivedColumns = this.mapTitlePreviewModalColumns;
        previewModal.componentInstance.previewData = this.previewAssetData;
        previewModal.componentInstance.imageUrlKeyName = 'path';
        previewModal.componentInstance.existingMappedAssetList = this.existingMappedAssetListOnEntityValue;
        previewModal.componentInstance.activeMappedAssetList = this.getActiveMappedList();
        previewModal.componentInstance.entityName = this.getSelectedEntityName();
        previewModal.componentInstance.entityValue = this.getSelectedEntityValue();
        const childInstance: PreviewModalComponent = previewModal.componentInstance as PreviewModalComponent;
        this.loaderForButtons['previewAndMapButton'] = false;
        childInstance.removeRowEvent.subscribe((row) => {
            this.removePreviewRowData(row);
            previewModal.componentInstance.previewData = this.previewAssetData;
            if (this.previewAssetData.length <= 0) {
                previewModal.dismiss('close');
            }
        });
        childInstance.saveButtonEvent.subscribe(() => {
            this.dataForGeneratedOrManualMappedTitles = [];
            this.saveMapKBBTitles();
        });
    }

    getActiveMappedList() {
        return this.existingMappedAssetListOnEntityValue.filter((item) => item.isActive);
    }

    removePreviewRowData(rowData) {
        this.previewAssetData = this.previewAssetData.filter((item) => item['temp_id'] != rowData['temp_id']);
    }

    async getMappedAssetListOnKey() {
        const entityName = this.getSelectedEntityNameValue();
        const entityValue = this.getSelectedEntityValue();
        return await this.autoAssetService.getMappedAssetListOnKey(entityName, entityValue,
            this.configConstantService.ASSET_TYPE_TITLE)
            .then((response) => {
                return response;
            })
            .catch(() => {
                console.error('Get Mapped Asset List On Key Failed');
            });
    }


    async saveMapKBBTitles() {
        this.showLoader = true;
        const mappedRow = this.previewAssetData;
        await this.autoAssetService.mapAsset(mappedRow, this.configConstantService.ASSET_TYPE_TITLE)
            .then(() => {
                this.fetchTitleAsset();
                this.previewAssetData = [];
                this.showLoader = false;
            })
            .catch(() => {
                this.showLoader = false;
            });
    }

    isDemandKwdSet(): boolean {
        return this.utilService.isSet(this.demandKwd.selectedValue);
    }

    async generateTitleAssetFromOzil() {
        this.dataForGeneratedOrManualMappedTitles = [];
        const demand_kwd = this.demandKwd.selectedValue;
        const publisherPageTitle = this.publisherPageTitle.selectedValue ? this.publisherPageTitle.selectedValue['label'] : '';
        const url = this.publisherUrl.selectedValue ? this.publisherUrl.selectedValue['label'] : '';
        if (!this.isDemandKwdSet()) {
            this.toastingService.warning('Demand Keyword empty', 'Please enter Demand Keyword');
        } else if (this.toggledRows.length > 0) {
            this.toastingService.warning('Unsaved Changes', 'Please review your pending modifications in the Preview');
        } else if (this.promptForOzil.length > 0 && !this.promptForOzil.includes('{{DEMAND_KEYWORD}}')) {
            this.toastingService.warning('{{DEMAND_KEYWORD}} missing in prompt', 'Please add {{DEMAND_KEYWORD}} in your prompt');
        } else if (this.demandKwd.selectedValue.length > 1) {
            return;
        }
        else {
            this.loaderForButtons['generateTitleFromOzilButton'] = true;
            const payloadForTitleAssetFromOzil = {
                demand_kwd: demand_kwd,
                publisher_page_url: url,
                publisher_page_title: publisherPageTitle,
                prompt: this.promptForOzil
            };

            const response = await this.autoAssetService.generateAssetsFromOzil([payloadForTitleAssetFromOzil],
                this.configConstantService.ASSET_TYPE_TITLE)
                .finally(() => {
                    this.loaderForButtons['generateTitleFromOzilButton'] = false;
                })
            this.generatedTitlesFromOzil = response['ad_titles'];
            this.openTableForMappingGeneratedTitles(demand_kwd, publisherPageTitle, url);
        }
    }


    getQueueTitleGenerationPayload() {
        const payload = [{
            dmd_kwd: this.demandKwd.selectedValue.join(','),
            purl: this.publisherUrl.selectedValue?.label,
            ptitle: this.publisherPageTitle.selectedValue?.label,
            pmt: this.promptForOzil
        }];
        return payload;
    }

    async queueTitleGeneration() {
        if (!this.isDemandKwdSet()) {
            this.toastingService.warning('Demand Keyword empty', 'Please enter Demand Keyword');
        } else if (this.toggledRows.length > 0) {
            this.toastingService.warning('Unsaved Changes', 'Please review your pending modifications in the Preview');
        } else if (this.promptForOzil.length > 0 && !this.promptForOzil.includes('{{DEMAND_KEYWORD}}')) {
            this.toastingService.warning('{{DEMAND_KEYWORD}} missing in prompt', 'Please add {{DEMAND_KEYWORD}} in your prompt');
        } else {
            this.resetTitlesData();
            this.loaderForButtons['queueLoader'] = true;
            const payload = this.getQueueTitleGenerationPayload();
            const assetType = this.configConstantService.ASSET_TYPE_TITLE;
            await this.autoAssetService.queueAssetGeneration(payload, assetType)
                .then((response) => {
                    const requestId = response['request_id'];
                    this.toastingService.success("Request Processing", `Your request id ${requestId}`);
                })
                .catch((error) => {
                    this.toastingService.error('Request Failed', "Unable to process request");
                })
                .finally(() => {
                    this.loaderForButtons['queueLoader'] = false;
                });
        }
    }

    async bulkTitleGeneration(payload) {
        payload = this.autoAssetService.getUpdatedKeyForBulkUploadData(payload, this.bulkUploadService.titleAssetKeySetForBulkUpload)
        this.loaderForButtons['bulkUploadLoader'] = true;
        const assetType = this.configConstantService.ASSET_TYPE_TITLE;
        const response = await this.autoAssetService.queueAssetGeneration(payload, assetType)
            .then((response) => {
                const requestId = response['request_id'];
                this.toastingService.success("Request Processing", `Your request id ${requestId}`);
            })
            .catch((error) => {
                this.toastingService.error('Request Failed', "Unable to process request");
            })
            .finally(() => {
                this.loaderForButtons['bulkUploadLoader'] = false;
            });
    }

    async bulkTitleMapping(payload) {
        this.loader['mapTitleLoader'] = true;
        this.waitingMessage = 'Mapping...';
        const assetType = this.configConstantService.ASSET_TYPE_TITLE;
        const response = await this.autoAssetService.mapAsset(payload, assetType)
            .then((response) => {
            })
            .catch((error) => {
                this.toastingService.error('Mapping Failed', "Unable to map title");
            })
            .finally(() => {
                this.loader['mapTitleLoader'] = false;
                this.resetWaitingMsg();
            });
    }

    generateTitleButtonDisabled() {
        return !this.isDemandKwdSet() || this.demandKwd.selectedValue.length > 1 || this.toggledRows.length > 0 || (this.promptForOzil.length > 0 && !this.promptForOzil.includes('{{DEMAND_KEYWORD}}'));
    }

    queueTitleGenerationButtonDisabled() {
        return !this.isDemandKwdSet();
    }

    openModal(): NgbModalRef {
        return this.modalService.open(PreviewModalComponent, {
            size: 'lg',
            scrollable: true,
        });
    }

    openTableForMappingGeneratedTitles(demandKwd: string[], publisherPageTitle: string, url: string) {
        this.dataForGeneratedOrManualMappedTitles = [
            ...this.dataForGeneratedOrManualMappedTitles,
            ...this.generatedTitlesFromOzil.map(title => ({
                asset_value: title,
                demand_keyword: demandKwd,
                publisher_page_title: publisherPageTitle,
                publisher_page_url: url,
            }))
        ];

        this.settingForGeneratedOrManualMappedTitles = {
            columns: this.columnHeadingsForGeneratedOrManualMappedTitles,
            actions: false
        };
        this.loaderForButtons['generateTitleFromOzilButton'] = false;
        this.generatedTitlesFromOzil = [];
    }

    clearTableData() {
        this.assetData = [];
    }

    resetTitlesData() {
        this.dataForGeneratedOrManualMappedTitles = [];
    }

    resetFetchedData() {
        this.dataForGeneratedOrManualMappedTitles = [];
    }


    toggleCollapse() {
        this.isCollapsed = !this.isCollapsed;
    }

    addManualTitle(event) {
        if (this.utilService.isSet(this.manualTitleMappings.selectedValue)) {
            this.dataForGeneratedOrManualMappedTitles.push(
                { asset_value: event, isSelected: true }
            );
            this.settingForGeneratedOrManualMappedTitles = {
                columns: this.columnHeadingsForGeneratedOrManualMappedTitles,
                actions: false,
            };
        }
    }

    removeManualTitle(event) {
        const indexToRemove = this.dataForGeneratedOrManualMappedTitles.findIndex(item => item.ad_titles ===
            event.value);
        if (indexToRemove !== -1) {
            this.dataForGeneratedOrManualMappedTitles.splice(indexToRemove, 1);
            this.settingForGeneratedOrManualMappedTitles = {
                columns: this.columnHeadingsForGeneratedOrManualMappedTitles,
                actions: false,
            };
        }
    }

    openBulkUploadModal() {
        const bulkModal = this.modalService.open(BulkUploadModalComponent, {
            size: "md",
            scrollable: true,
        });

        bulkModal.componentInstance.parentComponentName = this.configConstantService.TITLE_GENERATION;
        bulkModal.componentInstance.modalHeader = "Bulk Title Generation";
        bulkModal.componentInstance.modalSaveButtonName = "Queue";
        bulkModal.componentInstance.sampleCsvData = this.bulkUploadService.titleGenerationSampleCsvData;
        bulkModal.componentInstance.sampleCsvColumns = this.bulkUploadService.titleGenerationBulkUploadColumns;
        bulkModal.componentInstance.csvColumns = this.bulkUploadService.titleGenerationBulkUploadColumns;
        const childInstance: BulkUploadModalComponent = bulkModal.componentInstance as BulkUploadModalComponent;
        childInstance.generateEvent.subscribe((bulkData) => {
            this.bulkTitleGeneration(bulkData);
        });
    }

    openBulkTitleMappingModal() {
        const bulkModal = this.modalService.open(BulkUploadModalComponent, {
            size: "md",
            scrollable: true,
        });

        bulkModal.componentInstance.parentComponentName = this.configConstantService.TITLE_GENERATION;
        bulkModal.componentInstance.modalHeader = "Bulk Title Mapping";
        bulkModal.componentInstance.modalSaveButtonName = "Map";
        bulkModal.componentInstance.sampleCsvData = this.bulkUploadService.titleMappingSampleCsvData;
        bulkModal.componentInstance.sampleCsvColumns = this.bulkUploadService.titleMappingBulkUploadColumns;
        bulkModal.componentInstance.csvColumns = this.bulkUploadService.titleMappingBulkUploadColumns;
        const childInstance: BulkUploadModalComponent = bulkModal.componentInstance as BulkUploadModalComponent;
        childInstance.generateEvent.subscribe((bulkData) => {
            this.bulkTitleMapping(bulkData);
        });
    }


    extractParticularColumnsDataFromJsonArray(data: any[], columnsToExtract: string[]): any[] {
        return data.map(obj =>
            columnsToExtract.reduce((acc, key) => {
                if (obj.hasOwnProperty(key)) {
                    acc[this.bulkUploadService.sdHeaderMapping[key]] = obj[key];
                }
                return acc;
            }, {})
        );
    }

}
