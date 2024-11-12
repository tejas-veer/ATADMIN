import { Injectable } from '@angular/core';
import { Headers, Http } from '@angular/http';
import { BaseUrlService } from './base-url.service';
import 'rxjs/add/operator/toPromise';
import { ToastingService } from '../utils/toaster.service';
import { HttpClient, HttpHeaders } from "@angular/common/http";
@Injectable({
  providedIn: 'root'
})
export class GeneratorService {

  static toastService: ToastingService;
  private num: number;

  static setToastingService(toastingService: ToastingService) {
    this.toastService = toastingService;
  }

  constructor(private http: Http,
    private httpClient: HttpClient,
    private baseUrlService: BaseUrlService,
    private toastService: ToastingService) {
    this.num = 0;
  }

  getCustomization(entity) {
    let url = this.baseUrlService.getBaseUrl() + '/entity/' + entity + '/customization';
    return this.http.get(url).toPromise()
      .then(response => {
        this.toastService.showToast('success', 'Fetched Customisation', "");
        console.log("Customizations", response.text());
        console.log(++this.num);
        return response.json().entityCustomization;
      })
      .catch(err => {
        this.handleError(err)
      });
  }

  queueGen(entity: string, header: string, attribution: string) {
    let url = this.baseUrlService.getBaseUrl() + '/generator/entity/' + entity + '?';
    if (header) {
      url += '&header-text=' + header.trim();
    }
    if (attribution) {
      url += '&ad-attr-link=' + attribution.trim();
    }

    url = encodeURI(url);
    return this.http.get(url).toPromise()
      .then(response => {
        this.toastService.success('Queued Successsfully', response.text());
      })
      .catch(err => {
        this.handleError(err)
      });
  }

  addCustomization(entity: string, header: string, attribution: string): Promise<any> {
    let url = this.baseUrlService.getBaseUrl() + '/entity/' + entity + '/customization/add?';
    if (header) {
      url += '&header-text=' + encodeURIComponent(header);
    }

    if (attribution) {
      url += '&ad-attr-link=' + attribution;
    }

    if (!(header || attribution)) {
      return Promise.resolve('');
    }
    url = encodeURI(url);
    return this.http.get(url).toPromise()
      .then(response => {
        this.toastService.success('Customization Saved Successfully', response.text());
      })
      .catch(err => {
        this.handleError(err)
      });
  }

  createRequest(entity: string, useproxy: string, nocache: boolean) {
    let url = this.baseUrlService.getBaseUrl() + '/generator/entity/' + entity + '/new?';

    if (useproxy) {
      url += '&useproxy=' + useproxy;
    }

    if (nocache) {
      url += '&nocache=' + nocache;
    }

    return this.http.get(url).toPromise()
      .then(response => response.json())
      .catch(err => this.handleError(err));
  }

  insert(entity: string, pc: string) {
    let Url = this.baseUrlService.getBaseUrl() + '/generator/entity/' + entity + '/insert';
    let header = new Headers({ 'Content-Type': 'text/plain' });
    return this.http.post(Url, pc, { headers: header }).toPromise().then(response => response).catch(err => this.handleError(err))
  }

  poll(requestId: string) {
    let requestUrl = this.baseUrlService.getBaseUrl() + '/generator/poll/' + requestId;
    return this.http.get(requestUrl).toPromise()
      .then(response => response.json())
      .catch(err => {
        return this.handleError(err)
      });
  }

  getZeroColorFrameworks() {
    let requestUrl = this.baseUrlService.getBaseUrl() + '/generator/zeroColor/frameworks';
    return this.http.get(requestUrl).toPromise()
      .then(response => response.json())
      .catch(err => {
        return this.handleError(err)
      });
  }

  getZeroColor(entity: string, level: string, frameworks: Array<string>, sizes: Array<string>) {
    let Url = this.baseUrlService.getBaseUrl() + '/generator/zeroColor/' + level + '/' + entity + '/templates';
    return this.http.post(Url, {
      frameworks: frameworks,
      sizes: sizes
    }).toPromise().then(response => response.json()).catch(err => this.handleError(err))
  }


  insertZeroColor(entity: string, level: string, contents) {
    let Url = this.baseUrlService.getBaseUrl() + '/generator/zeroColor/' + level + '/' + entity + '/insertTemplates';
    return this.http.post(Url, contents).toPromise().then(response => response.json()).catch(err => this.handleError(err))
  }

  postIssue(contents) {
    let x = new HttpHeaders();
    x.set('Content-Type', 'text/plain; charset=utf-8');
    let Url = this.baseUrlService.getBaseUrl() + '/issue';
    contents = "Generator Service:" + contents;
    return this.httpClient.post(Url, contents, { headers: x }).toPromise().then(response => response).catch(err => this.handleError(err));
  }

  private handleError(error: any): Promise<any> {
    GeneratorService.toastService.error('Error', '' + error);
    return Promise.reject(error.message || error);
  }
}
