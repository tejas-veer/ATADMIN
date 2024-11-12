import { Injectable } from '@angular/core';
import { BaseUrl } from '../base-url.service';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { AutoAssetService } from './auto-asset.service';
import { ToastingService } from '../../utils/toaster.service';
import { AutoAssetConstantsService } from '../auto-asset-constants.service';
import { CookieService } from '../cookie.service';

@Injectable({
    providedIn: 'root',
})
export class ImageAssetService {
    ImageSourceMap = {
        'MANUAL IMAGE - [1]': { setId: 1 },
        'TABOOLA IMAGE - [2]': { setId: 2 },
        'KBB IMAGE - [3]': { setId: 3 },
        'DEMAND BASIS ANIMATION - [4]': { setId: 4 },
        'IMAGE ANIMATION - [5]': { setId: 5 },
        'KEN BURNS ANIMATION - [6]': { setId: 6 },
        'STABLE DIFFUSION GPT - [9]': { setId: 9 },
        'KEN_BURNS_SD_GPT - [10]': { setId: 10 },
        'A360 - [11]' : { setId: 11 },
    };

    SetIdToImageSourceMap = {
        1: 'MANUAL IMAGE - [1]',
        2: 'TABOOLA IMAGE - [2]',
        3: 'KBB IMAGE - [3]',
        4: 'DEMAND BASIS ANIMATION - [4]',
        5: 'IMAGE ANIMATION - [5]',
        6: 'KEN BURNS ANIMATION - [6]',
        9: 'STABLE DIFFUSION GPT - [9]',
        10: 'KEN_BURNS_SD_GPT - [10]',
        11: 'A360 - [11]',
    };

    EntityNameMapForImage = {
        'Demand Basis': {
            value: 'DEMAND_BASIS',
            dropDownValue: 'Demand basis',
            placeholder: 'Demand Basis',
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
        'Demand Basis IAD Size': {
            value: 'DEMAND_BASIS_IAD_SIZE',
            dropDownValue: 'Demand basis',
            placeholder: 'Demand Basis',
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
    };

    constructor(private autoAssetService: AutoAssetService, private http: HttpClient,
                private toastingService: ToastingService,
                private configConstantService: AutoAssetConstantsService,
                private cookieService: CookieService,
    ) {
    }

    isSizeSelectVisible(entityName) {
        const sizeSelectValues = ['DEMAND_BASIS_IAD_SIZE', 'AD_GROUP_ID_IAD_SIZE', 'CAMPAIGN_ID_IAD_SIZE', 'ADVERTISER_ID_IAD_SIZE'];
        return sizeSelectValues.includes(entityName);
    }

    async getKBBImagesToMap(keywords: any[], size: string): Promise<any> {
        const url = BaseUrl.get() + '/asset/query/getKBBImage';
        const params = new HttpParams()
            .set('keyword', keywords.join('|'))
            .set('size', size);

        return await this.http.post(url, {}, {params}).toPromise()
            .then((response) => {
                const error = this.autoAssetService.handleInvalidOrEmptyResponse(response);
                if (error) throw error;
                return response;
            })
            .catch((error) => {
                if (!this.autoAssetService.showToast(error, 'Keyword')) this.toastingService.error('Failed to Fetch', 'Unable to fetch asset');
                throw error;
            });
    }

    async getStableDiffusionImagesFromRequestId(requestId) {
        const url = BaseUrl.get() + '/asset/query/getStableDiffusionImagesFromRequestId';
        const params = new HttpParams().set('request_id', requestId);

        return await this.http.post(url, {}, {params}).toPromise()
            .then((response) => {
                const error = this.autoAssetService.handleInvalidOrEmptyResponse(response);
                if (error) throw error;
                return response;
            })
            .catch((error) => {
                if (!this.autoAssetService.showToast(error, `Request ID`)) this.toastingService.error('Failed to Fetch', 'Unable to fetch asset');
                throw error;
            });
    }

    async fetchStableDiffusionImagesWithImageFetchId(imageFetchId) {
        const url = BaseUrl.get() + '/asset/query/getStableDiffusionImagesByImageFetchId';
        const params = new HttpParams().set('image_fetch_id', imageFetchId);

        return await this.http.post(url, {}, {params}).toPromise()
            .then((response) => {
                return response;
            })
            .catch((error) => {
                throw error;
            });
    }

    async mapStableDiffusionImage(payload) {
        const url = BaseUrl.get() + '/asset/query/mapStableDiffusionImage';

        const httpOptions = {
            headers: new HttpHeaders({
                'content-type': 'application/json',
            }),
        };

        return await this.http.post(url, payload, httpOptions).toPromise()
            .then(response => {
                this.toastingService.success(`Image Mapped`,
                    `${payload?.length} Image mapped successfully`);
                return response;
            })
            .catch(error => {
                this.toastingService.error('Failed to Map', 'Unable to map images');
                throw error;
            });
    }

    async mapHostedImage(payload) {
        const url = BaseUrl.get() + '/asset/query/mapHostedImage';
        const httpOptions = {
            headers: new HttpHeaders({
                'content-type': 'application/json',
            }),
        };

        return await this.http.post(url, payload, httpOptions).toPromise()
            .then(response => {
                this.toastingService.success(`Image Mapped`,
                    `Image mapped successfully`);
                return response;
            })
            .catch(error => {
                this.toastingService.error('Failed to Map', 'Mapping failed or Duplicate Entries');
                throw error;
            });
    }

    async mapBulkHostedImages(payload) {
        const url = BaseUrl.get() + '/asset/query/mapBulkHostedImages';
        const httpOptions = {
            headers: new HttpHeaders({
                'content-type': 'application/json',
            }),
        };

        return await this.http.post(url, payload, httpOptions).toPromise()
            .then(response => {
                return response;
            })
            .catch(error => {
                this.toastingService.error('Failed to Map', 'Mapping failed or Duplicate Entries');
                throw error;
            });
    }

    mapLocalImage(payload: any) {
        const url = this.cookieService.getMapLocalImageEndPoint();
        const formData = new FormData();
        formData.append('entity_name', payload['entity_name'].toUpperCase());
        formData.append('entity_value', payload['entity_value']);
        formData.append('asset_size', payload['asset_size']);
        formData.append('set_id', payload['set_id']);

        for (const file of payload['asset_list']) {
            formData.append('asset_list', file.value);
        }

        const headers = new HttpHeaders();
        headers.set('Content-Type', 'multipart/form-data');

        return this.http.post(url, formData, {headers: headers}).toPromise()
            .then(response => {
                this.toastingService.success(`Image Mapped`,
                    `Image mapped successfully`);
                return response;
            })
            .catch(error => {
                this.toastingService.error('Failed to Map', 'Unable to map images');
                throw error;
            });
    }
}
