import {Injectable} from '@angular/core';
import {environment} from '../../../environments/environment';
import {CookieService} from "./cookie.service";

export class BaseUrl {
    static get(): any {
        if (environment.production) {
            return `/ATAdmin/api`;
        }
        return `http://localhost:8082/ATAdmin/api`;
    }
}

@Injectable({providedIn: 'root'})
export class BaseUrlService {
    /*
    This Service is meant to be used in case the UI and the APIs are hosted on different Servers
    set to empty string if not using
     */
    constructor(private cookieService: CookieService) {
    }

    getBaseUrl() {
        if (environment.production) {
            return `/ATAdmin/api`;
        }
        return `http://localhost:8082/ATAdmin/api`;
    }

    getSearchUrl() {
        return this.getBaseUrl() + '/entity/search/:keyword';
    }

    getCustomerSearchUrl() {
        return this.getBaseUrl() + '/search/customer/:keyword';
    }

    getSearchUrlForDimension(dim: string): string {
        return this.getBaseUrl() + '/search/' + dim.toLowerCase() + '/:keyword?buSelected=' +
            this.cookieService.getBUSelectedFromCookie();
    }

    getPartnerSearchUrl() {
        return this.getBaseUrl() + '/search/partner/:keyword';
    }

    getSessionUrl() {
        return this.getBaseUrl() + '/session';
    }

    getDomainSearchUrl() {
        return this.getBaseUrl() + '/search/domain/:keyword?buSelected=' + this.cookieService.getBUSelectedFromCookie();
    }

    getAdtagSearchUrl() {
        return this.getBaseUrl() + '/search/adtag/:keyword?buSelected=' + this.cookieService.getBUSelectedFromCookie();
    }

    getSizeSeachUrl() {
        return this.getBaseUrl() + '/search/size/:keyword';
    }
}




