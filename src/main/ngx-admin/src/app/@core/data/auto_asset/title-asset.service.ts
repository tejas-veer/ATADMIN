import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class TitleAssetService {
  EntityNameMapForTitle = {
    'Demand Basis': {
    value: 'DEMAND_BASIS',
    dropDownValue: 'Demand basis',
    placeholder: 'Demand Basis',
},
'Ad ID': {
    value: 'AD_ID',
    dropDownValue: 'Ad Id',
    placeholder: 'Ad ID',
},
'Ad Group ID': {
    value: 'AD_GROUP_ID',
    dropDownValue: 'AdGroup',
    placeholder: 'Ad Group ID',
},
'Campaign ID': {
    value: 'CAMPAIGN_ID',
    dropDownValue: 'Campaign',
    placeholder: 'Campaign ID',
},
'Ad ID Demand Basis': {
    value: 'AD_ID$$DEMAND',
    dropDownValue: 'Ad Id',
    placeholder: 'Ad ID',
},
'Ad Group ID Demand Basis': {
    value: 'AD_GROUP_ID$$DEMAND',
    dropDownValue: 'AdGroup',
    placeholder: 'Ad Group ID',
},
'Campaign ID Demand Basis': {
    value: 'CAMPAIGN_ID$$DEMAND',
    dropDownValue: 'Campaign',
    placeholder: 'Campaign ID',
},
};
  constructor() { }

  isMultipleEntityValueInputBox(entityName) {
    const demandBasisValues = ["AD_ID$$DEMAND", "AD_GROUP_ID$$DEMAND", "CAMPAIGN_ID$$DEMAND"];
    return demandBasisValues.includes(entityName);
}
}
