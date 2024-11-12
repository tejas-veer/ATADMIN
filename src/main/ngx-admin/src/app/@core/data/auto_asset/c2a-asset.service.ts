import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class C2aAssetService {
  EntityNameMapForC2A = {
    'Global': {
        value: 'GLOBAL',
        dropDownValue: 'Global',
        placeholder: 'Global'
    },
    'Domain': {
        value: 'DOMAIN',
        dropDownValue: 'Publisher Domain',
        placeholder: 'Domain'
    },
    'Domain + Campaign ID': {
        value: 'DOMAIN$$CAMPAIGN_ID',
        dropDownValue: 'Campaign',
        placeholder: 'Campaign ID'
    },
    'Domain + Advertiser ID': {
        value: 'DOMAIN$$ADVERTISER_ID',
        dropDownValue: 'Bdata - Advertiser',
        placeholder: 'Advertiser ID'
    },
    'Ad Group ID': {
        value: 'AD_GROUP_ID',
        dropDownValue: 'AdGroup',
        placeholder: 'Ad Group ID'
    },
    'Campaign ID': {
        value: 'CAMPAIGN_ID',
        dropDownValue: 'Campaign',
        placeholder: 'Campaign ID'
    },
    'Advertiser ID': {
        value: 'ADVERTISER_ID',
        dropDownValue: 'Bdata - Advertiser',
        placeholder: 'Advertiser ID'
    },
  };

  constructor() { }

  isAdditionAlInputRequired(entityName) {
    const joinedEntity = ["DOMAIN$$CAMPAIGN_ID", "DOMAIN$$ADVERTISER_ID"];
    return joinedEntity.includes(entityName);
  }
}
