import { Injectable } from '@angular/core';
import {ToastingService} from "../utils/toaster.service";
@Injectable({
  providedIn: 'root',
})
export class BlockingCsvService {
  validBlockingHeaders: String[] = ['supply_hierarchy_level', 'supply_entity_value', 'demand_hierarchy_level', 'demand_entity_value', 'id', 'creative_type', 'size', 'system_page_type'];
  constructor(private toastingService: ToastingService) { }

  isBlockingCsvValid(headers: Array<String>): boolean {
    return this.validBlockingHeaders.every(ele => headers.indexOf(ele) !== -1);
  }

  getBlockingPayload(results: any): Array<any> {
    let data = [];
    if (!this.isBlockingCsvValid(results[0])) {
      this.toastingService.error('Column Missing', 'Column missing or column name is not as expected');
    } else {
      for (let i = 1; i < results.length; i++) {
        let obj = {
          supply: results[i][0],
          supplyId: results[i][1],
          demand: results[i][2],
          demandId: results[i][3],
          templateIds: results[i][4],
          type: results[i][5],
          templateSizes: results[i][6],
          systemPageType: results[i][7],
        };
        data.push(obj);
      }
    }
    return data;
  }
}
