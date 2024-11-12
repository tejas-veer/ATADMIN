import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
//todo in next upload try to remove this class
export class ConversionMapService {
  frontendToBackendEnumMap: any = {
    'Bidder': 'Bidder',
    'All Assets' : 'ALL_ASSETS',
    'Publisher Domain': 'Publisher_Domain',
    'Seller Tag Id': 'SELLER_TAG',
    'AUTO ASSET C2A ID': 'C2A_ID',
    'Title': 'Demand_basis',
    'AUTO ASSET IMAGE ID': 'IMAGE_ID',
    'Template Size': 'TEMPLATE_SIZE',
    'Bdata - Advertiser': 'ADVERTISER',
    'Ad Id': 'AD',
    'Demand basis': 'DEMAND_BASIS',
    'Provider Topic': 'Provider_Topic',
    'Display Topic': 'Display_Topic',
    'Campaign': 'CAMPAIGN',
    'AdGroup': 'ADGROUP',
    'Template': 'TEMPLATE',
    'TEMPLATE_TAC': 'TEMPLATE_TAC',
    'Domain': 'DOMAIN',
    'Ad Tag': 'ADTAG',
    'Partner': 'PARTNER',
    'AUTO ASSET TITLE ID': 'AUTO_ASSET_TITLE_ID',
    'Sprig Keyword Category' : 'Sprig Keyword Category',
    'Serp Cluster ID' : 'Serp Cluster ID',
    'Campaign Objective': 'CAMPAIGN_OBJECTIVE',
    'AUTO_ASSET_TEST': 'AUTO_ASSET_TEST',
    'AUTO ASSET IMAGE TEST': 'AUTO_ASSET_IMAGE_TEST',
    'AUTO ASSET TITLE TEST': 'AUTO_ASSET_TITLE_TEST',
    'AUTO ASSET C2A TEST': 'AUTO_ASSET_C2A_TEST',
    'AUTO ASSET IMAGE SETID': 'AUTO_ASSET_IMAGE_SET_ID',
    'AUTO ASSET TITLE SETID': 'AUTO_ASSET_TITLE_SET_ID',
    'AUTO ASSET IMAGE SOURCE TYPE': 'AUTO_ASSET_IMAGE_SOURCE_TYPE',
    'AUTO ASSET TITLE SOURCE TYPE': 'AUTO_ASSET_TITLE_SOURCE_TYPE',
    'AUTO ASSET C2A SOURCE TYPE': 'AUTO_ASSET_C2A_SOURCE_TYPE',
    'Template Type': 'TEMPLATE_TYPE',
    'Sub Template Type': 'SUB_TEMPLATE_TYPE',
  };

  frontendToDrilldownMultiMap: any = {
    'Bidder': ['Bidder'],
    'All Assets' : ['All Assets','ALL_ASSETS'],
    'Title': ['Title', 'AUTO ASSET TITLE ID', 'AUTO_ASSET_TITLE_ID'],
    'C2A': ['C2A_ID', 'C2A', 'AUTO ASSET C2A ID'],
    'Domain': ['Publisher_Domain', 'Publisher Domain',  'Domain', 'PUBLISHER_DOMAIN'],
    'Seller Tag Id': ['SELLER_TAG', 'Seller Tag Id'],
    'Image': ['IMAGE_ID', 'AUTO ASSET IMAGE ID', 'Image'],
    'Template': ['Template', 'TEMPLATE'],
    'Advertiser': ['Advertiser', 'ADVERTISER', 'Bdata - Advertiser'],
    'Campaign': ['Campaign', 'CAMPAIGN'],
    'AdGroup': ['AdGroup', 'ADGROUP'],
    'Ad Id': ['Ad Id', 'AD'],
    'Serp Cluster ID': ['Serp Cluster ID'],
    'Sprig Keyword Category': ['Sprig Keyword Category'],
    'Provider Topic': ['Provider Topic', 'Provider_Topic', 'PROVIDER_TOPIC'],
    'Display Topic': ['Display Topic', 'Display_Topic', 'DISPLAY_TOPIC'],
    'Demand basis': ['Demand basis', 'Demand_basis', 'DEMAND_BASIS', 'Demand Basis'],
    'TEMPLATE_TAC': ['TEMPLATE_TAC'],
    'Campaign Objective': ['Campaign Objective', 'CAMPAIGN_OBJECTIVE'],
    'AUTO_ASSET_TEST': ['AUTO_ASSET_TEST'],
    'AUTO ASSET IMAGE TEST': [ 'AUTO ASSET IMAGE TEST', 'AUTO_ASSET_IMAGE_TEST'],
    'AUTO ASSET TITLE TEST': [ 'AUTO ASSET TITLE TEST', 'AUTO_ASSET_TITLE_TEST'],
    'AUTO ASSET C2A TEST': ['AUTO ASSET C2A TEST', 'AUTO_ASSET_C2A_TEST'],
    'AUTO ASSET IMAGE SETID': ['AUTO ASSET IMAGE SETID', 'AUTO_ASSET_IMAGE_SET_ID'],
    'AUTO ASSET TITLE SETID': [ 'AUTO ASSET TITLE SETID', 'AUTO_ASSET_TITLE_SET_ID'],
    'AUTO ASSET IMAGE SOURCE TYPE': ['AUTO ASSET IMAGE SOURCE TYPE', 'AUTO_ASSET_IMAGE_SOURCE_TYPE'],
    'AUTO ASSET TITLE SOURCE TYPE': ['AUTO ASSET TITLE SOURCE TYPE', 'AUTO_ASSET_TITLE_SOURCE_TYPE'],
    'AUTO ASSET C2A SOURCE TYPE': [ 'AUTO ASSET C2A SOURCE TYPE', 'AUTO_ASSET_C2A_SOURCE_TYPE'],
    'Template Type': ['Template Type', 'TEMPLATE_TYPE'],
    'Sub Template Type': ['Sub Template Type', 'SUB_TEMPLATE_TYPE'],
  };
  constructor() { }
}