import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ReportingConstantsService {

  public MAX_FILTER_CHIPS_LIST: Array<string> = ['Bidder', 'Display Topic', 'Provider Topic', 'Demand basis', 'Template', 'Template Size', 'Publisher Domain',
    'Bdata - Advertiser', 'Seller Tag Id', 'TEMPLATE_TAC', 'Campaign', 'AdGroup', 'Sprig Keyword Category' , 'Serp Cluster ID' , 'AUTO ASSET IMAGE ID',
      'AUTO ASSET TITLE ID', 'AUTO ASSET C2A ID', 'Campaign Objective', 'AUTO_ASSET_TEST', 'AUTO ASSET IMAGE TEST', 'AUTO ASSET TITLE TEST',
    'AUTO ASSET C2A TEST', 'AUTO ASSET IMAGE SETID', 'AUTO ASSET TITLE SETID', 'AUTO ASSET IMAGE SOURCE TYPE',
    'AUTO ASSET TITLE SOURCE TYPE', 'AUTO ASSET C2A SOURCE TYPE', 'Ad Id', 'Template Type', 'Sub Template Type'];

  public readonly MAX_RENDER_BY_OPTIONS: Array<string> = ['Bidder', 'Template', 'Title', 'Image', 'C2A', 'All Assets', 'Ad Id',
     'Display Topic', 'Provider Topic', 'Demand basis', 'Sprig Keyword Category' , 'Serp Cluster ID' , 'AUTO_ASSET_TEST', 'AUTO ASSET IMAGE TEST',
    'AUTO ASSET TITLE TEST', 'AUTO ASSET C2A TEST', 'AUTO ASSET IMAGE SETID', 'AUTO ASSET TITLE SETID', 'AUTO ASSET IMAGE SOURCE TYPE',
    'AUTO ASSET TITLE SOURCE TYPE', 'AUTO ASSET C2A SOURCE TYPE'];

  public MAX_REPORTING_METRICS: Array<string> = ['Valid Impressions', 'Weighted Conversions', 'Valid Clicks', 'Actual CVR', 'Media.net Rev',
    'Media.net Profit', 'Media.net CPA', 'Actual CTR', 'Media.net CPM', 'Media.net RPM', 'Media.net Internal RPM',
    '[Ad] CTR * Actual CVR', 'Rev Ratio', 'Internal Rev Ratio'];

  public readonly ALL_DRILLDOWN_DROPDDOWN_OPTIONS: Array<string> = ['Bidder', 'All Assets', 'Title', 'C2A', 'Domain', 'Seller Tag Id', 'Advertiser', 'Campaign',
    'Template', 'Image', 'AdGroup', 'Ad Id', 'Display Topic',  'Provider Topic' , 'Demand basis', 'Sprig Keyword Category' , 'Serp Cluster ID', 'TEMPLATE_TAC', 'Campaign Objective', 'AUTO_ASSET_TEST', 'AUTO ASSET IMAGE TEST', 'AUTO ASSET TITLE TEST',
    'AUTO ASSET C2A TEST', 'AUTO ASSET IMAGE SETID', 'AUTO ASSET TITLE SETID', 'AUTO ASSET IMAGE SOURCE TYPE',
    'AUTO ASSET TITLE SOURCE TYPE', 'AUTO ASSET C2A SOURCE TYPE', 'Template Type', 'Sub Template Type'];

  public CM_FILTER_CHIPS_LIST: Array<string> = ['Template', 'Template Size', 'Domain', 'Ad Tag', 'Partner'];

  public readonly CM_RENDER_BY_OPTIONS: Array<string> = ['Template'];

  public CM_REPORTING_METRICS: Array<string> = ['Page Impression', 'Keyword Clicks', 'Ad Clicks', 'Revenue', 'RPM', 'L2R (%)', 'L2A (%)', 'RPC'];

  public readonly ONLY_IMAGE_TEMPLATE: number  = 900000954;

  public readonly PURE_NATIVE_REFERENCE_TEMPLATE = {
    id: 900010315,
    name: 'PURE NATIVE REFERENCE TEMPLATE',
    width: '300',
    height: '250',
  };

  constructor() { }
}
