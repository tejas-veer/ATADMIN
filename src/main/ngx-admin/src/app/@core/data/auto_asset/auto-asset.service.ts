import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { BaseUrl } from '../base-url.service';
import { ReportingCsvService } from '../reporting-csv.service';
import { CookieService } from '../cookie.service';
import { ToastingService } from "../../utils/toaster.service";
import { catchError } from 'rxjs/operators';
import { Observable, throwError } from 'rxjs';
import { AutoAssetConstantsService } from "../auto-asset-constants.service";
import { UtilService } from '../../utils/util.service';

@Injectable({ providedIn: 'root' })

export class AutoAssetService {

    DATA_NOT_EXIST = "No Data Exist"
    API_QUERY_FAIL = "API Query Failed"
    DEMAND_BASIS = "Demand basis"
    MAX_MAP_LIMIT = 5;
    MAX_LOCAL_FILE_LIMIT = 5;
    autoAssetConfigPercentages = [0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80, 85, 90, 95, 100];

    configAutoAssetTestProperties = [
        "AUTO_ASSET_TEST_PERCENTAGE",
        "IMAGE_TEST_PERCENTAGE",
        "TITLE_TEST_PERCENTAGE",
        "C2A_TEST_PERCENTAGE",
    ]

    configOnlyAssetProperties = [
        "ONLY_AA_C2A",
        "ONLY_AA_IMAGE",
        "ONLY_AA_TITLE"
    ]

    autoAssetConfigProperties = [
        ...this.configAutoAssetTestProperties,
        ...this.configOnlyAssetProperties
    ]

    EntityNameMapForAutoAsset = {
        'Provider Topic': {
            value: 'DEMAND_BASIS',
            dropDownValue: 'Demand basis',
            placeholder: 'Provider Topic',
        },
        'Ad ID': {
            value: 'AD_ID',
            dropDownValue: 'Ad Id',
            placeholder: 'Ad ID',
        },
        'Ad Group ID': {
            value: 'AD_GROUP_ID',
            dropDownValue: 'AdGroup',
            placeholder: 'Ad Group ID',
        },
        'Campaign ID': {
            value: 'CAMPAIGN_ID',
            dropDownValue: 'Campaign',
            placeholder: 'Campaign ID',
        },
        'Advertiser ID': {
            value: 'ADVERTISER_ID',
            dropDownValue: 'Bdata - Advertiser',
            placeholder: 'Advertiser ID',
        },
        'Domain': {
            value: 'DOMAIN',
            dropDownValue: 'Publisher Domain',
            placeholder: 'Domain',
        },
        'Domain + Ad ID': {
            value: 'DOMAIN$$AD_ID',
            dropDownValue: 'Ad Id',
            placeholder: 'Ad ID',
            isMulti: true,
        },
        'Domain + Ad Group ID': {
            value: 'DOMAIN$$ADGROUP_ID',
            dropDownValue: 'AdGroup',
            placeholder: 'Ad Group ID',
            isMulti: true,
        },
        'Domain + Campaign ID': {
            value: 'DOMAIN$$CAMPAIGN_ID',
            dropDownValue: 'Campaign',
            placeholder: 'Campaign ID',
            isMulti: true,
        },
        'Domain + Advertiser ID': {
            value: 'DOMAIN$$ADVERTISER_ID',
            dropDownValue: 'Bdata - Advertiser',
            placeholder: 'Advertiser ID',
            isMulti: true,
        },
        'Domain + All Demand': {
            value: 'DOMAIN$$ALL_DEMAND',
            dropDownValue: 'Publisher Domain',
            placeholder: 'Domain',
            isMulti: false,
        },
        'Ad ID Provider Topic': {
            value: 'AD_ID$$DEMAND',
            dropDownValue: 'Ad Id',
            placeholder: 'Ad ID',
        },
        'Ad Group ID Provider Topic': {
            value: 'AD_GROUP_ID$$DEMAND',
            dropDownValue: 'AdGroup',
            placeholder: 'Ad Group ID',
        },
        'Campaign ID Provider Topic': {
            value: 'CAMPAIGN_ID$$DEMAND',
            dropDownValue: 'Campaign',
            placeholder: 'Campaign ID',
        },
        'Keyword Category': {
            value: 'KEYWORD_CATEGORY_BASE',
            dropDownValue: 'Sprig Keyword Category',
            placeholder: 'Keyword Category',
        },
        'Keyword Cluster': {
            value: 'KWD_CLUSTER',
            dropDownValue: 'Serp Cluster ID',
            placeholder: 'Keyword Cluster',
        },
        'Provider Topic IAD Size': {
            value: 'DEMAND_BASIS_IAD_SIZE',
            dropDownValue: 'Demand basis',
            placeholder: 'Provider Topic',
        },
        'Ad Group ID IAD Size': {
            value: 'AD_GROUP_ID_IAD_SIZE',
            dropDownValue: 'AdGroup',
            placeholder: 'Ad Group',
        },
        'Campaign ID IAD Size': {
            value: 'CAMPAIGN_ID_IAD_SIZE',
            dropDownValue: 'Campaign',
            placeholder: 'Campaign',
        },
        'Advertiser ID IAD Size': {
            value: 'ADVERTISER_ID_IAD_SIZE',
            dropDownValue: 'Bdata - Advertiser',
            placeholder: 'Advertiser',
        },
        'Global': {
            value: 'GLOBAL',
            dropDownValue: 'Global',
            placeholder: 'Global',
        },
    };

