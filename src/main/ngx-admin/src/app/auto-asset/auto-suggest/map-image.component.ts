import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { EMPTY, Subject, Subscription, timer } from 'rxjs';
import { catchError, switchMap, takeUntil, takeWhile } from 'rxjs/operators';
import { LocalDataSource } from "ng2-smart-table";
import { AutoAssetService } from "../../@core/data/auto_asset/auto-asset.service";
import { AssetImageComponent } from "../shared-component/asset-image/asset-image.component";
import { CheckBoxComponent } from "../shared-component/check-box/check-box.component";
import { NgbModal, NgbProgressbarConfig } from "@ng-bootstrap/ng-bootstrap";
import { ToastingService } from '../../@core/utils/toaster.service';
import { ImageUploadModalComponent } from '../shared-component/image-upload-modal/image-upload-modal.component';
import { ConversionMapService } from '../../@core/data/conversion-map.service';
import { AutoAssetConstantsService } from '../../@core/data/auto-asset-constants.service';
import { BulkUploadModalComponent } from '../shared-component/bulk-upload-modal/bulk-upload-modal.component';
import { MatPaginator } from '@angular/material/paginator';
import { ngxCsv } from 'ngx-csv';
import { PreviewModalComponent } from '../shared-component/preview-modal/preview-modal.component';
import { ActivatedRoute, Router } from '@angular/router';
import { Clipboard } from '@angular/cdk/clipboard';
import { HttpParams } from "@angular/common/http";
import { ImageSourceEnum } from "../../@core/data/image-source.enum";
import { UtilService } from "../../@core/utils/util.service";
import { BulkUploadService } from "../../@core/data/auto_asset/bulk-upload.service";
import { ImageAssetService } from "../../@core/data/auto_asset/image-asset.service";
import { DynamicWidthCellComponent } from '../shared-component/dynamic-width-cell/dynamic-width-cell.component';

@Component({
    selector: 'auto-suggest',
    templateUrl: './map-image.component.html',
    styleUrls: ['./map-image.component.css', '../shared-style.css'],
})
export class MapImageComponent implements OnInit, OnDestroy {
    @ViewChild(MatPaginator) paginator: MatPaginator;

    private destroy$ = new Subject<void>();
    private aaRequestIntervalTimer: Subscription | null = null;

    DEMAND_BASIS = 'Demand Basis';
    KBB = this.configConstantService.KBB;
    STABLE_DIFFUSION = this.configConstantService.STABLE_DIFFUSION;
    AA_GET_REQUEST_DETAILS_INTERVAL = 10000;

    totalPaginationItems = 0;
    pageSize = 10;
    currentPage = 0;
    sidebarSpinnerLoader = false;
    requestOffset = 0;

    csvDataToDownload: any;
    downloadLoader = false;

    filterInput: any = '';
    showLoader = false;
    showPreviewButtonLoader = false;
    isMappedShowLoader = false;
    existingMappedAssetListOnEntityValue = [];

    selectedRow = [];
    showMessage: boolean;

    dimensionMap = new Map();
    entityNameMapForImage: any;

    assetSource: any;
    keyword: any;
    keywordAssetSize: any;
    entityName: any;
    entityValue: any;
    entityAssetSize: any;
    imageSource: any;
    sdKeyword: any;

    promptText: any;
    stableDiffusionMap: any;
    defaultPrompt: any;
    waitingMessage = '';

    stableDiffusionFilterKeys = ['keyword', 'prompt', 'negativePrompt', 'version'];
    enteredKeyword: any = null;
    enteredPrompt: any = null;
    enteredNegativePrompt: any = null;
    selectedVersion: any = null;

    visibleSidebar = false;
    requestDetailsList = [];
    initialLoader = 1;

    assetData = [];
    previewAssetData = [];

    requestTaskInfo = null;

    settings = {};
    source: LocalDataSource = new LocalDataSource();
    actions: {
        add: false;
        edit: false;
        delete: false;
    };

    KbbColumns = {
        image: {
            title: "Image",
            type: "custom",
            filter: false,
            renderComponent: AssetImageComponent,
            valuePrepareFunction: (cell, row) => {
                return row.path;
            },
        },
        keyword: {
            title: 'Keyword value',
            filter: false,
        },
        path: {
            title: 'Asset Value',
            filter: false,
            type: 'html',
            valuePrepareFunction: (cell, row) => {
                return `<a href="${row.path}" target="_blank">Click here</a>`;
            },
        },
        croppedImageId: {
            title: 'External Asset ID',
            filter: false,
        },
        originalImageId: {
            title: 'Original Image ID',
            filter: false,
        },
        checkBox: {
            title: 'Map Image',
            type: 'custom',
            filter: false,
            renderComponent: CheckBoxComponent,
            onComponentInitFunction: (instance) => {
                instance.clickEvent.subscribe((rowData) => {
                    this.handleSelectedRowData(rowData);
                    this.showPreviewInfoMessage();
                });
            },
        },
    };

    stableDiffusionColumns = {
        image: {
            title: 'Image',
            type: 'custom',
            filter: false,
            renderComponent: AssetImageComponent,
            valuePrepareFunction: (cell, row) => {
                return row.imageUrl;
            },
        },
        keyword: {
            title: 'Keyword',
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
        negativePrompt: {
            title: 'Negative Prompt',
            filter: false,
            type: 'custom',
            renderComponent: DynamicWidthCellComponent,
            valuePrepareFunction: (cell, row) => {
                return { width: '400px', value: row.negativePrompt };
            },
        },
        version: {
            title: 'Version',
            filter: false,
        },
        imageUrl: {
            title: 'Path',
            filter: false,
            type: 'html',
            valuePrepareFunction: (cell, row) => {
                return `<a href="${row.imageUrl}" target="_blank">Click here</a>`;
            },
        },
        checkBox: {
            title: 'Map Image',
            type: 'custom',
            filter: false,
            renderComponent: CheckBoxComponent,
            onComponentInitFunction: (instance) => {
                instance.clickEvent.subscribe((rowData) => {
                    this.handleSelectedRowData(rowData);
                    this.showPreviewInfoMessage();
                });
            },
        },
    };


    mapKBBImagesPreviewModalColumns = {
        keyword: {
            title: 'Basis',
            filter: false,
        },
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
            title: 'Asset Value',
            filter: false,
            type: 'html',
            valuePrepareFunction: (cell, row) => {
                return `<a href="${row.path}" target="_blank">Click here</a>`;
            },
        },
        set_id: {
            title: 'Set Id',
            filter: false,
        },
        croppedImageId: {
            title: 'External Asset ID',
            filter: false,
        },
        originalImageId: {
            title: 'Original Image ID',
            filter: false,
        },
    };

