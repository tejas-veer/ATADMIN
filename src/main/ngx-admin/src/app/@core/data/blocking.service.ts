import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {BaseUrl} from './base-url.service';
import {ToastingService} from '../utils/toaster.service';
import {CookieService} from './cookie.service';

@Injectable({
  providedIn: 'root',
})
export class BlockingService {

  constructor(private http: HttpClient,
    private toastingService: ToastingService,
    private cookieService: CookieService) {
  }

  public getBlockingTable(supplyValue: string, supply: string, demandValue: string, demand: string, type: string):
      Promise<any> {
        const url = BaseUrl.get() + '/atmapping/query/blocking/' + type.toUpperCase();
        const buSelected = this.cookieService.getBUSelectedFromCookie();
        return this.http.get(url, {
          params: new HttpParams({
            fromObject: {
              supply: supply.toUpperCase(),
              supplyId: supplyValue,
              demand: demand.toUpperCase(),
              demandId: demandValue,
              buSelected: buSelected,
            },
          }),
    })
      .toPromise()
      .catch(err => {
        this.handleError(err);
      });
  }


  public addNewBlocking(sizeList: Array<any>, hierarchyLevel: Object, status: string): Promise<any> {
    const url = BaseUrl.get() + '/atmapping/update/blocking';
    const updates = BlockingService.sizeListToBlockingInfo(sizeList, status);
    const buSelected = this.cookieService.getBUSelectedFromCookie();
    return this.http
      .post(url, {
        hierarchyLevel: hierarchyLevel,
        payload: updates,
        buSelected: buSelected,
      })
      .toPromise()
      .then(data => {
        return { response: data, updates: updates };
      })
      .catch(err => this.handleError(err));
  }

  private handleError(err): any {
    this.toastingService.error(err.status + ' ' + err.statusText, err.error);
    return Promise.reject(err);
  }

  public static sizeListToBlockingInfo(sizeList: Array<any>, status: string): Array<any> {
    const blockingInfoList = [];

    sizeList.forEach(item => {
      const size = item.size;
      item.creativeList.forEach(creative => {
        blockingInfoList.push({
          creativeId: creative.id,
          size: size,
          status: status,
          type: creative.type.toUpperCase(),
        });
      });
    });
    return blockingInfoList;
  }
}