    EntityNameMapForConfig = {
        'Domain + Ad ID': {
            value: 'DOMAIN$$AD_ID',
            dropDownValue: 'Ad Id',
            placeholder: 'Ad ID',
            isMulti: true,
        },
        'Domain + Ad Group ID': {
            value: 'DOMAIN$$ADGROUP_ID',
            dropDownValue: 'AdGroup',
            placeholder: 'Ad Group ID',
            isMulti: true,
        },
        'Domain + Campaign ID': {
            value: 'DOMAIN$$CAMPAIGN_ID',
            dropDownValue: 'Campaign',
            placeholder: 'Campaign ID',
            isMulti: true,
        },
        'Domain + Advertiser ID': {
            value: 'DOMAIN$$ADVERTISER_ID',
            dropDownValue: 'Bdata - Advertiser',
            placeholder: 'Advertiser ID',
            isMulti: true,
        },
        'Domain + All Demand': {
            value: 'DOMAIN$$ALL_DEMAND',
            dropDownValue: 'Publisher Domain',
            placeholder: 'Domain',
            isMulti: false,
        },
        'Ad ID': {
            value: 'AD_ID',
            dropDownValue: 'Ad Id',
            placeholder: 'Ad ID',
            isMulti: false,
        },
        'Ad Group ID': {
            value: 'ADGROUP_ID',
            dropDownValue: 'AdGroup',
            placeholder: 'Ad Group ID',
            isMulti: false,
        },
        'Campaign ID': {
            value: 'CAMPAIGN_ID',
            dropDownValue: 'Campaign',
            placeholder: 'Campaign ID',
            isMulti: false,
        },
        'Advertiser ID': {
            value: 'ADVERTISER_ID',
            dropDownValue: 'Bdata - Advertiser',
            placeholder: 'Advertiser ID',
            isMulti: false,
        },
        'Global': {
            value: 'GLOBAL',
            dropDownValue: 'Global',
            placeholder: 'Global',
            isMulti: false,
        },
    };

    ConfigEntityNameOrder = Object.values(this.EntityNameMapForConfig).map(config => config.value);

    constructor(private http: HttpClient,
        private reportingCsvService: ReportingCsvService,
        private cookieService: CookieService,
        private toastingService: ToastingService,
        private configConstantService: AutoAssetConstantsService,
        private utilService: UtilService,
    ) {
    }