    mapStableDiffusionImagesPreviewModalColumns = {
        keyword: {
            title: 'Keyword',
            filter: false,
        },
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
        imageUrl: {
            title: 'Asset Value',
            filter: false,
            type: 'html',
            valuePrepareFunction: (cell, row) => {
                return `<a href="${row.imageUrl}" target="_blank">Click here</a>`;
            },
        },
        set_id: {
            title: 'Set Id',
            filter: false,
        },
    };

    filterMap = new Map<String, String[]>();
    filteredData = [];

    emptyList = [];
    requestId = null;
    requestIdInput = null;

    toFetchRequestTypes = ['STABLE_DIFFUSION', 'ASSET_MAPPING'];

    constructor(public autoAssetService: AutoAssetService,
        private imageAssetService: ImageAssetService,
        public modalService: NgbModal,
        private bulkUploadService: BulkUploadService,
        private toastingService: ToastingService,
        private conversionMapService: ConversionMapService,
        private configConstantService: AutoAssetConstantsService,
        config: NgbProgressbarConfig,
        private route: ActivatedRoute,
        private clipboard: Clipboard,
        private router: Router,
        private utilService: UtilService,
    ) {
        config.max = 100;
        this.autoAssetService.getAARequest(10, 0, this.toFetchRequestTypes);
        const imageSourceOptions = Object.values(ImageSourceEnum);
        this.entityNameMapForImage = this.imageAssetService.EntityNameMapForImage;
        const entityNameOptions = Object.keys(this.entityNameMapForImage);


        this.dimensionMap.set('AssetSource', {
            options: ['KBB', 'Stable Diffusion'],
            selectedValue: 'Stable Diffusion',
            showLoader: false,
        });
        this.dimensionMap.set('Keyword', {
            options: [],
            selectedValue: [],
            showLoader: false,
        });
        this.dimensionMap.set('KeywordAssetSize', {
            options: this.configConstantService.KEYWORD_ASSET_SIZE,
            selectedValue: null,
            showLoader: false,
        });
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
        this.dimensionMap.set('EntityAssetSize', {
            options: [],
            selectedValue: null,
            showLoader: false,
        });
        this.dimensionMap.set('ImageSource', {
            options: imageSourceOptions,
            selectedValue: ImageSourceEnum[this.dimensionMap.get('AssetSource').selectedValue],
            showLoader: false,
        });
        this.dimensionMap.set('sdKeyword', {
            options: [],
            selectedValue: [],
            showLoader: false,
        });

        this.assetSource = this.dimensionMap.get('AssetSource');
        this.keyword = this.dimensionMap.get('Keyword');
        this.keywordAssetSize = this.dimensionMap.get('KeywordAssetSize');
        this.entityName = this.dimensionMap.get('EntityName');
        this.entityValue = this.dimensionMap.get('EntityValue');
        this.entityAssetSize = this.dimensionMap.get('EntityAssetSize');
        this.imageSource = this.dimensionMap.get('ImageSource');
        this.sdKeyword = this.dimensionMap.get('sdKeyword');


        this.stableDiffusionMap = {
            versionList: this.configConstantService.SD_VERSION_LIST,
            version: this.configConstantService.SD_DEFAULT_VERSION,
            keywordInput: '',
            keyword: this.sdKeyword.selectedValue,
            negativePrompt: this.configConstantService.SD_NEGATIVE_PROMPT,
            prompt: this.configConstantService.SD_DEFAULT_PROMPT,
            negativePromptSelected: false,
        };

        this.handleQueryParameters();

        this.settings = {
            actions: false,
        };
        this.showMessage = true;
    }

    ngOnInit() {
    }

    ngOnDestroy() {
        this.destroy$.next();
        this.destroy$.complete();
    }

    handleQueryParameters() {
        this.route.queryParams.subscribe(params => {
            if (this.hasRequestIdParams(params)) {
                this.handleRequestIdParams(params);
            } else if (this.hasSourceParams(params)) {
                this.handleSourceParams(params);
            }
        });
    }

    hasSourceParams(params) {
        return params.hasOwnProperty('source');
    }

    hasRequestIdParams(params): boolean {
        return (
            params.hasOwnProperty('request_id') &&
            params.hasOwnProperty('source')
        );
    }

    handleSourceParams(params) {
        const source = decodeURIComponent(params['source']);
        this.assetSource.selectedValue = source;
        this.imageSource.selectedValue = ImageSourceEnum[source];
    }

    handleRequestIdParams(params): void {
        this.assetSource.selectedValue = decodeURIComponent(params['source']);
        this.requestId = decodeURIComponent(params['request_id']);
        this.viewStableDiffusionGeneratedImagesForRequestId(this.requestId);
    }

    getBaseUrl() {
        const currentUrl = window.location.href;
        const segments = currentUrl.split('/#/');
        let url = segments.length > 1 ? segments[0] : currentUrl;
        if (segments.length > 1) {
            url += '/#/' + segments[1].split('?')[0];
        }
        return url;
    }

    generateAndCopySidebarRequestDetailsUrl(requestId) {
        const baseUrl = this.getBaseUrl();
        const params = new HttpParams()
            .set('source', encodeURIComponent(this.STABLE_DIFFUSION))
            .set('request_id', encodeURIComponent(requestId));

        const sharableLink = this.constructUrl(baseUrl, params);
        this.copyToClipboard(sharableLink);
    }

    constructUrl(baseUrl: string, params: HttpParams) {
        const paramsUrl = params.toString();
        const url = baseUrl + (paramsUrl ? '?' + paramsUrl : '');
        return url;
    }

    copyToClipboard(content): void {
        this.clipboard.copy(content);
        this.toastingService.success('URL copied', 'URL successfully copied');
    }

    handleSourceSelection(assetSource) {
        this.resetData();
        this.assetSource.selectedValue = assetSource;
        this.imageSource.selectedValue = ImageSourceEnum[assetSource];
        this.router.navigate([], {
            queryParams: { source: assetSource },
        });

    }

    isSelectedSource(source) {
        if (this.assetSource.selectedValue == source) {
            return true;
        } else if (this.assetSource.selectedValue == source) {
            return true;
        }
        return false;
    }

