import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {AutoAssetService} from '../@core/data/auto_asset/auto-asset.service';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {ToastingService} from '../@core/utils/toaster.service';
import {ConversionMapService} from '../@core/data/conversion-map.service';
import {AutoAssetConstantsService} from '../@core/data/auto-asset-constants.service';
import {UtilService} from '../@core/utils/util.service';
import {ImageAssetService} from '../@core/data/auto_asset/image-asset.service';
import {PrimeNGConfig} from 'primeng/api'
import {DomSanitizer} from '@angular/platform-browser';
import {AutoAssetEntityDropdownService} from '../@core/data/auto_asset/auto-asset-entity-dropdown.service';
import {C2aAssetService} from '../@core/data/auto_asset/c2a-asset.service';
import {NgSelectComponent} from '@ng-select/ng-select';
import {TitleAssetService} from '../@core/data/auto_asset/title-asset.service';
import {QueryParamService} from '../@core/utils/query-param.service';

@Component({
    selector: 'ngx-auto-asset-v2',
    templateUrl: './auto-asset-v2.component.html',
    styleUrls: ['./auto-asset-v2.component.css', './shared-style.css'],
})
export class AutoAssetV2Component implements OnInit {
    @ViewChild(NgSelectComponent) ngSelectComponent: NgSelectComponent;
    @ViewChild('fileInput') fileInput: ElementRef;

    isCardBodyVisibleImage = true;
    isCardBodyVisibleTitle = true;
    isCardBodyVisibleCta = true;


    isManualAssetVisible = false;
    isGenerateImageAssetVisible = true;
    isManualTitleAssetVisible = false;
    isGenerateTitleAssetVisible = true;

    showDialog: boolean = false;
    showConfigInfo: boolean = false;
    visibleSidebar: boolean = false;
    activeSidebar = this.constant.ASSET_TYPE_C2A;
    activeDialog = this.constant.ASSET_TYPE_IMAGE;
    activeConfigDialog = this.constant.ASSET_TYPE_IMAGE;
    uploadedFiles: any[] = [];
    configData: any[] = [];

    display = true;
    dimensionMap: Map<string, any> = new Map();
    entityValueInput: any;

    showLoader = false;
    waitingMessage = '';

    settings = {};
    imageAssetData = [];
    titleAssetData = [];
    c2aAssetData = [];
    previewAssetData = [];

    actions: {
        add: false,
        edit: false,
        delete: false,
    }

    toggledRows: any[] = [];
    showMessage: boolean;

    entityNameMap = this.autoAssetService.EntityNameMapForAutoAsset;

    imageSources: any;
    entityName: any;
    entityValue: any;
    entityAssetSize: any;
    keyword: any;
    kbbImageSize: any;
    version: any;
    imageSource: any;
    imageUrl: any;
    prompt: any;
    negativePrompt: any;
    c2a: any;
    domain: any;
    demandBasis: any;

    manualTitle: any;
    demandKeyword: any;
    publisherUrl: any;
    publisherPageTitle: any;
    titlePrompt: any;

    fetchedAsset: any[];

    imageLoader = false;
    titleLoader = false;
    c2aLoader = false;

    originalConfigDataJson = {};
    configDataJson = {};
    parentsIds: any;
    showAddConfig = false;
    upsertedConfigs: Set<any> = new Set();
    newConfigDetails: any = {};

    constructor(public autoAssetService: AutoAssetService,
                public imageAssetService: ImageAssetService,
                public modalService: NgbModal,
                private toastingService: ToastingService,
                public constant: AutoAssetConstantsService,
                private conversionMapService: ConversionMapService,
                private utilService: UtilService,
                private primengConfig: PrimeNGConfig,
                private autoAssetConstant: AutoAssetConstantsService,
                private sanitizer: DomSanitizer,
                public dropDownService: AutoAssetEntityDropdownService,
                public c2aAssetService: C2aAssetService,
                public titleAssetService: TitleAssetService,
                private queryParamService: QueryParamService,
    ) {

        this.dropDownService.setEntityNameMap(this.entityNameMap);
        const entityNameOptions = Object.keys(this.entityNameMap);

        this.dimensionMap.set('EntityName', {
            options: entityNameOptions,
            selectedValue: this.queryParamService.getQueryParamValue('entity_name') || entityNameOptions[0],
            showLoader: false,
        });
        this.dimensionMap.set('EntityValue', {
            options: [],
            selectedValue: this.queryParamService.getQueryParamValue('entity_value')
                ? this.queryParamService.getQueryParamValue('entity_value').split(',') : [],
            showLoader: false,
        });
        this.dimensionMap.set('EntityAssetSize', {
            options: [],
            selectedValue: this.queryParamService.getQueryParamValue('asset_size') || null,
            showLoader: false,
        });
        const setIds = Object.keys(imageAssetService.ImageSourceMap);
        this.dimensionMap.set('setId', {
            options: setIds,
            selectedValue: setIds[0],
            showLoader: false,
        });
        this.dimensionMap.set('imageUrl', {
            selectedValue: null,
        });
        this.dimensionMap.set('keyword', {
            options: [],
            selectedValue: [],
            showLoader: false,
        });
        this.dimensionMap.set('imageSize', {
            options: this.autoAssetConstant.KEYWORD_ASSET_SIZE,
            selectedValue: null,
            showLoader: false,
        });
        this.dimensionMap.set('version', {
            options: ['GPT_GENERATED_PROMPT', 'USER_PROMPT'],
            selectedValue: 'GPT_GENERATED_PROMPT',
            showLoader: false,
        });
        this.dimensionMap.set('prompt', {
            selectedValue: this.autoAssetConstant.SD_DEFAULT_PROMPT,
        });
        this.dimensionMap.set('negativePrompt', {
            selectedValue: this.autoAssetConstant.SD_NEGATIVE_PROMPT,
        });

        const sources: {} = {
            'KBB': {'name': 'KBB', 'setId': '3'},
            'STABLE DIFFUSION': {'name': 'STABLE DIFFUSION', 'setId': '9'}
        };

        this.dimensionMap.set('source', {
            options: Object.values(sources),
            selectedValue: Object.values(sources)[0],
        });
        this.dimensionMap.set('c2a', {
            selectedValue: null,
        });

        this.dimensionMap.set('domain', {
            options: [],
            selectedValue: this.queryParamService.getQueryParamValue('domain') || null,
            showLoader: false,
        });

        this.dimensionMap.set('manualTitle', {
            options: [],
            selectedValue: [],
            showLoader: false,
        });

        this.dimensionMap.set('demandKeyword', {
            options: [],
            selectedValue: this.queryParamService.getQueryParamValue('demand_keyword') || null,
            showLoader: false,
        });
        this.dimensionMap.set('publisherUrl', {
            options: [],
            selectedValue: null,
            showLoader: false,
        });
        this.dimensionMap.set('publisherPageTtile', {
            options: [],
            selectedValue: null,
            showLoader: false,
        });

        this.dimensionMap.set('titlePrompt', {
            selectedValue: this.autoAssetConstant.OZIL_GENERATED_TITLE_DEFAULT_PROMPT,
        });
        this.dimensionMap.set('Demand basis', {
            options: [],
            selectedValue: this.queryParamService.getQueryParamValue("demand_basis"),
            showLoader: false,
        });

        this.imageSources = this.dimensionMap.get('source');
        this.entityName = this.dimensionMap.get('EntityName');
        this.entityValue = this.dimensionMap.get('EntityValue');
        this.entityAssetSize = this.dimensionMap.get('EntityAssetSize');
        this.keyword = this.dimensionMap.get('keyword');
        this.imageSource = this.dimensionMap.get('setId');
        this.kbbImageSize = this.dimensionMap.get('imageSize');
        this.version = this.dimensionMap.get('version');
        this.prompt = this.dimensionMap.get('prompt');
        this.negativePrompt = this.dimensionMap.get('negativePrompt');
        this.imageUrl = this.dimensionMap.get('imageUrl');
        this.c2a = this.dimensionMap.get('c2a');
        this.domain = this.dimensionMap.get('domain');

        this.manualTitle = this.dimensionMap.get('manualTitle');
        this.demandKeyword = this.dimensionMap.get('demandKeyword');
        this.publisherUrl = this.dimensionMap.get('publisherUrl');
        this.publisherPageTitle = this.dimensionMap.get('publisherPageTtile');
        this.titlePrompt = this.dimensionMap.get('titlePrompt');
        this.demandBasis = this.dimensionMap.get('Demand basis');

        this.settings = {
            actions: false,
            noDataMessage: ''
        }
        this.showMessage = true;
        this.fetchOnQueryParams();
    }

