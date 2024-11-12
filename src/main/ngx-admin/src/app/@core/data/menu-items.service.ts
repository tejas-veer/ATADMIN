import {Injectable} from '@angular/core';
import {CookieService} from './cookie.service';
import {GoogleAnalyticsConfig} from "./ga-configs";

@Injectable()
export class MenuItemsService {
    constructor(private cookieService: CookieService) {
    }

    public getBU() {
        const buSelected = this.cookieService.getBUSelectedFromCookie();
        if (buSelected === 'MAX') {
            return 'CM-Native';
        } else {
            return 'Keywords-Only';
        }
    }

    data: any[] = [
        {
            title: 'Reporting',
            icon: 'nb-bar-chart',
            link: 'atreporting/',
            desc: 'Use data to manipulate template serving',
            isOpenInNewTab: false,
            gaConfig: GoogleAnalyticsConfig.REPORTING_TAB,
        },
        {
            title: 'Mapping',
            icon: 'nb-plus',
            link: 'at/mapping',
            desc: 'Map templates to entities',
            isOpenInNewTab: false,
            gaConfig: GoogleAnalyticsConfig.MAPPING_TAB,
        },
        {
            title: 'Blocking',
            icon: 'nb-close-circled',
            link: 'at/blocking',
            desc: 'Block Templates or Frameworks on entities',
            isOpenInNewTab: false,
            gaConfig: GoogleAnalyticsConfig.BLOCKING_TAB,
        },
        {
            title: 'Auto Asset',
            icon: 'nb-layout-default',
            link: 'auto_asset/v2',
            desc: 'Auto Assets Interface',
            isOpenInNewTab: false,
            gaConfig: GoogleAnalyticsConfig.AUTO_ASSET_INTERFACE_TAB,
        },
        {
            title: 'Custom Assets Renderer',
            icon: 'nb-compose',
            link: 'http://appsmith.internal.reports.mn/app/custom-asset-renderer/template-stitcher-65081d30f773d30a56f6897d?bu_selected='
                + this.getBU(),
            desc: 'Check preset assets & edit them. Interface is sharable.',
            isOpenInNewTab: true,
            gaConfig: GoogleAnalyticsConfig.CUSTOM_ASSETS_RENDERER_TAB,
        },
        {
            title: 'Bulk Template Renderer',
            icon: 'nb-grid-b-outline',
            link: this.cookieService.getBulkPreviewEndPointBasedOnBU(),
            desc: 'Render templates at bulk for your demand',
            isOpenInNewTab: true,
            gaConfig: GoogleAnalyticsConfig.BULK_TEMPLATE_RENDERER_TAB,
        },
        {
            title: 'Multi Ads Renderer',
            icon: 'nb-layout-two-column',
            link: 'http://appsmith.internal.reports.mn/app/render/home-6478888b70e1cb3bdb7a98d8?branch=master',
            desc: 'Check what got rendered for your Adgroup for a MultiAd template',
            isOpenInNewTab: true,
            gaConfig: GoogleAnalyticsConfig.MULTI_ADS_RENDERER_TAB,
        },
        {
            title: 'Bulk Upload Status',
            icon: 'nb-help',
            link: 'request',
            desc: 'Get status of your Bulk uploads',
            isOpenInNewTab: false,
            gaConfig: GoogleAnalyticsConfig.BULK_UPLOAD_STATUS_TAB,
        },
        {
            title: 'Config',
            icon: 'nb-gear',
            link: 'at/config/cfg/i',
            desc: 'Set Header Text,AdChoices Link and other generation parameters',
            isOpenInNewTab: false,
            gaConfig: GoogleAnalyticsConfig.CONFIG_TAB,
        },
        {
            title: 'Feedback Form',
            icon: 'nb-arrow-retweet',
            link: 'https://forms.gle/Aa6ZkomVeGbm9xBE8',
            desc: 'Got a feedback, bug or request to make? Fill this',
            isOpenInNewTab: true,
            gaConfig: GoogleAnalyticsConfig.FEEDBACK_FORM_TAB,
        },
    ];

    getData() {
        return this.data;
    }
}