    restartGetAARequestIntervalTimer() {
        if (this.aaRequestIntervalTimer) {
            this.aaRequestIntervalTimer.unsubscribe();
        }

        this.aaRequestIntervalTimer = timer(this.AA_GET_REQUEST_DETAILS_INTERVAL, this.AA_GET_REQUEST_DETAILS_INTERVAL).pipe(
            switchMap(() => this.autoAssetService.getAARequest(this.pageSize, this.requestOffset, this.toFetchRequestTypes).pipe(
                catchError(error => {
                    console.error('Error occurred:', error);
                    return EMPTY;
                }),
            )),
            takeWhile(() => this.isAARequestProcessing()),
            takeUntil(this.destroy$),
        ).subscribe(
            (response: any) => {
                this.requestDetailsList = response;
                this.handleAARequestTotalCount(response);
            },
            (error: any) => {
                console.error('Error occurred in interval subscription:', error);
            },
        );
    }

    getRequestDetails() {
        this.initialLoader -= 1;
        this.autoAssetService.getAARequest(this.pageSize, this.requestOffset, this.toFetchRequestTypes)
            .subscribe(
                (response) => {
                    this.requestDetailsList = response;
                    this.handleAARequestTotalCount(response);
                },
                (error) => {
                    this.toastingService.error('Fetch Failed', `Failed to fetch request details`);
                    this.sidebarSpinnerLoader = false;
                    this.initialLoader = -1;
                },
                () => {
                    this.sidebarSpinnerLoader = false;
                    this.initialLoader = -1;
                },
            );
    }

    isAARequestProcessing() {
        const hasUnprocessedOrProcessing = this.requestDetailsList.some(obj => obj.isActive == 1);
        return hasUnprocessedOrProcessing;
    }

    viewRequestDetails() {
        this.toggleSideBar();
        this.getRequestDetails();
        this.restartGetAARequestIntervalTimer();
    }

    refreshRequestDetails() {
        this.sidebarSpinnerLoader = true;
        this.getRequestDetails();
    }

    onPageChange(event) {
        this.sidebarSpinnerLoader = true;
        this.currentPage = event.pageIndex;
        this.requestOffset = this.currentPage * this.pageSize;
        this.getRequestDetails();
    }

    handleAARequestTotalCount(response) {
        if (Array.isArray(response) && response.length > 0 && response[0].hasOwnProperty('totalCount')) {
            this.totalPaginationItems = response[0]['totalCount'];
        } else {
            this.totalPaginationItems = 0;
        }
    }

    calculateProgressPercentageValue(request) {
        if (request.isActive != -1) {
            return ((request.rowsProcessed / request.rowsGenerated) * 100).toFixed(2);
        }
        return 0;
    }

    calculateProgressPercentageText(request) {
        if (request.isActive != -1) {
            return ((request.rowsProcessed / request.rowsGenerated) * 100).toFixed(2) + '%';
        }
        return '100%';
    }

    isRequestProcessing(requestDetails) {
        return requestDetails.isActive == 1;
    }

    addLoaders(data) {
        return data.map((item) => {
            item['downloadLoader'] = false;
            item['suspendLoader'] = false;
            return item;
        });
    }

    viewStableDiffusionGeneratedImagesForRequestId(requestId) {
        if (requestId != null && requestId != '') {
            this.routeToAssetSourceAndRequestId(this.STABLE_DIFFUSION, requestId);
            this.resetAssetData();
            this.resetRequestIdInput();
            this.requestId = requestId;
            this.visibleSidebar = false;
            this.showLoader = true;
            this.waitingMessage = `Fetching the generated asset for Request Id ${requestId}`;
            this.imageAssetService.getStableDiffusionImagesFromRequestId(requestId)
                .then((response: any[]) => {
                    this.requestTaskInfo = response['task_status_info'];
                    let imageData = this.filterEmptyImageUrlsList(response['image_list']);
                    this.visibleSidebar = false;
                    this.extractFilterOptions(imageData, this.stableDiffusionFilterKeys);
                    imageData = this.addLoaders(imageData);
                    this.loadDataToTable(imageData, this.stableDiffusionColumns);
                })
                .finally(() => {
                    this.showLoader = false;
                    this.resetWaitingMessage();
                });
        }
    }

    routeToAssetSourceAndRequestId(source, requestId) {
        this.handleSourceSelection(source);
        this.router.navigate([], {
            queryParams: { source: this.assetSource.selectedValue, request_id: requestId },
        });
    }

    filterEmptyImageUrlsList(data) {
        return data.filter(obj => obj.imageUrl && obj.imageUrl.trim() !== '');
    }

    suspendStableDiffusionRequest(request) {
        request.suspendLoader = true;
        this.autoAssetService.suspendAARequest(request.requestId)
            .then((response) => {
                this.getRequestDetails();
                request.suspendLoader = false;
                this.toastingService.success('Request Cancelled', `Successfully cancelled the request with id ${request.requestId}`);
            })
            .catch(() => {
                request.suspendLoader = false;
                this.toastingService.error('Cancel Request Failed', `Unable to cancel request with id ${request.requestId}`);

            });
    }

    toggleSideBar() {
        this.visibleSidebar = !this.visibleSidebar;
        this.resetRequestIdInput();
    }

    getSelectedEntityName() {
        return this.entityName.selectedValue;
    }

    getSelectedEntityNameValue() {
        return this.entityNameMapForImage[this.entityName.selectedValue].value;
    }

    getSelectedEntityNamePlaceholderValue() {
        return this.entityNameMapForImage[this.getSelectedEntityName()].placeholder;
    }

    getSelectedEntityNameDropdownValue() {
        return this.entityNameMapForImage[this.getSelectedEntityName()].dropDownValue;
    }

    getSelectedEntityValue() {
        const entityValueList = this.entityValue.selectedValue || [];
        const entityValues = entityValueList.map((item) => {
            const extractedEntityValue = this.autoAssetService.extractIdUsingRegex(item);
            if (this.imageAssetService.isSizeSelectVisible(this.getSelectedEntityNameValue())) {
                return extractedEntityValue + this.configConstantService.DOUBLE_DOLLAR + this.getSelectedEntityAssetSize();
            }
            return extractedEntityValue;
        });

        return entityValues;
    }

    getSelectedEntityValueDisplayName() {
        return this.entityValue.selectedValue;
    }

    getSelectedEntityAssetSize() {
        return this.entityAssetSize.selectedValue;
    }

    getSelectedKeywordValue() {
        return this.keyword.selectedValue;
    }

    getSelectedKeywordAssetSize() {
        return this.keywordAssetSize.selectedValue;
    }

