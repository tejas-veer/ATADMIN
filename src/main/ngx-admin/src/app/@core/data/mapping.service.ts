import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { BaseUrl } from './base-url.service';
import { CookieService } from './cookie.service';

@Injectable({
  providedIn: 'root'
})
export class MappingService {

  requestDisplayedColumns = ['requestId','requestType','adminName','creationDate','rowsGenerated','progressPercentage']

  constructor(private http: HttpClient,
    private cookieService: CookieService) {

  }

  getMappings(supplyValue: string, supply: string, demandValue: string, demand: string, type: string): Promise<any> {
    const url = BaseUrl.get() + '/atmapping/query/mapping/' + type.toUpperCase();
    const buSelected = this.cookieService.getBUSelectedFromCookie();
    return this.http.get(url, {
      params:
        new HttpParams({
          fromObject: {
            supply: supply.toUpperCase(),
            supplyId: supplyValue,
            demand: demand.toUpperCase(),
            demandId: demandValue,
            buSelected: buSelected
          }
        })
    }).toPromise();
  }

  insertMappings(type: string, payload: Object): Promise<any> {
    const url = BaseUrl.get() + '/atmapping/update/mapping/' + type.toUpperCase() + '/insert';
    return this.http.post(url, payload).toPromise();
  }

  showRequests(): Promise<any>{
    const url = BaseUrl.get() + '/atmapping/update/request';
    return this.http.get(url).toPromise();
  }

  insertBulkMapping(payload: Object): Promise<any> {
    const url = BaseUrl.get() + '/atmapping/update/bulkMapping/insert';
    return this.http.post(url, payload).toPromise();
  }

  insertBulkUnMapping(payload: Object): Promise<any> {
    const url = BaseUrl.get() + '/atmapping/update/bulkUnmapping';
    return this.http.post(url, payload).toPromise();
  }

  insertBulkBlocking(payload: Object): Promise<any> {
    const url = BaseUrl.get() + '/atmapping/update/bulkBlocking';
    return this.http.post(url, payload).toPromise();
  }

  deleteMappings(type: string, payload: Object): Promise<any> {
    const url = BaseUrl.get() + '/atmapping/update/mapping/' + type.toUpperCase() + '/delete';
    return this.http.post(url, payload).toPromise();
  }
}