    handleInvalidOrEmptyResponse(response: any): Error {
        if (response === null || response === 'null' || response === undefined) {
            return new Error(this.API_QUERY_FAIL);
        } else if (Array.isArray(response) && response.length === 0) {
            return new Error(this.DATA_NOT_EXIST);
        } else if (typeof response === 'object' && Object.keys(response).length === 0) {
            return new Error(this.DATA_NOT_EXIST);
        }
        return null
    }

    showToast(error, dimension) {
        if (error.message === this.DATA_NOT_EXIST) {
            if (dimension != "") {
                this.toastingService.warning('No data exists', `Please enter valid '${dimension}'`);
            } else {
                this.toastingService.warning('No data exists', 'Nothing to show');
            }
            return true;
        } else if (error.message === this.API_QUERY_FAIL) {
            this.toastingService.error('API Query Failed', 'Failed to fetch data from the API. Please try again.');
            return true;
        }
        return false;
    }

    async getMappedAssetListOnKey(entityName, entityValueJoinedString, assetType: string): Promise<any> {
        const url = BaseUrl.get() + '/asset/query/getMappedAssetListOnKey';
        const params = new HttpParams()
            .set('entity_name', entityName)
            .set('entity_value', entityValueJoinedString)
            .set('asset_type', assetType);

        return await this.http.post(url, {}, { params })
            .toPromise()
            .then((response) => {
                return this.convertIsActiveToBoolean(response);
            })
            .catch((error) => {
                this.toastingService.error('Failed to Fetch', 'Unable to fetch already mapped asset data');
                throw error;
            });
    }

    async getMappedAssetListOnKeyValue(keyValueJoinedString): Promise<any> {
        const url = BaseUrl.get() + '/asset/query/getMappedAssetListOnKeyValue';
        const params = new HttpParams().set('key_values', keyValueJoinedString)

        return await this.http.post(url, {}, { params })
            .toPromise()
            .then((response) => {
                return this.convertIsActiveToBoolean(response);
            })
            .catch((error) => {
                this.toastingService.error('Failed to Fetch', 'Unable to fetch already mapped asset data');
                throw error;
            });
    }

    getTopDemandBasis() {
        const payload = {
            'filters': {
                'Campaign Type': [
                    {
                        'state': 'Equal',
                        'values': ['native'],
                    },
                ],
                'Bidder': [
                    {
                        'state': 'Equal',
                        'values': ['Perform Native DSP - [229]'],
                    },
                ],
            },
            'metrics': 'Valid Impressions',
            'dimensions': ['Demand basis'],
        };

        const payloadJson = JSON.stringify(payload);
        const demandBasisList = this.reportingCsvService.getTopDemandBasis(payloadJson);
        return demandBasisList;
    }

    getAARequest(limit, offset, requestTypes): Observable<any> {
        const url = BaseUrl.get() + '/asset/query/getAARequest';
        const params = new HttpParams()
            .set('limit', limit)
            .set('offset', offset)
            .set('request_types', requestTypes)


        return this.http.post(url, {}, { params }).pipe(
            catchError(error => {
                console.error('Error occurred in service:', error);
                return throwError(error);
            })
        );
    }

    async generateAssetsFromOzil(payload: Array<any>, assetType): Promise<any> {
        const url = this.getEndpointForGenerationOfAssets(assetType);
        const httpOptions = {
            headers: new HttpHeaders({
                'content-type': 'application/json'
            }),
        };
        return await this.http.post(url, payload, httpOptions).toPromise()
            .then((response) => {
                return response;
            })
            .catch((error) => {
                throw error;
            })
    }

    getEndpointForGenerationOfAssets(assetType: string): string {
        if (assetType === this.configConstantService.ASSET_TYPE_IMAGE) {
            return BaseUrl.get() + '/asset/query/generateStableDiffusionImages';
        } else {
            return BaseUrl.get() + '/asset/query/title/generateTitlesFromOzil';
        }
    }