    async getKBBImages() {
        if (this.getSelectedKeywordValue().length <= 0 && this.getSelectedKeywordAssetSize() == null) {
            this.toastingService.warning('Incomplete Input', `Please select 'Keyword' and 'Size`);
        } else if (this.getSelectedKeywordValue().length <= 0) {
            this.toastingService.warning('Incomplete Input', `Please select 'Keyword'`);
        } else if (this.getSelectedKeywordAssetSize() == null) {
            this.toastingService.warning('Incomplete Input', `Please select 'Size'`);
        } else if (this.selectedRow.length > 0) {
            this.toastingService.warning('Unsaved Changes', 'Please review your pending modifications in the Preview');
        } else {
            this.showLoader = true;
            this.waitingMessage = "Fetching Assets...";
            await this.imageAssetService.getKBBImagesToMap(
                this.getSelectedKeywordValue(),
                this.getSelectedKeywordAssetSize(),
            )
                .then((response) => {
                    const data = this.convertToArray(response);
                    this.loadDataToTable(data, this.KbbColumns);
                    this.toastingService.success('Fetch Assets', `Successfully Fetched ${this.assetData.length} Assets`);
                })
                .finally(() => {
                    this.showLoader = false;
                    this.resetWaitingMessage();
                });
        }
    }


    async generateStableDiffusionImages() {
        if (this.showLoader) {
            return;
        } else if (this.sdKeyword.selectedValue.length >= 2) {
            this.toastingService.warning('Keyword limit Exceed', `Please enter only one keyword`);
        } else if (this.sdKeyword.selectedValue.length <= 0 && (this.stableDiffusionMap.prompt == null || this.stableDiffusionMap.prompt.length == 0)) {
            this.toastingService.warning('Incomplete Input', `Please enter 'Keyword' or 'Prompt`);
        } else if (this.sdKeyword.selectedValue.length > 0 && this.stableDiffusionMap.prompt.length > 0 && !this.isPromptHasKeywordPlaceholder()) {
            this.toastingService.warning('Incorrect Format', `'{{kwd}}' placeholder is missing in prompt, as keyword is passed`);
        } else if (this.sdKeyword.selectedValue.length <= 0 && this.stableDiffusionMap.prompt.length > 0 && this.isPromptHasKeywordPlaceholder()) {
            this.toastingService.warning('Incorrect Format', `Remove '{{kwd}}' placeholder from prompt, as no keyword is passed`);
        } else if (this.selectedRow.length > 0) {
            this.toastingService.warning('Unsaved Changes', 'Please review your pending modifications in the Preview');
        } else {
            try {
                this.resetAssetData();
                this.resetRequestId();
                this.waitingMessage = 'Generating...'
                this.showLoader = true;
                const assetType = this.configConstantService.ASSET_TYPE_IMAGE;
                const payload = this.getStableDiffusionPayload();
                const response = await this.autoAssetService.generateAssetsFromOzil(payload, assetType);
                if (response['status'] == 'success') {
                    const imageData = this.formatStableDiffusionGenerateResponse(response, response['prompt'], response['negative_prompt']);
                    this.handleStableDiffusionImagesData(imageData);
                    this.showLoader = false;
                } else if (response['status'] == 'processing') {
                    const waitingTime = this.extractTimeFromMessage(response['message']);
                    this.waitingMessage = 'Request under process. Please wait for around ' + waitingTime + ' seconds';
                    await this.checkAndGetStableImagesData(response);
                }
            } catch (error) {
                console.error('Error:', error);
                this.toastingService.error('Failed To Process', 'Unable to process request');
                this.showLoader = false;
            }
        }
    }

    queueStableDiffusionImagesGeneration() {
        if (this.showLoader) {
            return;
        } else if (this.sdKeyword.selectedValue.length <= 0 && (this.stableDiffusionMap.prompt == null || this.stableDiffusionMap.prompt.length == 0)) {
            this.toastingService.warning('Incomplete Input', `Please enter 'Keyword' or 'Prompt`);
        } else if (this.sdKeyword.selectedValue.length > 0 && this.stableDiffusionMap.prompt.length > 0 && !this.isPromptHasKeywordPlaceholder()) {
            this.toastingService.warning('Incorrect Format', `'{{kwd}}' placeholder is missing in prompt, as keyword is passed`);
        } else if (this.sdKeyword.selectedValue.length <= 0 && this.stableDiffusionMap.prompt.length > 0 && this.isPromptHasKeywordPlaceholder()) {
            this.toastingService.warning('Incorrect Format', `Remove '{{kwd}}' placeholder from prompt, as no keyword is passed`);
        } else if (this.selectedRow.length > 0) {
            this.toastingService.warning('Unsaved Changes', 'Please review your pending modifications in the Preview');
        } else {
            this.resetAssetData();
            this.waitingMessage = 'Queuing ...'
            this.showLoader = true;
            const payload = this.getStableDiffusionPayload();
            const assetType = this.configConstantService.ASSET_TYPE_IMAGE;
            this.autoAssetService.queueAssetGeneration(payload, assetType)
                .then((response) => {
                    const requestId = response['request_id'];
                    this.toastingService.success('Request Processing', `Your request id ${requestId}`);
                })
                .catch((error) => {
                    this.toastingService.error('Request Failed', `Unable to process request`);
                })
                .finally(() => {
                    this.showLoader = false;
                    this.resetWaitingMessage();
                    this.getRequestDetails();
                });
        }
    }

    queueStableDiffusionImagesBulkDataRequest(bulkData) {
        bulkData = this.autoAssetService.getUpdatedKeyForBulkUploadData(bulkData, this.bulkUploadService.sdAssetKeySetForBulkUpload);
        this.showLoader = true;
        this.waitingMessage = 'Queuing ...'
        const assetType = this.configConstantService.ASSET_TYPE_IMAGE;
        this.autoAssetService.queueAssetGeneration(bulkData, assetType)
            .then((response) => {
                const requestId = response['request_id'];
                this.toastingService.success('Request Processing', `Your request id ${requestId}`);
            })
            .catch((error) => {
                this.toastingService.error('Request Failed', `Unable to process request`);
            })
            .finally(() => {
                this.showLoader = false;
                this.resetWaitingMessage();
                this.getRequestDetails();
            });
    }

