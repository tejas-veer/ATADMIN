import {ConfigFetchService} from './configfetch.service';
import {HttpClientModule} from '@angular/common/http';
import {EntityService} from './entity-fetch.service';
import {UserService} from "./users.service";
import {GlobalLoaderService} from './global-loader.service';
import {StateService} from './state.service';
import {ModuleWithProviders, NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {CookieService} from './cookie.service';
import {MenuItemsService} from './menu-items.service';
import {BaseUrlService} from "./base-url.service";
import {BuInUrlService} from "./bu-in-url.service";
import {ReportingCsvService} from "./reporting-csv.service";
import {BlockingCsvService} from "./blocking-csv.service";
import {MappingCsvService} from "./mapping-csv.service";
import {CmReportingService} from "./cm-reporting.service";

const SERVICES = [
    UserService,
    StateService,
    MenuItemsService,
    BaseUrlService,
    EntityService,
    ConfigFetchService,
    GlobalLoaderService,
    CookieService,
    BuInUrlService,
    ReportingCsvService,
    CmReportingService,
    BlockingCsvService,
    MappingCsvService,
];

@NgModule({
    imports: [
        CommonModule,
        HttpClientModule,
    ],
    providers: [
        ...SERVICES,
    ],
})
export class DataModule {
    static forRoot(): ModuleWithProviders<DataModule> {
        return <ModuleWithProviders<DataModule>>{
            ngModule: DataModule,
            providers: [
                ...SERVICES,
            ],
        };
    }
}
