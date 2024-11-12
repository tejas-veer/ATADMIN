import {Component, Input, OnInit} from '@angular/core';
import {MenuItemsService} from "../../../@core/data/menu-items.service";
import {Router} from "@angular/router";
import {GlobalLoaderService} from "../../../@core/data/global-loader.service";
import {CookieService} from "../../../@core/data/cookie.service";
import {ThemeColorService} from '../../../@core/data/theme-color.service';
import {AnalyticsService} from "../../../@core/utils";


@Component({
    selector: 'ngx-menu-items',
    templateUrl: './menu-items.component.html',
    styleUrls: ['./menu-items.component.scss']
})
export class MenuItemsComponent implements OnInit {

    @Input() themeColor: string;
    menu: Array<any> = [];

    constructor(private router: Router,
                private menuService: MenuItemsService,
                private loaderService: GlobalLoaderService,
                private cookieService: CookieService,
                private themeColorService: ThemeColorService,
                private analyticsService: AnalyticsService) {
        this.menu = menuService.getData();
        this.activeMarker();
    }

    activeMarker() {
        setInterval(() => {
            let route = this.router.routerState.snapshot.url;
            this.menu.forEach(item => (item.selected = route.indexOf(item.link) > -1));
        }, 1000);
    }

    ngOnInit(): void {
        let buSelected = this.cookieService.getBUSelectedFromCookie();
        this.themeColor = this.themeColorService.getThemeColor(buSelected);
    }

    performOnClick(e, item): void {
        this.analyticsService.trackEventOnGA(item.gaConfig);
        e.preventDefault();
        this.menu.forEach(it => it.selected = false);
        item.selected = true;
        this.loaderService.enable();
        if (item.isOpenInNewTab) {
            window.open(item.link, '_blank');
            return;
        }
        this.router.navigate([item.link]).then(data => {
            this.loaderService.disable();
        });
    }

}