    async checkAndGetStableImagesData(prevResponse) {
        const imageFetchId = prevResponse.img_fetch_id;
        let timeoutId;
        const checkImages = async () => {
            try {
                this.showLoader = true;
                const response = await this.imageAssetService.fetchStableDiffusionImagesWithImageFetchId(imageFetchId);

                if (response['status'] === this.configConstantService.SD_RESPONSE_STATUS_SUCCESS) {
                    const imageData = this.formatStableDiffusionGenerateResponse(response, prevResponse['prompt'], prevResponse['negative_prompt']);
                    this.handleStableDiffusionImagesData(imageData);
                    clearTimeout(timeoutId);
                    this.showLoader = false;
                    return;
                } else if (response['status'] === this.configConstantService.SD_RESPONSE_STATUS_PROCESSING) {
                    timeoutId = setTimeout(checkImages, 10000);
                    return;
                } else {
                    throw new Error('Failed to process: Unable to process request');
                }
            } catch (error) {
                clearTimeout(timeoutId);
                this.showLoader = false;
                this.toastingService.error('Failed To Process', 'Unable to process request');
            }
        };

        checkImages();
    }

    handleStableDiffusionImagesData(imageData) {
        this.extractFilterOptions(imageData, this.stableDiffusionFilterKeys);
        const data = this.addIsSelected(imageData);
        this.loadDataToTable(data, this.stableDiffusionColumns);
        this.toastingService.success('Generate Assets', `Successfully Generated ${this.assetData.length} Assets`);
    }

    loadDataToTable(data, columns) {
        this.assetData = data;
        this.loadNg2Table(this.assetData, columns);
        this.resetWaitingMessage();
    }

    getStableDiffusionPayload() {
        const payload = [{
            'ver': this.stableDiffusionMap.version,
            'kwd': this.sdKeyword.selectedValue.join(','),
            'pmt': this.stableDiffusionMap.prompt,
            'npmt': this.stableDiffusionMap.negativePrompt,
        }];
        return payload;
    }

    isPromptHasKeywordPlaceholder() {
        return this.stableDiffusionMap.prompt.includes('{{kwd}}');
    }

    formatStableDiffusionGenerateResponse(responseData, prompt: string, negativePrompt: string) {
        const keyword = this.sdKeyword.selectedValue[0];
        const version = this.stableDiffusionMap.version;

        const data = responseData.imgs.map(imageUrl => {
            return {
                imageUrl: imageUrl,
                keyword: keyword,
                prompt: prompt,
                negativePrompt: negativePrompt,
                version: version,
            };
        });
        return data;
    }

    async mapStableDiffusionImages() {
        this.isMappedShowLoader = true;
        this.waitingMessage = 'Mapping ...'
        const payload = this.previewAssetData;
        await this.imageAssetService.mapStableDiffusionImage(payload)
            .finally(() => {
                this.resetDataAfterMapStableDiffusionImages();
                this.isMappedShowLoader = false;
                this.resetWaitingMessage();
            });
    }


    extractTimeFromMessage(text) {
        const extractedNumber = text.split(':')[1];
        return 2 * Math.ceil(extractedNumber);
    }

    convertToArray(data) {
        const arrList = [];
        const keys = Object.keys(data);
        for (const entity of keys) {
            let arr = data[entity];
            arr = arr.map((item) => {
                item['keyword'] = entity;
                item['isSelected'] = false;
                return item;
            });
            arrList.push(...arr);
        }
        return arrList;
    }

    addIsSelected(data) {
        return data.map((item) => {
            item['isSelected'] = false;
            return item;
        });
    }

    loadNg2Table(data, columns) {
        if (data && data.length > 0) {
            this.settings = {
                columns: columns,
                actions: this.actions,
            };
            this.source.load(data);
        }
    }

