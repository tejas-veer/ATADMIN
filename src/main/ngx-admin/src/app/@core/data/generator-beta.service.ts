import { Injectable } from '@angular/core';
import { Headers, Http } from '@angular/http';
import { BaseUrlService } from './base-url.service';
import 'rxjs/add/operator/toPromise';
import { ToastingService } from '../utils/toaster.service';
import { HttpClient } from "@angular/common/http";
import { GeneratorService } from "./generator.service";
@Injectable({
  providedIn: 'root'
})
export class GeneratorBetaService {

  private num: number;

  constructor(private http: Http,
    private httpClient: HttpClient,
    private baseUrlService: BaseUrlService,
    private toastService: ToastingService) {
    this.num = 0;
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

  getCustomization(entity) {
    let url = this.baseUrlService.getBaseUrl() + '/entity/' + entity + '/customization';
    return this.http.get(url).toPromise()
      .then(response => {
        console.log("Customizations", response.text());
        console.log(++this.num);
        return response.json().entityCustomization;
      })
      .catch(err => {
        this.handleError(err)
      });
  }


  createRequest(entity: string, useproxy: boolean, nocache: boolean) {
    let url = this.baseUrlService.getBaseUrl() + '/generator/v2/generate/entity/' + entity + '?';

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


  poll(requestId: string) {
    let requestUrl = this.baseUrlService.getBaseUrl() + '/generator/v2/poll/' + requestId;
    return this.http.get(requestUrl).toPromise()
      .then(response => response.json())
      .catch(err => {
        return this.handleError(err)
      });
  }

  insert(entity: string, payload: string) {
    let Url = this.baseUrlService.getBaseUrl() + '/generator/v2/entity/' + entity + '/insert';
    let header = new Headers({ 'Content-Type': 'text/plain' });
    return this.http.post(Url, payload, { headers: header }).toPromise().then(response => response).catch(err => this.handleError(err))
  }

  fetchMockResponse(entity: string) {
    let requestUrl = this.baseUrlService.getBaseUrl() + "/generator/mockPoll/" + entity;
    return this.http.get(requestUrl).toPromise()
      .then(response => response.json())
      .catch(err => {
        return this.handleError(err);
      });
  }



  private handleError(error: any): Promise<any> {
    GeneratorService.toastService.error('Error', '' + error);
    return Promise.reject(error.message || error);
  }
}