    async queueAssetGeneration(payload, assetType) {
        const url = this.getEndpointForQueuingOfAssets(assetType);
        const httpOptions = {
            headers: new HttpHeaders({
                'content-type': 'application/json'
            }),
        };
        return await this.http.post(url, payload, httpOptions).toPromise()
            .then((response) => {
                return response;
            })
            .catch((error) => {
                throw error;
            })
    }

    getEndpointForQueuingOfAssets(assetType: string): string {
        if (assetType === this.configConstantService.ASSET_TYPE_IMAGE) {
            return BaseUrl.get() + '/asset/query/queueStableDiffusionImageGeneration';
        } else {
            return BaseUrl.get() + '/asset/query/title/queueTitleGeneration';
        }
    }

    async getGeneratedAssetListByRequestId(requestId) {
        const url = BaseUrl.get() + '/asset/query/title/getGeneratedAssetListByRequestId';
        const params = new HttpParams().set('request_id', requestId)

        return await this.http.post(url, {}, { params }).toPromise()
            .then((response) => {
                const error = this.handleInvalidOrEmptyResponse(response);
                if (error) throw error;
                return response;
            })
            .catch((error) => {
                if (!this.showToast(error, `Request ID`)) this.toastingService.error("Failed to Fetch", "Unable to fetch asset");
                throw error;
            })
    }

    async suspendAARequest(requestId) {
        const url = BaseUrl.get() + '/asset/query/suspendAARequest';
        const params = new HttpParams().set('request_id', requestId)

        return await this.http.post(url, {}, { params }).toPromise()
            .then((response) => {
                return response;
            })
            .catch((error) => {
                throw error;
            })
    }

    async getAutoSelectSuggestions(search_dimension, search_input) {
        const url = BaseUrl.get() + '/search/connectFilters/value';
        const params = new HttpParams()
            .set('search_dimension', search_dimension)
            .set('search_input', search_input)
            .set('buSelected', this.cookieService.getBUSelectedFromCookie());

        return await this.http.post(url, {}, { params }).toPromise()
            .then(response => response)
            .catch(error => {
                throw error;
            });
    }

    async getAssetToReview(selectedAssetType: string, itemsPerPage: number, lastRowUpdationDate: string, cursorId: number,
        selectedEntityName, selectedEntityValue, selectedAssetId,
        selectedSetId, selectedAdminName, selectedAssetValue: string) {
        const url = BaseUrl.get() + '/asset/query/getAssetsForReview';
        const params = this.getParmsForAssetsToReview(selectedAssetType, itemsPerPage, lastRowUpdationDate, cursorId,
            selectedEntityName, selectedEntityValue, selectedAssetId, selectedSetId, selectedAdminName, selectedAssetValue);
        return await this.http.get(url, { params })
            .toPromise()
            .then((response) => {
                const error = this.handleInvalidOrEmptyResponse(response);
                if (error) throw error;
                return response;
            })
            .catch((error) => {
                if (!this.showToast(error, "")) this.toastingService.error("Failed to Fetch", "Unable to fetch asset to review");
                throw error;
            });
    }

    getParmsForAssetsToReview(selectedAssetType: string, itemsPerPage: number, lastRowUpdationDate: string, cursorId: number,
        selectedEntityName, selectedEntityValue, selectedAssetId, selectedSetId, selectedAdminName,
        selectedAssetValue) {
        if (lastRowUpdationDate === undefined)
            lastRowUpdationDate = null;
        if (cursorId === undefined)
            cursorId = 0;
        return new HttpParams()
            .set('asset_type', selectedAssetType)
            .set('page_size', itemsPerPage)
            .set('cursor_updation_date', lastRowUpdationDate)
            .set('cursor_id', cursorId)
            .set('entity_name', selectedEntityName)
            .set('entity_value', selectedEntityValue)
            .set('asset_id', selectedAssetId)
            .set('set_id', selectedSetId)
            .set('admin_name', selectedAdminName)
            .set('asset_value', selectedAssetValue);
    }