    ngOnInit(): void {
    }

    fetchOnQueryParams() {
        this.fetchConfig();
        if (this.isValidEntityForImage() || this.isValidEntityForTitle() || this.isValidEntityForC2a()) {
            this.getAsset();
        }
    }

    showSidebar(type) {
        this.fetchedAsset = [];
        this.activeSidebar = type;
        this.visibleSidebar = true;
        document.body.style.overflow = 'hidden';
    }

    hideSidebar() {
        this.resetSideBarData();
        this.visibleSidebar = false;
        document.body.style.overflow = 'unset';
    }

    setEntityNameParam() {
        this.queryParamService.setMultipleQueryParams({
            entity_name: this.entityName.selectedValue,
            entity_value: null,
            domain: null,
            demand_basis: null,
            asset_size: null,
        });
    }

    setEntityValueParam() {
        this.queryParamService.setQueryParam('entity_value', this.entityValue.selectedValue.join(','));
    }

    setAssetSizeParam() {
        this.queryParamService.setQueryParam('asset_size', this.entityAssetSize.selectedValue);
    }

    setDomainParam() {
        this.queryParamService.setQueryParam('domain', this.domain.selectedValue);
    }

    setDemandBasisParam() {
        this.queryParamService.setQueryParam('demand_basis', this.demandBasis.selectedValue);
    }

    getSelectedEntityName() {
        return this.entityName.selectedValue;
    }

    getSelectedEntityNameValue() {
        return this.entityNameMap[this.entityName.selectedValue]?.value;
    }

    getSelectedEntityNamePlaceholderValue() {
        return this.entityNameMap[this.getSelectedEntityName()]?.placeholder;
    }

    toggleCardBody(cardType: string) {
        switch (cardType) {
            case 'image':
                this.isCardBodyVisibleImage = !this.isCardBodyVisibleImage;
                break;
            case 'title':
                this.isCardBodyVisibleTitle = !this.isCardBodyVisibleTitle;
                break;
            case 'cta':
                this.isCardBodyVisibleCta = !this.isCardBodyVisibleCta;
                break;
            case 'manualAsset':
                this.isManualAssetVisible = !this.isManualAssetVisible;
                break;
            case 'generateImageAsset':
                this.isGenerateImageAssetVisible = !this.isGenerateImageAssetVisible;
                break;
            case 'manualTitleAsset':
                this.isManualTitleAssetVisible = !this.isManualTitleAssetVisible;
                break;
            case 'generateTitleAsset':
                this.isGenerateTitleAssetVisible = !this.isGenerateTitleAssetVisible;
                break;
        }
    }

    setImageSource(source) {
        this.imageSources.selectedValue = source;
    }

    resetImageSourceInput() {
        this.keyword.selectedValue = null;
    }

    getSelectedEntityNameDropdownValue() {
        return this.entityNameMap[this.getSelectedEntityName()]?.dropDownValue;
    }

    showConfigDialogData(assetType) {
        this.activeConfigDialog = assetType;
        this.showConfigInfo = true;
    }

    closeConfigDialogData() {
        this.showConfigInfo = false;
    }

    showPreviewDialog(assetType) {
        this.activeDialog = assetType;
        this.showDialog = true;
    }

    hidePreviewDialog() {
        this.showDialog = false;
    }

    isDialogActive(assetType) {
        return this.activeDialog === assetType;
    }

    isAssetChanged(assets) {
        return assets.some(item => item.isChanged);
    }

    getSelectedKeywordValue() {
        return this.keyword.selectedValue;
    }

    isSelectedSource(source) {
        return this.imageSources.selectedValue.name === source;
    }

    isPromptVisible(): boolean {
        return this.version.selectedValue !== this.constant.SD_DEFAULT_VERSION;
    }

