import {CookieService} from './@core/data/cookie.service';
import {Component, OnInit} from '@angular/core';
import {AnalyticsService} from './@core/utils/analytics.service';
import {ToasterService} from 'angular2-toaster';
import {ToastingService} from './@core/utils/toaster.service';
import {ThemeColorService} from './@core/data/theme-color.service';
import {NbThemeService} from '@nebular/theme';
import {MenuItemsService} from './@core/data/menu-items.service';
import {GlobalLoaderService} from './@core/data/global-loader.service';
import {EntityService} from './@core/data/entity-fetch.service';
import {ConfigFetchService} from './@core/data/configfetch.service';
import {GeneratorService} from './@core/data/generator.service';
import {BuInUrlService} from "./@core/data/bu-in-url.service";
@Component({
    selector: 'ngx-app',
    template: `
        <toaster-container [toasterconfig]="config"></toaster-container>
        <ngx-sample-layout
                (dataToParentFromSample)="eventFromChild($event)">
            <ngx-menu-items></ngx-menu-items>
            <router-outlet></router-outlet>
            <ngx-bu-selection></ngx-bu-selection>
        </ngx-sample-layout>
    `,
})
export class AppComponent implements OnInit {
    config: any;
    menu: any;

    constructor(private analytics: AnalyticsService,
                private themeService: NbThemeService,
                private toastService: ToastingService,
                private toasterService: ToasterService,
                private menuService: MenuItemsService,
                private loaderService: GlobalLoaderService,
                private cookieService: CookieService,
                private themeColorService: ThemeColorService,
                private buInUrlService: BuInUrlService) {
        this.config = this.toastService.getToastConfig();
    }

    ngOnInit(): void {
        this.analytics.trackPageViews();
        this.themeService.changeTheme('default');
        this.toastService.setToasterService(this.toasterService);
        ConfigFetchService.setToastingService(this.toastService);
        EntityService.setToastingService(this.toastService);
        GeneratorService.setToastingService(this.toastService);
        this.menu = this.menuService.getData();
    }

    eventFromChild($event: any) {
        setTimeout(() => {
            localStorage.removeItem('isReload');
            this.buInUrlService.fetchBuFromUrlAndSetCookie();
        }, 1000);
    }

    menuClick(): void {
    }
}