    updateReviewAssetDetails(toggledRows): Promise<any> {
        const url = BaseUrl.get() + '/asset/query/reviewAsset';
        return this.updateAssetDetails(toggledRows, url);
    }

    blockUnblockAssets(toggledRows): Promise<any> {
        const url = BaseUrl.get() + '/asset/query/blockUnblockAsset';
        return this.updateAssetDetails(toggledRows, url);
    }

    async updateAssetDetails(toggledRows, url): Promise<any> {
        const httpOptions = {
            headers: new HttpHeaders({
                'content-type': 'application/json'
            }),
        }
        toggledRows = this.convertArrayToNumber(toggledRows);
        return await this.http.post(url, toggledRows, httpOptions).toPromise()
            .then((response) => {
                this.toastingService.success('Update Assets', `Assets were updated successfully.`);
                return response
            })
            .catch((error) => {
                this.toastingService.error("Failed to Update", "Unable to block/unblock asset");
                throw error;
            })
    }

    capitalizeFirstLetterOnly(str: string): string {
        str = str.toLowerCase();
        return str.charAt(0).toUpperCase() + str.slice(1);
    }

    async mapAsset(mappedRow, assetType: string) {
        const url = BaseUrl.get() + '/asset/query/mapAsset';

        const params = new HttpParams()
            .set('asset_type', assetType);
        const httpOptions = {
            headers: new HttpHeaders({
                'content-type': 'application/json',
            }),
            params: params,
        };
        await this.http.post(url, mappedRow, httpOptions).toPromise()
            .then(() => {
                this.toastingService.success(`${this.capitalizeFirstLetterOnly(assetType)} Mapped`,
                    `${mappedRow?.length} ${this.capitalizeFirstLetterOnly(assetType)} mapped successfully`);
            })
            .catch((error) => {
                this.toastingService.error('Failed to Map', `Unable to map ${this.capitalizeFirstLetterOnly(assetType)
                    + 's'}`);
                throw error;
            });
    }

    getAutoAssetConfig(entityName, entityValue) {
        const url = BaseUrl.get() + '/asset/query/config/getConfig';
        const params = new HttpParams().set("entity_name", entityName).set("entity_value", entityValue)
        return this.http.post(url, {}, { params })
            .toPromise()
            .then((response) => {
                return response;
            })
            .catch((error) => {
                throw error;
            });
    }

    updateAutoAssetConfig(updatedData) {
        const url = BaseUrl.get() + '/asset/query/config/updateConfig';
        return this.http.post(url, updatedData)
            .toPromise()
            .then((response) => {
                return response;
            })
            .catch((error) => {
                throw error;
            });
    }

    convertIsActiveToBoolean(response: any): any[] {
        return response.map(item => {
            return { ...item, isActive: item.isActive == 1 };
        });
    }

    convertArrayToNumber(jsonArray: any[]): any[] {
        return jsonArray.map(item => {
            return { ...item, isActive: item.isActive ? 1 : 0 };
        });
    }

    extractIdUsingRegex(inputString: any) {
        if (inputString != null) {
            inputString = String(inputString);
            const regex = /\[(\d+)\]/;
            const matchResult = inputString.match(regex);

            if (matchResult && matchResult.length > 1) {
                return matchResult[1];
            }
        }
        return inputString;
    }

    extractName(inputString: any) {
        return inputString.split('-')[0].trim();
    }

    getUpdatedKeyForBulkUploadData(data, keySet) {
        return data.map(obj => {
            const newObj: any = {};
            Object.keys(obj).forEach(oldKey => {
                const newKey = keySet[oldKey] || oldKey;
                newObj[newKey] = obj[oldKey];
            });
            return newObj;
        });
    }
}
