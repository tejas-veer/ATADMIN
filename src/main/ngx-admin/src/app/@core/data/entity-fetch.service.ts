import {Injectable} from '@angular/core';
import {Http} from '@angular/http';
import {BaseUrlService} from './base-url.service';
import 'rxjs/add/operator/toPromise';
import {ToastingService} from '../utils/toaster.service';
import {UserService} from './users.service';
import {CookieService} from "./cookie.service";


@Injectable()
export class EntityService {
    static toastService: ToastingService;

    static setToastingService(toastingService: ToastingService) {
        this.toastService = toastingService;
    }

    constructor(private http: Http,
                private baseUrlService: BaseUrlService,
                private toastService: ToastingService,
                private cookieService: CookieService) {

    }

    getEntityId(domain: string, adtag: string): Promise<any> {
        const Url = this.baseUrlService.getBaseUrl()
            + '/config/entity?domain=' + domain
            + '&adtag=' + EntityService.processedAdtag(adtag) + '&buSelected=' + this.cookieService.getBUSelectedFromCookie();
        return this.http.get(Url)
            .toPromise()
            .then(response => response.text())
            .catch(err => {
                return EntityService.handleError(err);
            });
    }

    static processedAdtag(adtag: string) {
        adtag = String(adtag);
        const i = adtag.indexOf('['), j = adtag.indexOf(']');
        if (i < 0) {
            return adtag;
        }
        return adtag.substring(i + 1, j);
    }

    getEntity(entity: string): Promise<any> {
        const Url = this.baseUrlService.getBaseUrl() + '/entity/' + entity + '?buSelected=' + this.cookieService.getBUSelectedFromCookie();
        return this.http.get(Url)
            .toPromise()
            .then(response => response.json())
            .catch(err => {
                return EntityService.handleError(err);
            });
    }

    getTopAdtagForEntity(domain: string, start, end): Promise<any> {
        const Url = this.baseUrlService.getBaseUrl()
            + '/search/adtag/?domain=' + domain
            + '&startDate=' + start + '&endDate=' + end + '&buSelected=' + this.cookieService.getBUSelectedFromCookie();
        return this.http.get(Url)
            .toPromise()
            .then(response => response.json())
            .catch(err => {
                return EntityService.handleError(err);
            });
    }

    getTopDomainsForEntity(adtag: string, start, end): Promise<any> {
        const Url =
            this.baseUrlService.getBaseUrl() + '/search/domain/?adtag=' + adtag + '&startDate=' + start + '&endDate=' + end + '&buSelected=' + this.cookieService.getBUSelectedFromCookie();
        return this.http.get(Url)
            .toPromise()
            .then(response => response.json())
            .catch(err => {
                return EntityService.handleError(err);
            });
    }

    addTemplate(entity: string, template: string): Promise<any> {
        const buSelected = this.cookieService.getBUSelectedFromCookie();
        const Url = this.baseUrlService.getBaseUrl() + '/entity/' + entity + '/template/add?template=' + template + "&bu=" + buSelected;
        return this.http.get(Url)
            .toPromise()
            .then(response => response.json())
            .catch(err => {
                return EntityService.handleError(err);
            });
    }

    getATStateData(entity: string, start: string, end: string): Promise<any> {
        const ATStateUrl = this.baseUrlService.getBaseUrl() +
            '/entity/' + entity + '/state?startDate=' + start + '&endDate=' + end + '&buSelected=' + this.cookieService.getBUSelectedFromCookie();
        return this.http.get(ATStateUrl)
            .toPromise()
            .then(response => response.json())
            .catch(err => {
                return EntityService.handleError(err);
            });
    }

    getAllDriuidDataUrl(domain: string, adtag: string, start, end): string {
        return this.baseUrlService.getBaseUrl()
            + '/druid/?startDate=' + start + '&endDate=' + end
            + '&domain=' + domain + '&adtag=' + EntityService.processedAdtag(adtag) + '&buSelected=' + this.cookieService.getBUSelectedFromCookie();
    }

    getDruidData(entity: string, start: string, end: string): Promise<any> {
        let ATDruidUrl = this.baseUrlService.getBaseUrl()
            + '/entity/' + entity + '/druid?startDate=' + start + '&endDate=' + end;
        ATDruidUrl += '&adv=' + UserService.getAdvancedMode() + '&buSelected=' + this.cookieService.getBUSelectedFromCookie();
        return this.http.get(ATDruidUrl)
            .toPromise()
            .then(response => {
                return response.json()
            })
            .catch(err => {
                return EntityService.handleError(err);
            });
    }

    updateGeneratorEntity(entity: string, enabled: Array<any>, disabled: Array<any>) {
        const UpdateUrl = this.baseUrlService.getBaseUrl() + '/entity/v2/' + entity + '/template/update';
        const buSelected = this.cookieService.getBUSelectedFromCookie();
        return this.http.post(UpdateUrl, {enable: enabled, disable: disabled, buSelected:buSelected}).toPromise()
            .then(response => response.json())
            .catch(err => {
                return EntityService.handleError(err);
            });
    }

    updateEntity(entity: string, enabled: Array<any>, disabled: Array<any>) {
        const UpdateUrl = this.baseUrlService.getBaseUrl() + '/entity/' + entity + '/template/update';
        const buSelected = this.cookieService.getBUSelectedFromCookie();
        return this.http.post(UpdateUrl, {enable: enabled, disable: disabled, buSelected:buSelected}).toPromise()
            .then(response => response.json())
            .catch(err => {
                return EntityService.handleError(err);
            });
    }

    getSizeForAdtag(adtag: string) {
        const UpdateUrl = this.baseUrlService.getBaseUrl() + '/config/size/' + adtag;
        return this.http.get(UpdateUrl).toPromise()
            .then(response => response)
            .catch(err => {
                return EntityService.handleError(err);
            });
    }

    static handleError(error: any): Promise<any> {
        EntityService.toastService.error('Error in EntityService', '' + (error.message || error));
        return Promise.reject(error.message || error);
    }
}