    getSelectedEntityValue() {
        let entityValueList = [];
        if (this.imageAssetService.isSizeSelectVisible(this.getSelectedEntityNameValue())) {
            entityValueList = this.entityValue.selectedValue.map((item) =>
                this.autoAssetService.extractIdUsingRegex(item) + this.constant.DOUBLE_DOLLAR + this.getSelectedEntityAssetSize()
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

    getSelectedEntityAssetSize() {
        return this.entityAssetSize.selectedValue;
    }

    getDemandBasisValue() {
        return this.demandBasis.selectedValue;
    }

    getSelectedEntityValueForTitle() {
        let entityValueList = [];
        if (this.titleAssetService.isMultipleEntityValueInputBox(this.getSelectedEntityNameValue())) {
            entityValueList = this.entityValue.selectedValue.map((item) =>
                this.autoAssetService.extractIdUsingRegex(item) + this.constant.DOUBLE_DOLLAR +
                this.getDemandBasisValue(),
            );
        } else {
            entityValueList = this.entityValue.selectedValue.map((item) =>
                this.autoAssetService.extractIdUsingRegex(item),
            );
        }
        return entityValueList;
    }

    getSelectedEntityValueForC2a() {
        if (this.c2aAssetService.isAdditionAlInputRequired(this.dropDownService.getSelectedEntityNameValue(this.entityName))) {
            return this.dropDownService.getEntityIdsFromSelectedEntityValues(this.entityValue).map((entityValue) => {
                return this.domain.selectedValue + '$$' + entityValue;
            })
        }
        return this.dropDownService.getEntityIdsFromSelectedEntityValues(this.entityValue);
    }

    getSelectedEntityValueForConfig() {
        const entityNameValue = this.dropDownService.getSelectedEntityNameValue(this.entityName);
        const entityValue = this.dropDownService.getEntityIdsFromSelectedEntityValues(this.entityValue);
        if (entityNameValue === 'DOMAIN$$ALL_DEMAND') {
            return this.entityValue.selectedValue + this.constant.DOUBLE_DOLLAR + this.constant.ALL_DEMAND;
        } else if (this.isEntityNameIsMulti(this.entityName.selectedValue)) {
            return this.domain.selectedValue + this.constant.DOUBLE_DOLLAR + entityValue;
        }
        return entityValue;
    }

    getFinalEntityValue() {
        let entityNameValue = this.dropDownService.getSelectedEntityNameValue(this.entityName);
        let finalEntityValueList = [];

        if (this.imageAssetService.isSizeSelectVisible(entityNameValue)) {
            finalEntityValueList = this.entityValue.selectedValue.map((item) =>
                this.autoAssetService.extractIdUsingRegex(item) +
                this.constant.DOUBLE_DOLLAR +
                this.entityAssetSize?.selectedValue); // Append entity size
        } else if (this.titleAssetService.isMultipleEntityValueInputBox(entityNameValue)) {
            finalEntityValueList = this.entityValue.selectedValue.map((item) =>
                this.autoAssetService.extractIdUsingRegex(item) +
                this.constant.DOUBLE_DOLLAR +
                this.demandBasis?.selectedValue); // Append demand basis
        } else if (this.c2aAssetService.isAdditionAlInputRequired(entityNameValue)) {
            return this.dropDownService.getEntityIdsFromSelectedEntityValues(this.entityValue).map((entityValue) => {
                return this.domain.selectedValue + '$$' + entityValue;
            });
        } else if (entityNameValue === 'DOMAIN$$ALL_DEMAND') {
            return this.entityValue.selectedValue +
                this.constant.DOUBLE_DOLLAR +
                this.constant.ALL_DEMAND;
        } else if (this.isEntityNameIsMulti(this.entityName.selectedValue)) {
            return this.domain?.selectedValue +
                this.constant.DOUBLE_DOLLAR +
                this.dropDownService.getEntityIdsFromSelectedEntityValues(this.entityValue);
        } else {
            finalEntityValueList = this.entityValue.selectedValue.map((item) =>
                this.autoAssetService.extractIdUsingRegex(item));
        }

        return finalEntityValueList.join(',');
    }

    async getAsset() {
        if (this.imageLoader || this.titleLoader || this.c2aLoader) {
            return;
        } else if (!this.isEntityValueDisabled() && this.getSelectedEntityValue().length <= 0) {
            this.toastingService.warning('Incomplete Input', 'Please Enter Demand Basis to Show Mapped Assets')
        } else if (this.toggledRows.length > 0) {
            this.toastingService.warning('Unsaved Changes', 'Please review your pending modifications in the Preview')
        } else {
            this.resetAssetData();
            this.imageLoader = true;
            this.titleLoader = true;
            this.c2aLoader = true;
            this.getMappedAsset(this.constant.ASSET_TYPE_IMAGE);
            this.getMappedAsset(this.constant.ASSET_TYPE_TITLE);
            this.getMappedAsset(this.constant.ASSET_TYPE_C2A);
            this.fetchConfig();
        }
    }

    async getKBBImages() {
        if (this.keyword.selectedValue.length <= 0 && this.kbbImageSize.selectedValue == null) {
            this.toastingService.warning('Incomplete Input', `Please select 'Keyword' and 'Size`);
        } else if (this.keyword.selectedValue.length <= 0) {
            this.toastingService.warning('Incomplete Input', `Please select 'Keyword'`);
        } else if (this.kbbImageSize.selectedValue == null) {
            this.toastingService.warning('Incomplete Input', `Please select 'Size'`);
        } else {
            this.showLoader = true;
            this.waitingMessage = 'Fetching Assets...';
            await this.imageAssetService.getKBBImagesToMap(
                this.keyword.selectedValue,
                this.kbbImageSize.selectedValue,
            )
                .then((response) => {
                    const data = this.formatKBBImageData(response);
                    this.fetchedAsset = data;
                    this.toastingService.success('Fetch Assets', `${this.imageSources.selectedValue.name} : Successfully Fetched ${this.fetchedAsset.length} Assets`);
                })
                .finally(() => {
                    this.showLoader = false;
                    this.resetWaitingMessage();
                });
        }
    }

    formatKBBImageData(data) {
        const arrList = [];
        const keys = Object.keys(data);
        for (const entity of keys) {
            let arr = data[entity];
            arr = arr.map((item) => {
                item['assetValue'] = item?.path;
                item['keyword'] = entity;
                item['isSelected'] = false;
                item['setId'] = '3';
                return item;
            });
            arrList.push(...arr);
        }
        return arrList;
    }

    async generateStableDiffusionImages() {
        if (this.showLoader) {
            return;
        } else if (this.keyword.selectedValue.length >= 2) {
            this.toastingService.warning('Keyword limit Exceed', `Please enter only one keyword`);
        } else if (this.keyword.selectedValue.length <= 0 && (this.prompt.selectedValue == null || this.prompt.selectedValue.length == 0)) {
            this.toastingService.warning('Incomplete Input', `Please enter 'Keyword' or 'Prompt`);
        } else if (this.prompt.selectedValue.length > 0 && this.prompt.selectedValue.length > 0 && !this.isPromptHasKeywordPlaceholder()) {
            this.toastingService.warning('Incorrect Format', `'{{kwd}}' placeholder is missing in prompt, as keyword is passed`);
        } else if (this.keyword.selectedValue.length <= 0 && this.prompt.selectedValue.length > 0 && this.isPromptHasKeywordPlaceholder()) {
            this.toastingService.warning('Incorrect Format', `Remove '{{kwd}}' placeholder from prompt, as no keyword is passed`);
        } else {
            try {
                this.waitingMessage = 'Generating Images...'
                this.showLoader = true;
                const assetType = this.constant.ASSET_TYPE_IMAGE;
                const payload = this.getStableDiffusionPayload();
                const response = await this.autoAssetService.generateAssetsFromOzil(payload, assetType);
                if (response['status'] === 'success') {
                    this.fetchedAsset = this.formatStableDiffusionGenerateResponse(response, response['prompt'], response['negative_prompt']);
                    this.showLoader = false;
                } else if (response['status'] === 'processing') {
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

    async checkAndGetStableImagesData(prevResponse) {
        const imageFetchId = prevResponse.img_fetch_id;
        let timeoutId;
        const checkImages = async () => {
            try {
                this.showLoader = true;
                const response = await this.imageAssetService.fetchStableDiffusionImagesWithImageFetchId(imageFetchId);

                if (response['status'] === this.constant.SD_RESPONSE_STATUS_SUCCESS) {
                    this.fetchedAsset = this.formatStableDiffusionGenerateResponse(response, prevResponse['prompt'], prevResponse['negative_prompt']);
                    clearTimeout(timeoutId);
                    this.showLoader = false;
                    return;
                } else if (response['status'] === this.constant.SD_RESPONSE_STATUS_PROCESSING) {
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

    isPromptHasKeywordPlaceholder() {
        return this.prompt.selectedValue.includes('{{kwd}}');
    }

    getStableDiffusionPayload() {
        const payload = [{
            'ver': this.version.selectedValue,
            'kwd': this.keyword.selectedValue.join(','),
            'pmt': this.prompt.selectedValue,
            'npmt': this.negativePrompt.selectedValue,
        }];
        return payload;
    }

    formatStableDiffusionGenerateResponse(responseData, prompt: string, negativePrompt: string) {
        const keyword = this.keyword.selectedValue[0];
        const version = this.version.selectedValue;

        const data = responseData.imgs.map(imageUrl => {
            return {
                assetValue: imageUrl,
                keyword: keyword,
                prompt: prompt,
                negativePrompt: negativePrompt,
                version: version,
                isSelected: false,
            };
        });
        return data;
    }

    extractTimeFromMessage(text) {
        const extractedNumber = text.split(':')[1];
        return 2 * Math.ceil(extractedNumber);
    }

    getMappedAsset(assetType) {
        const selectedEntityName = this.getSelectedEntityNameValue();
        const selectedEntityValueJoined = this.getFinalEntityValue();
        this.showAssetLoader(assetType, true);
        this.autoAssetService.getMappedAssetListOnKey(selectedEntityName, selectedEntityValueJoined,
            assetType).then((data) => {
            const assetData = this.formatMappedAsset(data);
            if (assetType === this.constant.ASSET_TYPE_IMAGE) {
                this.imageAssetData = assetData;
            } else if (assetType === this.constant.ASSET_TYPE_TITLE) {
                this.titleAssetData = assetData;
            } else if (assetType === this.constant.ASSET_TYPE_C2A) {
                this.c2aAssetData = assetData;
            }
            this.showAssetLoader(assetType, false);
            this.toastingService.success(`Fetch ${assetType} Assets`, `Successfully Fetched ${assetData?.length} Assets`);
        })
            .catch((error) => {
                this.showAssetLoader(assetType, false);
            });
    }

    formatMappedAsset(data) {
        data.map((item) => {
            item['isChanged'] = false;
        });
        data.sort((a: { isActive: boolean }, b: { isActive: boolean }) => Number(b.isActive) - Number(a.isActive));
        return data;
    }

    async getDropdownOptionSuggestion(event, selectedDimension) {
        const dimension = this.dimensionMap.get(selectedDimension);
        dimension.showLoader = true;
        this.entityValueInput = event.target.value;
        dimension.options = dimension.options.slice(1);
        if (this.entityValueInput?.length > 0) {
            dimension.options = [this.entityValueInput, ...dimension.options];
        }

        if (selectedDimension === 'EntityAssetSize' || selectedDimension == 'imageAssetSize') {
            selectedDimension = 'Template Size';
        } else if (selectedDimension === 'domain') {
            selectedDimension = 'Publisher Domain';
        } else if (selectedDimension === 'keyword' || selectedDimension === 'demandKeyword' || selectedDimension === 'manualTitle' || selectedDimension === 'Demand basis') {
            selectedDimension = 'Demand basis';
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

    filterChangedAsset(assetType) {
        let assets = [];
        if (assetType === this.constant.ASSET_TYPE_IMAGE) {
            assets = this.imageAssetData;
        } else if (assetType === this.constant.ASSET_TYPE_TITLE) {
            assets = this.titleAssetData;
        } else if (assetType === this.constant.ASSET_TYPE_C2A) {
            assets = this.c2aAssetData;
        }
        this.previewAssetData = assets.filter(item => item.isChanged);
        return this.previewAssetData;
    }

    formatImageMapData(data) {
        return data.flatMap(obj => {
            const entityValues = this.getSelectedEntityValue();
            return entityValues.map(entityValue => {
                const newObj = {...obj};
                newObj['entity_name'] = this.getSelectedEntityNameValue();
                newObj['entity_value'] = entityValue;
                newObj['key_value'] = (
                    this.constant.ASSET_TYPE_IMAGE +
                    this.constant.KEY_VALUE_SEPARATOR +
                    this.getSelectedEntityNameValue() +
                    this.constant.KEY_VALUE_SEPARATOR +
                    entityValue
                ).toLowerCase();
                newObj['set_id'] = this.imageSources.selectedValue.setId;
                newObj['asset_size'] = this.getSelectedEntityAssetSize();
                return newObj;
            });
        });
    }

    showAssetLoader(assetType, showLoader) {
        if (assetType === this.constant.ASSET_TYPE_IMAGE) {
            this.imageLoader = showLoader;
        } else if (assetType === this.constant.ASSET_TYPE_TITLE) {
            this.titleLoader = showLoader;
        } else if (assetType === this.constant.ASSET_TYPE_C2A) {
            this.c2aLoader = showLoader;
        }
    }

    async mapAsset(assetType) {
        this.showLoader = true;
        this.waitingMessage = `Mapping ${assetType.toLowerCase()}...`;
        const assetList = this.formatToMapData(this.getSelectedImagesToMap(), assetType);
        await this.autoAssetService.mapAsset(assetList, assetType)
            .then(() => {
                this.getMappedAsset(assetType);
            })
            .catch(() => {
                this.showLoader = false;
            })
            .finally(() => {
                this.showLoader = false;
                this.resetWaitingMessage();
            });
    }

    updateAsset(assetType) {
        this.waitingMessage = 'Updating...';
        this.showAssetLoader(assetType, true);
        this.autoAssetService.blockUnblockAssets(this.filterChangedAsset(assetType))
            .then(() => {
                this.getMappedAsset(assetType);
            })
            .catch(() => {
                this.showAssetLoader(assetType, false);
                this.resetWaitingMessage();
            });
    }

    formatToMapData(data, assetType) {
        if (assetType === this.constant.ASSET_TYPE_IMAGE) {
            return this.formatImageMapData(data);
        } else if (assetType === this.constant.ASSET_TYPE_TITLE) {
            return this.getFormattedMapDataForTitle(data);
        } else if (assetType === this.constant.ASSET_TYPE_C2A) {
            return this.formatC2aMapData(data);
        }
    }

    getSelectedImagesToMap() {
        return this.fetchedAsset.filter(item => item.isSelected);
    }

    mapHostedImages() {
        if (!this.entityName.selectedValue || this.entityValue.selectedValue.length == 0) {
            this.toastingService.warning('Incomplete Input', 'Entity name or value not selected.');
        } else if (!this.imageUrl.selectedValue || this.imageUrl.selectedValue.length <= 0) {
            this.toastingService.warning('Incomplete Input', 'Image URL not entered.');
        } else {
            this.showLoader = true;
            this.waitingMessage = 'Mapping Url Image...';
            const payload = this.generatePayloadForMapUrlImage();
            this.imageAssetService.mapHostedImage(payload)
                .finally(() => {
                    this.imageUrl.selectedValue = null;
                    this.showLoader = false;
                    this.resetWaitingMessage();
                    this.getMappedAsset(this.constant.ASSET_TYPE_IMAGE);
                });
        }
    }

    generatePayloadForMapUrlImage() {
        const entityValues = this.getSelectedEntityValue();
        const payload = entityValues.map(entityValue => ({
            entity_name: this.getSelectedEntityNameValue(),
            entity_value: entityValue,
            asset_size: this.getSelectedEntityAssetSize(),
            asset_value: this.imageUrl.selectedValue,
            set_id: this.imageAssetService.ImageSourceMap[this.imageSource.selectedValue].setId,
        }));

        return payload;
    }


    handleFileInput(event: any) {
        const files = event.target.files;

        let totalFileSize = this.uploadedFiles.reduce((totalSize, selectedFile) => totalSize + selectedFile.value.size, 0);
        for (let i = 0; i < files.length; i++) {
            totalFileSize += files[i].size;
        }

        if (totalFileSize >= 1048576) {
            this.toastingService.warning('File Size Exceeds', `The total size of selected files should be less than ${1} MB.`);
        } else if (this.uploadedFiles.length + event.target.files.length > this.autoAssetService.MAX_LOCAL_FILE_LIMIT) {
            this.toastingService.warning('File Limit Exceed', `Whoops! You can select only up to ${this.autoAssetService.MAX_LOCAL_FILE_LIMIT} files.`);
        } else {
            for (let i = 0; i < files.length; i++) {
                const file = files[i];
                if (!this.isDuplicate(file)) {
                    const data = {
                        value: file,
                        url: this.sanitizer.bypassSecurityTrustUrl(URL.createObjectURL(file))
                    }
                    this.uploadedFiles.unshift(data);
                }
            }
        }
    }

    isDuplicate(file) {
        return this.uploadedFiles.some((item) => item.value.name == file.name);
    }

    clearFileInput(): void {
        this.fileInput.nativeElement.value = '';
        this.uploadedFiles = [];
    }

    deleteImage(deletedItem) {
        this.uploadedFiles = this.uploadedFiles.filter((item) => item.value.name != deletedItem.value.name);
    }

    mapLocalImage() {
        if (!this.entityName.selectedValue || this.entityValue.selectedValue.length == 0) {
            this.toastingService.warning('Incomplete Input', 'Entity name or value not selected.');
        } else if (this.uploadedFiles.length <= 0) {
            this.toastingService.warning('Incomplete Input', 'No image file selected.');
        } else {
            this.showLoader = true;
            this.waitingMessage = 'Mapping Local Image...';
            let payload = {
                entity_name: this.getSelectedEntityNameValue(),
                entity_value: this.getSelectedEntityValue().join(','),
                asset_size: this.getSelectedEntityAssetSize(),
                asset_list: this.uploadedFiles,
                set_id: this.imageAssetService.ImageSourceMap[this.imageSource.selectedValue].setId,
            }
            this.imageAssetService.mapLocalImage(payload)
                .finally(() => {
                    this.showLoader = false;
                    this.resetWaitingMessage();
                    this.clearFileInput();
                    this.getMappedAsset(this.constant.ASSET_TYPE_IMAGE);
                });
        }
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

    isSizeNeededAndNotNull() {
        return this.imageAssetService.isSizeSelectVisible(this.getSelectedEntityNameValue()) && this.getSelectedEntityAssetSize() == null;
    }

    isFetchAssetButtonDisabled(): boolean {
        return this.getSelectedEntityValue().length <= 0 || this.toggledRows.length > 0 || this.isSizeNeededAndNotNull();
    }

    showPreviewInfoMessage() {
        if (this.showMessage) {
            this.toastingService.info('Don\'t Forget to Save', 'Click on \'Preview & Save\' to see changes and save.');
        }
        this.showMessage = false;
    }

    handleC2aInput(event) {
        this.c2a.selectedValue = event.target.value
    }

    addManualCta() {
        const manualC2as = this.c2a.selectedValue.split(',');
        this.fetchedAsset.push(...this.formatManualCta(manualC2as));
        this.c2a.selectedValue = null;
    }

    formatManualCta(c2as) {
        return c2as.map((c2a) => ({
            assetValue: c2a,
            isSelected: true,
        }));
    }

    formatC2aMapData(data) {
        return data.flatMap((item) => {
            return this.getSelectedEntityValueForC2a()
                .map((entityValue) => {
                    const newObj = {
                        ...item,
                        key_value: (
                            this.constant.ASSET_TYPE_TITLE +
                            this.constant.KEY_VALUE_SEPARATOR +
                            this.getSelectedEntityNameValue() +
                            this.constant.KEY_VALUE_SEPARATOR +
                            entityValue
                        ).toLowerCase(),
                        set_id: 1,
                        is_active: true,
                    };

                    if (this.entityName.selectedValue === 'Global') {
                        newObj['entity_name'] = this.dropDownService.getSelectedEntityNameValue(this.entityName);
                        newObj['entity_value'] = null;
                    } else {
                        newObj['entity_name'] = this.dropDownService.getSelectedEntityNameValue(this.entityName);
                        newObj['entity_value'] = this.getEntityValueForc2a(); // assign correct value if not 'Global'
                    }

                    return newObj;
                });
        });
    }

    getEntityValueForc2a() {
        const selectedEntityValues = this.dropDownService.getEntityIdsFromSelectedEntityValues(this.entityValue);
        if (this.c2aAssetService.isAdditionAlInputRequired(this.dropDownService.getSelectedEntityNameValue(this.entityName))) {
            return this.domain.selectedValue + '$$' + selectedEntityValues;
        }
        return selectedEntityValues;
    }

    addManualTitleAsset() {
        if (this.manualTitle.selectedValue?.length === 0) {
            return;
        }
        const titles = [{
            assetValue: this.manualTitle.selectedValue[0],
            isSelected: true,
        }];
        this.fetchedAsset = Array.from(new Set([...this.fetchedAsset, ...titles]));
        this.isGenerateTitleAssetVisible = false;
        this.manualTitle.selectedValue = [];
    }


    async generateTitle() {
        if (this.demandKeyword.selectedValue === null || this.demandKeyword.selectedValue.length === 0) {
            this.toastingService.warning('Demand Keyword empty', 'Please enter Demand Keyword');
        } else if (this.titlePrompt.length > 0 && !this.titlePrompt.includes('{{DEMAND_KEYWORD}}')) {
            this.toastingService.warning('{{DEMAND_KEYWORD}} missing in prompt', 'Please add {{DEMAND_KEYWORD}} in your prompt');
        } else {
            this.showLoader = true;
            this.waitingMessage = 'Generating Title...'
            const payloadForTitleAssetFromOzil = {
                demand_kwd: [this.demandKeyword.selectedValue],
                publisher_page_url: this.publisherUrl.selectedValue === null ? '' : this.publisherUrl.selectedValue?.label,
                publisher_page_title: this.publisherPageTitle.selectedValue === null ? '' : this.publisherPageTitle.selectedValue?.label,
                prompt: this.titlePrompt.selectedValue,
            };

            await this.autoAssetService.generateAssetsFromOzil([payloadForTitleAssetFromOzil],
                this.constant.ASSET_TYPE_TITLE)
                .then((response) => {
                    this.fetchedAsset = this.formatGeneratedTitles(response['ad_titles']);
                })
                .catch((error) => {
                    this.toastingService.error('Title Generate Failed', error?.response?.name);
                })
                .finally(() => {
                    this.showLoader = false;
                    this.waitingMessage = null;
                });
        }
    }

    formatGeneratedTitles(generatedTitles) {
        const titles = generatedTitles.map(title => {
            const newObj = {
                assetValue: title,
                demand_keyword: this.demandKeyword.selectedValue,
                publisher_page_title: this.publisherPageTitle.selectedValue,
                publisher_page_url: this.publisherUrl.selectedValue,
                isSelected: false,
            };
            return newObj;
        });

        return titles;
    }

    getFormattedMapDataForTitle(data) {
        const list = [];
        data.forEach(obj => {
            const entityValues = this.getSelectedEntityValueForTitle();
            entityValues.forEach(entityValue => {
                const newObj = {...obj};
                newObj['entity_name'] = this.getSelectedEntityNameValue();
                newObj['entity_value'] = entityValue;
                newObj['key_value'] = (this.constant.ASSET_TYPE_TITLE + this.constant.KEY_VALUE_SEPARATOR +
                    this.getSelectedEntityNameValue() + this.constant.KEY_VALUE_SEPARATOR + entityValue).toLowerCase();
                newObj['set_id'] = this.utilService.isSet(obj['demand_kwd'])
                    ? this.constant.AUTO_GENERATED_TITLE_SET_ID : this.constant.MANUAL_TITLE_SET_ID;
                newObj['size'] = null;
                newObj['keyword'] = obj['demand_keyword'] ?? '';
                newObj['is_active'] = 1;
                list.push(newObj);
            });
        });
        return list;
    }

    // Config JS Start

    toggleCollapse(config) {
        config.isCollapsed = !config.isCollapsed;
    }

    initializeUpsertedPropertyDetails() {
        this.newConfigDetails = {
            property: null,
            entity_name: null,
            entity_name_value: null,
            entity_value: null,
            domain: null,
            entity_name_options: this.filterEntityNameForSelectedEntityName(Object.keys(this.autoAssetService.EntityNameMapForConfig)),
            selected_entity_name: null,
            selected_entity_value: null,
            selected_domain: null,
            final_entity_value: null,
            valueOptions: [],
            value: null,
            is_active: true,
        }
    }

    isEntityNameIsMulti(entityName) {
        if (this.utilService.isSet(entityName)) {
            return this.entityNameMap[entityName]?.isMulti;
        }
        return false;
    }

    filterEntityNameForSelectedEntityName(entityNameOptions) {
        const allDemandIndex = entityNameOptions.indexOf('Domain + All Demand');
        const selectedIndex = entityNameOptions.indexOf(this.entityName.selectedValue);

        if (selectedIndex === -1) {
            return;
        }

        const domainPlusIds = entityNameOptions.slice(0, allDemandIndex + 1);
        const onlyIds = entityNameOptions.slice(allDemandIndex + 1);
        const filteredDomainPlusIds = domainPlusIds.slice(selectedIndex);
        const filteredOnlyIds = onlyIds.slice(selectedIndex - domainPlusIds.length);
        const filteredEntities = [...filteredDomainPlusIds, ...filteredOnlyIds];
        return filteredEntities.filter((item) => item != 'Global');
    }


    async fetchConfig() {
        if (!this.isValidEntityForConfig()) return;
        this.resetData();
        this.resetUpsertedConfigs();
        this.waitingMessage = 'Fetching';
        this.showLoader = true;
        this.showAddConfig = false;
        let entityName = this.dropDownService.getSelectedEntityNameValue(this.entityName);
        if (entityName === 'AD_GROUP_ID') {
            entityName = 'ADGROUP_ID';
        }
        const entityValue = this.getSelectedEntityValueForConfig();
        this.newConfigDetails.selected_entity_name = entityName;
        this.newConfigDetails.selected_entity_value = entityValue;
        this.newConfigDetails.selected_domain = this.domain.selectedValue;
        await this.autoAssetService.getAutoAssetConfig(entityName, entityValue)
            .then((response) => {
                const configData = response['config'];
                this.parentsIds = response['entity_ids'];
                this.originalConfigDataJson = this.formatConfigDataJson(configData);
                this.configDataJson = JSON.parse(JSON.stringify(this.originalConfigDataJson));
                this.configData = this.objectToList(this.configDataJson);
                this.setNewConfigDetails();
            })
            .catch((error) => {
                if (error?.error?.response.reason === 'Entity not found') {
                    this.toastingService.warning('No Config Found', 'Please enter valid entity details');
                } else {
                    this.toastingService.error("Fetching Failed", "Unable to fetch Config")
                }
            })
            .finally(() => {
                this.showAddConfig = true;
                this.waitingMessage = '';
                this.showLoader = false;
            });
    }

    formatConfigDataJson(data) {
        const groupedData = data.reduce((acc, item) => {
            const property = `${item.property}`;

            if (!acc[property]) {
                acc[property] = {
                    properties: [],
                    property: item.property,
                    isCollapsed: true,
                };
            }

            acc[property].properties.push({
                entity_name: item.entity_name,
                entity_value: item.entity_value,
                property: property,
                site_name: item.site_name,
                value: item.value,
                is_active: item.is_active,
            });

            return acc;
        }, {});

        Object.keys(groupedData).forEach(key => {
            groupedData[key].properties = this.sortProperties(groupedData[key].properties);
        });

        return groupedData;
    }

    sortProperties(properties) {
        properties.sort((a, b) => {
            const indexA = this.autoAssetService.ConfigEntityNameOrder.indexOf(a.entity_name);
            const indexB = this.autoAssetService.ConfigEntityNameOrder.indexOf(b.entity_name);

            if (indexA === -1 && indexB === -1) return 0;
            if (indexA === -1) return 1;
            if (indexB === -1) return -1;

            return indexB - indexA;
        });
        return properties;
    }


    objectToList(data) {
        const configList = Object.values(data);
        return configList;
    }

    setPropertyValueOptions() {
        if (this.utilService.isSet(this.newConfigDetails.property)) {
            if (this.autoAssetService.configOnlyAssetProperties.includes(this.newConfigDetails.property)) {
                this.newConfigDetails.valueOptions = [true, false];
            } else if (this.autoAssetService.configAutoAssetTestProperties.includes(this.newConfigDetails.property)) {
                this.newConfigDetails.valueOptions = this.autoAssetService.autoAssetConfigPercentages;
            }
        }
    }

    setNewConfigDetails()
        :
        void {
        if (this.isAllDemand()
        ) {
            this.newConfigDetails.entity_name = 'Domain + All Demand';
            this.newConfigDetails.entity_name_value = 'DOMAIN$$ALL_DEMAND';
            this.newConfigDetails.entity_value = 'ALL_DEMAND';
            this.newConfigDetails.final_entity_value = this.entityValue.selectedValue + '$$' + 'ALL_DEMAND';
            this.newConfigDetails.domain = this.entityValue.selectedValue;
        } else {
            this.newConfigDetails.domain = this.domain.selectedValue;
        }
    }

    setNewConfigEntityDetails(entityName: string): void {
        if (!entityName)
            return;

        const entityNameValue = this.dropDownService.getSelectedEntityNameValueFromPlaceholderFromEntityMap(entityName, this.autoAssetService.EntityNameMapForConfig);
        let entityValue = '';
        let finalEntityValue = '';
        let domain = this.newConfigDetails.selected_domain || this.newConfigDetails.domain;
        if (this.isEntityNameIsMulti(entityName)) {
            const entityNameSplit = entityNameValue.split('$$');
            const id = this.parentsIds[entityNameSplit[1]] !== undefined ? this.parentsIds[entityNameSplit[1]] : this.newConfigDetails.entity_value?.label;
            entityValue = id;
            finalEntityValue = domain + '$$' + entityValue;
        } else {
            if (entityNameValue == 'DOMAIN$$ALL_DEMAND') {
                entityValue = 'ALL_DEMAND';
                finalEntityValue = domain + '$$' + entityValue;
            } else {
                entityValue = this.parentsIds[entityNameValue];
                finalEntityValue = entityValue;
            }
        }
        this.newConfigDetails.entity_name_value = entityNameValue;
        this.newConfigDetails.entity_value = entityValue;
        this.newConfigDetails.final_entity_value = finalEntityValue;
    }

    addToUpsertedConfigs(item): void {
        const addedConfig = {
            entity_name: item.entity_name_value,
            entity_value: item.final_entity_value,
            property: item.property,
            site_name: `AUTO_ASSET_CONFIG$${item.final_entity_value}`,
            value: item.value,
            is_active: item.is_active ? 1 : 0,
        };
        this.upsertData(addedConfig);
    }

    upsertData(addedConfig) {
        this.showPreviewInfoMessage()
        let propertyLevel = this.configData.find(item => item.property === addedConfig.property);

        if (!propertyLevel) {
            propertyLevel = {
                property: addedConfig.property,
                isCollapsed: true,
                properties: []
            };
            this.configData.push(propertyLevel);
        }

        const existingConfig = propertyLevel.properties.find(item => item.entity_name === addedConfig.entity_name && item.entity_value === addedConfig.entity_value);
        const existsInUpsertedConfigs = Array.from(this.upsertedConfigs).some(config =>
            config.property === addedConfig.property && config.entity_name === addedConfig.entity_name && config.entity_value === addedConfig.entity_value
        );

        if (existingConfig) {
            if (this.isDataValueChanged(addedConfig)) {
                existingConfig.value = addedConfig.value;
                existingConfig.is_active = addedConfig.is_active;
                existingConfig.type = existingConfig.type === 'ADDED' ? 'ADDED' : 'UPDATED';
                this.upsertedConfigs.add(existingConfig);
            } else if (!this.isDataValueChanged(addedConfig)) {
                if (existsInUpsertedConfigs) {
                    this.removeItemFromUpsertedConfigs(addedConfig);
                } else {
                    this.toastingService.warning('Configuration Exists', 'Config is already present');
                }
            }
        } else if (!this.utilService.isSet(existingConfig)) {
            addedConfig['type'] = 'ADDED';
            propertyLevel.properties.push(addedConfig);
            this.upsertedConfigs.add(addedConfig);
        }
    }

    isDataValueChanged(config) {
        const originalConfig = this.getOriginalConfig(config);
        if (originalConfig) {
            config.is_active = config.is_active ? 1 : 0;
            if (originalConfig.value !== config.value || originalConfig.is_active !== config.is_active) {
                return true;
            }
        } else {
            return true;
        }
        return false;
    }

    getOriginalConfig(config) {
        const properties = this.originalConfigDataJson?.[config['property']]?.properties;

        if (!Array.isArray(properties)) {
            return undefined;
        }

        return properties.find(item => item.entity_name === config.entity_name && item.entity_value === config.entity_value);
    }

    removeItemFromUpsertedConfigs(item) {
        const toRemoveItem = Array.from(this.upsertedConfigs).find(config =>
            config.entity_name === item.entity_name && config.entity_value === item.entity_value
        );

        if (toRemoveItem) {
            this.upsertedConfigs.delete(toRemoveItem);
            if (item.type === 'ADDED') {
                this.removeConfig(item);
            } else {
                this.resetConfig(item);
            }
        }
    }

    removeConfig(config) {
        let propertyLevel = this.configData.find(item => item.property === config.property);
        propertyLevel.properties.splice(propertyLevel.properties.indexOf(item => item.entity_name === config.entity_name && item.entity_value === config.entity_value), 1);
    }

    resetConfig(config) {
        const originalConfig = this.getOriginalConfig(config);
        const toResetConfig = this.configData.find(item => item.property === config.property).properties.find(item => item.entity_name === config.entity_name && item.entity_value === config.entity_value);
        toResetConfig.value = originalConfig.value;
        toResetConfig.is_active = originalConfig.is_active;
        toResetConfig.type = null;
    }

    updateConfig() {
        this.resetData();
        this.waitingMessage = 'Updating';
        this.showLoader = true;
        this.autoAssetService.updateAutoAssetConfig(Array.from(this.upsertedConfigs))
            .then(() => {
                this.toastingService.success('Config Updated', 'Configs are successfully updated')
            })
            .catch((error) => {
                this.toastingService.error('Updating Failed', 'Configs update failed')
            })
            .finally(() => {
                this.waitingMessage = '';
                this.showLoader = false;
                this.fetchConfig();
            })
    }

    isFetchConfigEnabled()
        :
        boolean {
        const entityNameNotEmpty = this.utilService.isSet(this.dropDownService.getSelectedEntityName(this.entityName));
        const entityValueNotEmpty = this.utilService.isSet(this.dropDownService.getEntityIdsFromSelectedEntityValues(this.entityValue));
        const entityValueDisabled = this.isEntityValueDisabled();
        return entityNameNotEmpty && (entityValueDisabled || entityValueNotEmpty);
    }

    showDomainInput() {
        return this.isEntityNameIsMulti(this.newConfigDetails.entity_name);
    }

    isGlobal() {
        return this.newConfigDetails.selected_entity_name === 'GLOBAL';
    }

    isAllDemand() {
        return this.newConfigDetails.selected_entity_name === 'DOMAIN$$ALL_DEMAND';
    }

    isDomain() {
        return this.newConfigDetails.selected_entity_name.includes('DOMAIN');
    }

    isGlobalOrAllDemand() {
        return this.isGlobal() || this.isAllDemand();
    }

    isNewConfigsPropertyNameDisabled(): boolean {
        return this.isGlobal();
    }

    isNewConfigsEntityNameDisabled(): boolean {
        return this.isGlobalOrAllDemand();
    }

    isNewConfigsDomainDisabled(): boolean {
        return this.isGlobalOrAllDemand() || this.isDomain();
    }

    isNewConfigsPropertyValueDisabled(): boolean {
        return this.isGlobal();
    }

    configRowIsActiveDisabled(row) {
        return row.entity_name === 'GLOBAL';
    }

    setFinalEntityValue() {
        this.newConfigDetails.final_entity_value = this.newConfigDetails.entity_value?.label;
    }

    isUpsertConfigButtonEnabled()
        :
        boolean {
        const entityNameNotEmpty = this.utilService.isSet(this.newConfigDetails.entity_name);
        const domainIsRequiredAndNotEmpty = !this.isEntityNameIsMulti(this.newConfigDetails.entity_name) || this.utilService.isSet(this.newConfigDetails.domain);
        const propertyNotEmpty = this.utilService.isSet(this.newConfigDetails.property);
        const valueNotEmpty = this.utilService.isSet(this.newConfigDetails.value);
        return entityNameNotEmpty && domainIsRequiredAndNotEmpty && propertyNotEmpty && valueNotEmpty;
    }

    resetData() {
        this.configData = [];
        this.initializeUpsertedPropertyDetails();
    }

    resetUpsertedConfigs() {
        this.upsertedConfigs = new Set();
    }

    isPropertyVisible(property) {
        if (this.activeConfigDialog === this.constant.ASSET_TYPE_IMAGE) {
            return this.constant.IMAGE_CONFIGS.includes(property);
        } else if (this.activeConfigDialog === this.constant.ASSET_TYPE_TITLE) {
            return this.constant.TITLE_CONFIGS.includes(property);
        } else if (this.activeConfigDialog === this.constant.ASSET_TYPE_C2A) {
            return this.constant.C2A_CONFIGS.includes(property);
        }
    }

    getConfigPropertyOptions() {
        if (this.activeConfigDialog === this.constant.ASSET_TYPE_IMAGE) {
            return this.constant.IMAGE_CONFIGS;
        } else if (this.activeConfigDialog === this.constant.ASSET_TYPE_TITLE) {
            return this.constant.TITLE_CONFIGS;
        } else if (this.activeConfigDialog === this.constant.ASSET_TYPE_C2A) {
            return this.constant.C2A_CONFIGS;
        }
    }

    getUpsertedConfig() {
        return Array.from(this.upsertedConfigs);
    }

    getAutoAssetConfigPercentage(property) {
        const config = this.configData.find(config => config?.property === property);
        return config ? config?.properties.at(-1)?.value + ' %' : '- %';
    }

    isEntityValueDisabled() {
        return this.entityName.selectedValue === 'Global';
    }

    isValidEntityForImage() {
        const isEntityNameValid = ['Demand Basis', 'Ad ID', 'Ad Group ID', 'Campaign ID', 'Advertiser ID', 'Keyword Category', 'Keyword Cluster', 'Demand Basis IAD Size', 'Ad Group ID IAD Size', 'Campaign ID IAD Size', 'Advertiser ID IAD Size'].includes(this.entityName.selectedValue);
        const isEntityValueValid = this.entityValue.selectedValue.length > 0;
        return isEntityNameValid && isEntityValueValid;
    }

    isValidEntityForTitle() {
        const isEntityNameValid = ['Demand Basis', 'Ad ID', 'Ad Group ID', 'Campaign ID', 'Ad ID Demand Basis', 'Ad Group ID Demand Basis', 'Campaign ID Demand Basis'].includes(this.entityName.selectedValue);
        let isEntityValueValid = this.entityValue.selectedValue.length > 0;
        if (this.entityName.selectedValue !== 'Demand Basis' && this.entityName.selectedValue.includes('Demand Basis')) {
            isEntityValueValid = this.demandBasis.selectedValue != null && this.demandBasis.selectedValue?.length > 0 && isEntityNameValid;
        }
        return isEntityNameValid && isEntityValueValid;
    }

    isValidEntityForC2a() {
        const isEntityNameValid = ['Domain', 'Domain + Campaign ID', 'Domain + Advertiser ID', 'Ad Group ID', 'Campaign ID', 'Advertiser ID'].includes(this.entityName.selectedValue);
        let isEntityValueValid = this.entityValue.selectedValue.length > 0;
        if (this.isEntityValueMulti()) {
            isEntityValueValid = this.domain.selectedValue != null && this.domain.selectedValue?.length > 0 && isEntityValueValid;
        }
        return isEntityNameValid && isEntityValueValid;
    }

    isValidEntityForConfig() {
        const isEntityNameValid = ['Global', 'Ad ID', 'Ad Group ID', 'Campaign ID', 'Advertiser ID', 'Domain + All Demand', 'Domain + Ad ID', 'Domain + Ad Group ID', 'Domain + Campaign ID', 'Domain + Advertiser ID'].includes(this.entityName.selectedValue);
        const isEntityValueValid = this.entityName.selectedValue === 'Global' || this.entityValue.selectedValue.length > 0;
        return isEntityNameValid && isEntityValueValid;
    }

    isEntityValueMulti() {
        return this.entityName.selectedValue.includes('+');
    }

    resetAssetData() {
        this.imageAssetData = [];
        this.titleAssetData = [];
        this.c2aAssetData = [];
        this.configData = [];
    }

    resetSideBarData() {
        this.keyword.selectedValue = [];
        this.demandKeyword.selectedValue = [];
        this.publisherPageTitle.selectedValue = null;
        this.publisherUrl.selectedValue = null;
        this.kbbImageSize.selectedValue = null;
    }

}