    async getDropdownOptionSuggestion(event, selectedDimension) {
        const dimension = this.dimensionMap.get(selectedDimension);
        dimension.showLoader = true;
        this.filterInput = event.target.value;

        if (selectedDimension == 'Keyword' || selectedDimension == 'sdKeyword') {
            selectedDimension = this.entityNameMapForImage[this.DEMAND_BASIS].dropDownValue;
        } else if (selectedDimension == 'EntityAssetSize') {
            selectedDimension = 'Template Size'
        } else {
            selectedDimension = this.getSelectedEntityNameDropdownValue();
        }

        await this.autoAssetService.getAutoSelectSuggestions(
            selectedDimension,
            this.filterInput,
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
            });
    }

    async getMappedAssetListOnKey() {
        this.showPreviewButtonLoader = true;
        const entityName = this.getSelectedEntityNameValue();
        const entityValues = this.getSelectedEntityValue().join(',');
        const assetType = this.configConstantService.ASSET_TYPE_IMAGE;
        try {
            const data = await this.autoAssetService.getMappedAssetListOnKey(entityName, entityValues, assetType);
            this.showPreviewButtonLoader = false;
            return data.filter((item) => item.setId === this.getSetIdForImageSource());
        } catch (error) {
            this.showPreviewButtonLoader = false;
            return [];
        }
    }

    getSetIdForImageSource() {
        return this.imageAssetService.ImageSourceMap[this.imageSource.selectedValue].setId;
    }

    async saveMapKBBImages() {
        this.showLoader = true;
        this.waitingMessage = 'Mapping ...';
        const mappedRow = this.previewAssetData;
        const assetType = this.configConstantService.ASSET_TYPE_IMAGE;
        await this.autoAssetService.mapAsset(mappedRow, assetType)
            .then(() => {
                this.resetSelectedRow();
                this.getKBBImages();
            })
            .catch(() => {
                this.showLoader = false;
            })
            .finally(() => {
                this.resetWaitingMessage();
            });
    }


    async mapHostedImages(payload: any) {
        this.isMappedShowLoader = true;
        this.waitingMessage = "Mapping ...";
        await this.imageAssetService.mapHostedImage(payload)
            .finally(() => {
                this.isMappedShowLoader = false;
                this.resetWaitingMessage();
            });
    }


    async mapBulkHostedImages(payload: any) {
        this.isMappedShowLoader = true;
        this.waitingMessage = "Mapping ...";
        await this.imageAssetService.mapBulkHostedImages(payload)
            .then((response) => {
                const requestId = response['request_id'];
                this.toastingService.success('Mapping Request Processing', `Your request id is ${requestId}`);
            })
            .finally(() => {
                this.isMappedShowLoader = false;
                this.resetWaitingMessage();
            });
    }

    async mapLocalImages(payload: any) {
        this.isMappedShowLoader = true;
        this.waitingMessage = "Mapping ...";
        await this.imageAssetService.mapLocalImage(payload)
            .finally(() => {
                this.isMappedShowLoader = false;
                this.resetWaitingMessage();
            });
    }

    getActiveMappedList() {
        return this.existingMappedAssetListOnEntityValue.filter((item) => item.isActive);
    }

    handleSelectedRowData(rowData) {
        rowData.isSelected = !rowData.isSelected;
        this.selectedRow = this.assetData.filter((item) => item.isSelected);
        this.loadFilterData(this.stableDiffusionFilterKeys);
    }

    removePreviewRowData(rowData) {
        this.previewAssetData = this.previewAssetData.filter((item) => item['temp_id'] != rowData['temp_id']);
    }

    handleCommaSeparatedKeywordValue(event: any) {
        const keywordDimension = this.keyword;
        const inputArray = this.filterInput.split(',').map((value) => value.trim());
        const uniqueValues = new Set([...keywordDimension.selectedValue, ...inputArray]);
        keywordDimension.selectedValue = Array.from(uniqueValues);
        if (inputArray.length > 1 && !keywordDimension.showLoader) {
            const index = keywordDimension.selectedValue.lastIndexOf(this.filterInput);
            keywordDimension.selectedValue.splice(index, 1);
        }
    }

    handleCommaSeparatedStableDiffusionKeywordValue(event: any) {
        const dimension = this.sdKeyword;
        const inputArray = this.filterInput.split(',').map((value) => value.trim());
        const uniqueValues = new Set([...dimension.selectedValue, ...inputArray]);
        dimension.selectedValue = Array.from(uniqueValues);
        if (inputArray.length > 1) {
            const index = dimension.selectedValue.lastIndexOf(this.filterInput);
            dimension.selectedValue.splice(index, 1);
        }
    }

    extractFilterOptions(data: any[], keys) {
        const columnsToExtract = keys;
        columnsToExtract.forEach((heading) => {
            let value = [...new Set(data.map(item => item[heading]))];
            value = value.filter(option => this.utilService.isSet(option));
            this.filterMap.set(heading, value);
        });
    }

    getFilterOptions(selectedSelect: String) {
        let options = [];
        if (this.filterMap.has(selectedSelect)) {
            options = this.filterMap.get(selectedSelect);
        }
        return options;
    }

    loadFilterData(filterKeys) {
        this.filteredData = this.assetData.filter(item => {
            const conditionKeyword = this.enteredKeyword == null || item.keyword == this.enteredKeyword;
            const conditionPrompt = !this.enteredPrompt || item.prompt.includes(this.enteredPrompt);
            const conditionNegativePrompt = !this.enteredNegativePrompt || item.negativePrompt.includes(this.enteredNegativePrompt);
            const conditionVersion = !this.selectedVersion || item.version == this.selectedVersion;
            return conditionKeyword && conditionPrompt && conditionNegativePrompt && conditionVersion;
        });
        this.extractFilterOptions(this.filteredData, filterKeys);
        this.source.load(this.filteredData);
    }

    extractParticularColumnsDataFromJsonArray(data: any[], columnsToExtract: string[]): any[] {
        return data.map(obj =>
            columnsToExtract.reduce((acc, key) => {
                if (obj.hasOwnProperty(key)) {
                    acc[this.bulkUploadService.sdHeaderMapping[key]] = obj[key];
                }
                return acc;
            }, {}),
        );
    }

    filterSDData(data) {
        const result = { succeed: [], failed: [] };

        data.forEach(obj => {
            if (obj.hasOwnProperty('imageUrl') && obj['imageUrl'] !== '') {
                obj['set_id'] = this.getSetIdForImageSource();
                result.succeed.push(obj);
            } else {
                result.failed.push(obj);
            }
        });
        return result;
    }

    async getStableDiffusionCSVDataWithRequestId(requestId) {
        await this.imageAssetService.getStableDiffusionImagesFromRequestId(requestId)
            .then((response) => {
                this.csvDataToDownload = response['image_list'];
            })
            .catch((error) => {
                throw error;
            });
    }

    processAndDownloadSDData(requestId) {
        const csvNameSucceed = 'AutoAssetRequest ' + requestId + '(Success)';
        const csvNameFailed = 'AutoAssetRequest ' + requestId + '(Failed)';
        const successCSVKeys = ['keyword', 'version', 'imageUrl', 'prompt', 'negativePrompt', 'set_id'];
        const failCSVKeys = ['version', 'keyword', 'prompt', 'negativePrompt'];
        const sdData = this.filterSDData(this.csvDataToDownload);
        const successProcessedData = this.extractParticularColumnsDataFromJsonArray(sdData.succeed, successCSVKeys);
        const failedProcessedData = this.extractParticularColumnsDataFromJsonArray(sdData.failed, failCSVKeys);
        new ngxCsv(successProcessedData, csvNameSucceed, { headers: this.bulkUploadService.validStableDiffusionAssetHeadersForDownload });

        if (failedProcessedData.length > 0) {
            new ngxCsv(failedProcessedData, csvNameFailed, { headers: this.bulkUploadService.stableDiffusionSampleCsvColumns });
        }
    }

    async downloadStableDiffusionAssetDataCsv(request) {
        try {
            request.downloadLoader = true;
            const requestId = request.requestId;
            await this.getStableDiffusionCSVDataWithRequestId(requestId)
                .then(() => {
                    this.processAndDownloadSDData(requestId);
                    request.downloadLoader = false;
                });
        } catch {
            request.downloadLoader = false;
            this.toastingService.error('CSV Download Failed', 'Unable to download requested CSV');
        }
    }

    async downloadStableDiffusionAssetDataCsvFromRequestId(requestId) {
        if (requestId != null && requestId != '') {
            try {
                this.downloadLoader = true;
                const csvNameSucceed = 'AutoAssetRequest ' + requestId + '(Succeed)';
                const csvNameFailed = 'AutoAssetRequest ' + requestId + '(Failed)';
                await this.getStableDiffusionCSVDataWithRequestId(requestId)
                    .then(() => {
                        this.processAndDownloadSDData(requestId);
                        this.downloadLoader = false;
                    });
            } catch {
                this.downloadLoader = false;
                this.toastingService.error('CSV Download Failed', 'Unable to download requested CSV');
            }
        }
    }

    selectAll(event) {
        this.assetData.forEach((item) => {
            item.isSelected = event.target.checked;
        });
        this.selectedRow = this.assetData.filter((item) => item.isSelected);
        this.source.load(this.assetData);
        this.showPreviewInfoMessage();
    }

    isEmptyObject = (obj: any): boolean => {
        return obj == null || Object.keys(obj).length === 0;
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

    resetRequestId() {
        this.requestId = null;
    }

    resetRequestIdInput() {
        this.requestIdInput = null;
    }

    resetAssetData() {
        this.assetData = [];
        this.requestTaskInfo = null;
        this.source.empty();
        this.resetEntityData();
        this.resetFilters();
    }

    resetWaitingMessage() {
        this.waitingMessage = '';
    }

    resetStableDiffusionInputData() {
        this.stableDiffusionMap = {
            versionList: this.configConstantService.SD_VERSION_LIST,
            version: this.configConstantService.SD_DEFAULT_VERSION,
            keywordInput: '',
            keyword: [],
            negativePrompt: this.configConstantService.SD_NEGATIVE_PROMPT,
            prompt: this.configConstantService.SD_DEFAULT_PROMPT,
            negativePromptSelected: false,
        };
        this.resetRequestId();
    }

    resetEntityData() {
        this.entityName.selectedValue = this.DEMAND_BASIS;
        this.entityValue.selectedValue = [];
        this.entityAssetSize.selectedValue = null;
    }

    resetFilters() {
        this.enteredKeyword = null;
        this.enteredPrompt = null;
        this.enteredNegativePrompt = null;
        this.selectedVersion = null;
    }

    resetData() {
        this.selectedRow = [];
        this.assetData = [];
        this.previewAssetData = [];
        this.requestTaskInfo = null;
        this.keyword.selectedValue = [];
        this.keywordAssetSize.selectedValue = [];
        this.sdKeyword.selectedValue = [];
        this.resetStableDiffusionInputData();
        this.resetEntityData();
    }

    resetDataAfterMapStableDiffusionImages() {
        this.resetAssetDataIsSelected();
        this.resetSelectedRow();
    }

    resetAssetDataIsSelected() {
        this.assetData.map((item) => {
            item.isSelected = false;
        });
        this.source.load(this.assetData);
    }

    resetSelectedRow() {
        this.selectedRow = [];
    }

    isFetchImagesButtonDisabled(): boolean {
        if (this.assetSource.selectedValue == this.KBB) {
            const isKeywordEmpty = this.getSelectedKeywordValue().length <= 0;
            const isKeywordAssetSizeNull = this.getSelectedKeywordAssetSize() == null;
            const hasSelectedRow = this.selectedRow.length > 0;
            return isKeywordEmpty || isKeywordAssetSizeNull || hasSelectedRow;
        } else if (this.assetSource.selectedValue == this.STABLE_DIFFUSION) {
            const isKeywordEmpty = this.sdKeyword.selectedValue.length <= 0;
            const isPromptNullOrEmpty = this.stableDiffusionMap.prompt == null || this.stableDiffusionMap.prompt.length <= 0;
            const isNegativePromptNullOrEmpty = this.stableDiffusionMap.negativePrompt == null || this.stableDiffusionMap.negativePrompt.length <= 0;
            const isNegativePromptSelectedAndEmpty = this.stableDiffusionMap.negativePromptSelected && isNegativePromptNullOrEmpty;
            return isKeywordEmpty || isPromptNullOrEmpty || isNegativePromptSelectedAndEmpty;
        }
    }

    isPreviewButtonDisabled(): boolean {
        const hasSelectedRow = this.selectedRow.length === 0;
        const isEntityValueEmpty = this.getSelectedEntityValue().length == 0;
        const isSizeSelectVisible = this.imageAssetService.isSizeSelectVisible(this.getSelectedEntityNameValue());
        const isEntityValueNullOrDefault = this.getSelectedEntityValue().some((item) => item === "null$$null");
        const isEntityAssetSizeNull = this.getSelectedEntityAssetSize() == null;

        return hasSelectedRow || isEntityValueEmpty || (isSizeSelectVisible && (isEntityValueNullOrDefault || isEntityAssetSizeNull));
    }

    isGenerateSDButtonVisible() {
        const isKeywordEntered = this.sdKeyword.selectedValue.length > 0;
        const isPromptEntered = this.stableDiffusionMap.prompt.length > 0;
        const isPromptEmpty = this.stableDiffusionMap.prompt.length == 0;
        const isPromptHasKeywordPlaceholder = this.isPromptHasKeywordPlaceholder();

        const keywordEnteredWithPromptPlaceholder = isKeywordEntered && isPromptHasKeywordPlaceholder;
        const keywordEnteredAndPromptEmpty = isKeywordEntered && isPromptEmpty;
        const promptEnteredWithEmptyKeywordAndPlaceholder = isPromptEntered && !isKeywordEntered && !isPromptHasKeywordPlaceholder;

        return keywordEnteredWithPromptPlaceholder || keywordEnteredAndPromptEmpty || promptEnteredWithEmptyKeywordAndPlaceholder;
    }

    isGenerateSDButtonDisabled() {
        const keywordLength = this.sdKeyword.selectedValue.length;
        return keywordLength > 1;
    }

    showPreviewInfoMessage() {
        if (this.showMessage) {
            this.toastingService.info('Don\'t Forget to Save', 'Click on \'Preview & Save\' to see changes and save.');
        }
        this.showMessage = false;
    }

    isRequestDetailsTextVisible() {
        return this.isSelectedSource(this.STABLE_DIFFUSION) && this.requestId != null;
    }

    openPreviewModal() {
        if (this.assetSource.selectedValue == this.KBB) {
            this.KBBReviewModal();
        } else if (this.assetSource.selectedValue == this.STABLE_DIFFUSION) {
            this.stableDiffusionPreviewModal();
        }
    }

    convertToPreviewData(data) {
        const list = [];
        let tempId = 1;

        data.forEach(obj => {
            const entityValues = this.getSelectedEntityValue();

            entityValues.forEach(entityValue => {
                const newObj = { ...obj };
                newObj['temp_id'] = tempId;
                newObj['entity_name'] = this.getSelectedEntityNameValue();
                newObj['entity_value'] = entityValue;
                newObj['key_value'] = (this.configConstantService.ASSET_TYPE_IMAGE + this.configConstantService.KEY_VALUE_SEPARATOR + this.getSelectedEntityNameValue() + this.configConstantService.KEY_VALUE_SEPARATOR + entityValue).toLowerCase();
                newObj['set_id'] = this.imageAssetService.ImageSourceMap[this.imageSource.selectedValue].setId;
                newObj['asset_size'] = this.getSelectedEntityAssetSize();
                if (this.isSelectedSource(this.STABLE_DIFFUSION)) {
                    newObj['asset_value'] = obj.imageUrl;
                }
                list.push(newObj);
                tempId++;
            });
        });

        return list;
    }

    async KBBReviewModal() {
        if (this.showPreviewButtonLoader) {
            return;
        } else if (this.selectedRow.length <= 0 && this.getSelectedEntityValue().length == 0) {
            this.toastingService.warning('Incomplete Input', `Please select '${this.getSelectedEntityName()}'and Image`);
        } else if (this.selectedRow.length <= 0) {
            this.toastingService.warning('Nothing to Preview', 'There\'s No Change In Assets Mappings');
        } else if (this.getSelectedEntityValue().length == 0) {
            this.toastingService.warning('Incomplete Input', `Please select '${this.getSelectedEntityName()}'`);
        } else {
            this.existingMappedAssetListOnEntityValue = await this.getMappedAssetListOnKey();
            const previewModal = this.modalService.open(PreviewModalComponent, {
                size: 'lg',
                scrollable: true,
            });
            this.previewAssetData = this.convertToPreviewData(this.selectedRow);
            previewModal.componentInstance.previewModalName = this.configConstantService.KBB_MAP_IMAGE_ASSET_PREVIEW_MODAL;
            previewModal.componentInstance.receivedColumns = this.mapKBBImagesPreviewModalColumns;
            previewModal.componentInstance.previewData = this.previewAssetData;
            previewModal.componentInstance.imageUrlKeyName = 'path';
            previewModal.componentInstance.existingMappedAssetList = this.existingMappedAssetListOnEntityValue;
            previewModal.componentInstance.activeMappedAssetList = this.getActiveMappedList();
            previewModal.componentInstance.entityName = this.getSelectedEntityNamePlaceholderValue();
            previewModal.componentInstance.entityValue = this.getSelectedEntityValueDisplayName();
            const childInstance: PreviewModalComponent = previewModal.componentInstance as PreviewModalComponent;
            childInstance.removeRowEvent.subscribe((row) => {
                this.removePreviewRowData(row);
                previewModal.componentInstance.previewData = this.previewAssetData;
                if (this.previewAssetData.length <= 0) {
                    previewModal.dismiss('close');
                }
            });
            childInstance.saveButtonEvent.subscribe(() => {
                this.saveMapKBBImages();
            });
        }
    }

    async stableDiffusionPreviewModal() {
        if (this.showPreviewButtonLoader) {
            return;
        } else if (this.selectedRow.length <= 0 && this.getSelectedEntityValue().length == 0) {
            this.toastingService.warning('Incomplete Input', `Please select '${this.getSelectedEntityName()}'and Image`);
        } else if (this.selectedRow.length <= 0) {
            this.toastingService.warning('Nothing to Preview', 'There\'s No Change In Assets Mappings');
        } else if (this.getSelectedEntityValue().length == 0) {
            this.toastingService.warning('Incomplete Input', `Please select '${this.getSelectedEntityName()}'`);
        } else {
            this.existingMappedAssetListOnEntityValue = await this.getMappedAssetListOnKey();
            const previewModal = this.modalService.open(PreviewModalComponent, {
                size: 'lg',
                scrollable: true,
            });
            this.previewAssetData = this.convertToPreviewData(this.selectedRow);
            previewModal.componentInstance.previewModalName = this.configConstantService.SD_MAP_ASSET_PREVIEW_MODAL;
            previewModal.componentInstance.receivedColumns = this.mapStableDiffusionImagesPreviewModalColumns;
            previewModal.componentInstance.previewData = this.previewAssetData;
            previewModal.componentInstance.imageUrlKeyName = 'imageUrl';
            previewModal.componentInstance.existingMappedAssetList = this.existingMappedAssetListOnEntityValue;
            previewModal.componentInstance.activeMappedAssetList = this.getActiveMappedList();
            previewModal.componentInstance.entityName = this.getSelectedEntityNamePlaceholderValue();
            previewModal.componentInstance.entityValue = this.getSelectedEntityValueDisplayName();
            const childInstance: PreviewModalComponent = previewModal.componentInstance as PreviewModalComponent;
            childInstance.removeRowEvent.subscribe((row) => {
                this.removePreviewRowData(row);
                previewModal.componentInstance.previewData = this.previewAssetData;
                if (this.previewAssetData.length <= 0) {
                    previewModal.dismiss('close');
                }
            });
            childInstance.saveButtonEvent.subscribe(() => {
                this.mapStableDiffusionImages();
            });
        }
    }

    openStableDiffusionBulkUploadModal() {
        const bulkModal = this.modalService.open(BulkUploadModalComponent, {
            size: 'md',
            scrollable: true,
        });

        bulkModal.componentInstance.parentComponentName = this.configConstantService.STABLE_DIFFUSION;
        bulkModal.componentInstance.modalHeader = "Bulk SD Image Generation";
        bulkModal.componentInstance.modalSaveButtonName = "Queue";
        bulkModal.componentInstance.sampleCsvData = this.bulkUploadService.stableDiffusionSampleCsvData;
        bulkModal.componentInstance.sampleCsvColumns = this.bulkUploadService.stableDiffusionSampleCsvColumns;
        bulkModal.componentInstance.csvColumns = this.bulkUploadService.stableDiffusionBulkUploadMandatoryColumns;
        const childInstance: BulkUploadModalComponent = bulkModal.componentInstance as BulkUploadModalComponent;
        childInstance.generateEvent.subscribe((bulkData) => {
            this.queueStableDiffusionImagesBulkDataRequest(bulkData);
        });
    }

    openHostedUrlUploadModal() {
        const modal = this.modalService.open(ImageUploadModalComponent, {
            size: 'md',
            backdrop: 'static',
            scrollable: true,
        });
        modal.componentInstance.modalName = this.configConstantService.HOSTED_IMAGE_UPLOAD_MODAL;

        const childInstance: ImageUploadModalComponent = modal.componentInstance as ImageUploadModalComponent;
        childInstance.mapEvent.subscribe((payload) => {
            this.mapHostedImages(payload);
        });
        childInstance.bulkMapEvent.subscribe((payload) => {
            this.mapBulkHostedImages(payload);
        });
    }

    openLocalImageUploadModal() {
        const modal = this.modalService.open(ImageUploadModalComponent, {
            size: 'md',
            backdrop: 'static',
            scrollable: true,
        });
        modal.componentInstance.modalName = this.configConstantService.LOCAL_IMAGE_UPLOAD_MODAL;
        modal.componentInstance.parentComponentName = this.configConstantService.STABLE_DIFFUSION;

        const childInstance: ImageUploadModalComponent = modal.componentInstance as ImageUploadModalComponent;
        childInstance.mapEvent.subscribe((payload) => {
            this.mapLocalImages(payload);
        });
    }

    isPromptVisible(): boolean {
        return this.stableDiffusionMap.version !== this.configConstantService.SD_DEFAULT_VERSION;
    }
}
