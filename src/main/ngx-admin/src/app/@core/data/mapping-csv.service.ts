import { Injectable } from '@angular/core';
import {ToastingService} from "../utils/toaster.service";

@Injectable({
  providedIn: 'root',
})
export class MappingCsvService {
  validMappingHeaders: String[] = ['supply_hierarchy_level', 'supply_entity_value', 'demand_hierarchy_level', 'demand_entity_value', 'template_id', 'mapping_type', 'template_size', 'system_page_type'];

  constructor(private toastingService: ToastingService) { }

  isCsvValid(validHeaders: String[], headers: Array<String>): boolean {
    return validHeaders.every(ele => headers.indexOf(ele) !== -1);
  }

  getMappingPayload(results: any): Array<any> {
    let data = [];
    if (!this.isCsvValid(this.validMappingHeaders, results[0])) {
      this.toastingService.error('Column Missing', 'Column missing or column name is not as expected');
    } else {
      for (let i = 1; i < results.length; i++) {
        let obj = {
          supply: results[i][0],
          supplyId: results[i][1],
          demand: results[i][2],
          demandId: results[i][3],
          templateIds: results[i][4],
          mappingType: results[i][5],
          templateSizes: results[i][6],
          systemPageType: results[i][7],
        };
        data.push(obj);
      }
    }
    return data;
  }

}
