import { Injectable } from '@angular/core';
import { AutoAssetService } from './auto-asset.service';
import { ConversionMapService } from '../conversion-map.service';
import { UtilService } from '../../utils/util.service';

@Injectable({
  providedIn: 'root'
})
export class AutoAssetEntityDropdownService {
  entityNameMap;
  constructor(public autoAssetService: AutoAssetService,
    private conversionMapService: ConversionMapService,
    public utilService : UtilService,
  ) {
  }

  setEntityNameMap(map: any) {
    this.entityNameMap = map;
  }

  getSelectedEntityName(entityName) {
    return entityName.selectedValue;
  }

  getSelectedEntityNameValue(entityName) {
    return this.entityNameMap[entityName.selectedValue]?.value;
  }

  getSelectedEntityNameValueFromPlaceholder(entityName) {
    return this.entityNameMap[entityName]?.value;
  }

  getSelectedEntityNameValueFromPlaceholderFromEntityMap(entityName , entityNameMap) {
    return entityNameMap[entityName]?.value;
  }

  getEntityIdsFromSelectedEntityValues(entityValue) {
    let values = entityValue.selectedValue;
    return this.getIdsFromValue(values);
  }

  getIdsFromValue(values) {
    let entityValueList = [];
    if (this.utilService.isSet(values)) {
      if (!Array.isArray(values)) {
        values = [values];
      }
      entityValueList = values.map((item) =>
        this.autoAssetService.extractIdUsingRegex(item)
      );
    }
    return entityValueList;
  }

  getCommaSeparatedEntityValue(entityValue) {
    return this.getEntityIdsFromSelectedEntityValues(entityValue).join(",");
  }


  async getDropdownOptionsForInput(analyticDimensionName, inputValue: string): Promise<string[]> {
    try {
      const data = await this.autoAssetService.getAutoSelectSuggestions(analyticDimensionName, inputValue);
      return this.processDropdownOptions(data, analyticDimensionName);
    } catch (error) {
      return [];
    }
  }

  private processDropdownOptions(data: any, selectedDimensionValue: string): string[] {
    const dimensionEnumName = this.conversionMapService.frontendToBackendEnumMap[selectedDimensionValue].toLowerCase();
    const convertKeysToLowerCase = (obj) => {
      const converted = {};
      for (const key in obj) {
        if (Object.prototype.hasOwnProperty.call(obj, key)) {
          converted[key.toLowerCase()] = obj[key];
        }
      }
      return converted;
    };

    return Object.values(data).map((item) => {
      const itemLowerCaseKeys = convertKeysToLowerCase(item);
      if (itemLowerCaseKeys[dimensionEnumName] !== undefined && itemLowerCaseKeys[dimensionEnumName] !== null) {
        return itemLowerCaseKeys[dimensionEnumName];
      }
      return '';
    });
  }

}
