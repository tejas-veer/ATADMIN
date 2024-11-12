import { EntityService } from './entity-fetch.service';
import { Injectable } from '@angular/core';
import { BaseUrlService } from "./base-url.service";
import { Http } from "@angular/http";

@Injectable({
  providedIn: 'root'
})
export class DebugService {

  constructor(private baseUrlService: BaseUrlService, private http: Http) {
  }

  static handleError(error: any): Promise<any> {
    EntityService.toastService.error('Error in DebugService', '' + (error.message || error));
    return Promise.reject(error.message || error);
  }

  getTemplateDebugDataFromURL(url: string) {

    let apiURL = this.baseUrlService.getBaseUrl() + '/debug?durl=' + encodeURIComponent(url);
    console.log('apiURL:' + apiURL)
    return this.http.get(apiURL).toPromise()
      .then(res => {
        res = res.json();
        return res;
      })
      .catch(err => {
        return EntityService.handleError(err);
      })
  }

  cleanURL(debugURL: String): any {
    console.log(debugURL);
    let params_final = [];
    let tsize = "300x250";
    let paramsx = debugURL.split('?')[1].split("&");
    let boxAddress = debugURL.split('?')[0];

    for (let param of paramsx) {
      let key = param.split("=")[0];
      let value = param.split("=")[1];
      if (key !== 'orand' && key !== 'debug' && value && key !== 'calling_source' && key !== 'test')
        params_final.push({ "key": key, "value": decodeURIComponent(value) });
      if (key === 'tsize') tsize = decodeURIComponent(value);
    }
    console.log(params_final);
    return { "paramsFinal": params_final, "tsize": tsize, "boxAddress": boxAddress };

  }

  makecacheThresholdObj(cacheThresholdCopy) {
    let cacheThreshold = [];
    for (let key of Object.keys(cacheThresholdCopy)) {
      cacheThreshold.push({ "key": key, "value": cacheThresholdCopy[key], "value1": null });
    }
    return cacheThreshold;
  }

  makeDebugURL(boxAddress, params_final, cacheThreshold): string {
    let apiURL = boxAddress + "?calling_source=AUTO_TEMPLATE_ADMIN";
    for (let param of params_final) {
      if (param.value.length === 0)
        continue;
      apiURL = apiURL + '&' + param.key + '=' + encodeURIComponent(param.value);
    }

    if (cacheThreshold) {
      let orandAdded = false;
      for (let flag of cacheThreshold) {
        if (flag['value1']) {
          if (!orandAdded) {
            apiURL = apiURL + '&orand=';
            orandAdded = true;
            apiURL = apiURL + encodeURIComponent(flag.key + '|' + flag.value1);
          }
          else {
            apiURL = apiURL + encodeURIComponent(';' + flag.key + '|' + flag.value1);
          }
        }
      }
    }
    return apiURL;
  }
}
