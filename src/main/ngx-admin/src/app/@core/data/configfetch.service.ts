import {Injectable} from '@angular/core';
import {Http} from '@angular/http';
import {BaseUrlService} from './base-url.service';
import 'rxjs/add/operator/toPromise';
import {ToastingService} from '../utils/toaster.service';
import {CookieService} from './cookie.service';

@Injectable({
  providedIn: 'root',
})
export class ConfigFetchService {

  private static toastService: ToastingService;

  static setToastingService(toastingService: ToastingService) {
    this.toastService = toastingService;
  }

  constructor(private http: Http,
    private baseUrlService: BaseUrlService,
    private toastService: ToastingService,
    private cookieService: CookieService) {

  }

  getValidSizes(): Promise<any> {
    const Url = this.baseUrlService.getBaseUrl() + '/config/global_config?property=VALID_SIZES';
    return this.http.get(Url)
      .toPromise()
      .then(response => response.json())
      .catch(err => ConfigFetchService.handleError(err));
  }

  putEntityConfigs(entity: string, level: string, update: any): Promise<any> {
    const url = this.baseUrlService.getBaseUrl() + '/config/generator/' + level.toUpperCase() + '/' + entity;
    return this.http.post(url, update).toPromise().then(response => response.json())
      .catch(err => ConfigFetchService.handleError(err));
  }
  getEntityConfigs(entity: string, level: string): Promise<any> {
    const url = this.baseUrlService.getBaseUrl() + '/config/generator/' + level.toUpperCase() + '/' + entity + '?buSelected=' + this.cookieService.getBUSelectedFromCookie();
    return this.http.get(url).toPromise().then(response => response.json())
      .catch(err => ConfigFetchService.handleError(err));
  }

  static handleError(error: any): Promise<any> {
    ConfigFetchService.toastService.error('Error', '' + (error.message || error));
    return Promise.reject(error.message || error);
  }
}
