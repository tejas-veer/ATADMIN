import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { NbMediaBreakpointsService, NbMenuService, NbSidebarService, NbThemeService } from '@nebular/theme';
import {UserService} from '../../../@core/data/users.service';
import { CookieService } from '../../../@core/data/cookie.service';
import { ThemeColorService } from '../../../@core/data/theme-color.service';
import {BuInUrlService} from '../../../@core/data/bu-in-url.service';

@Component({
    selector: 'ngx-header',
    styleUrls: ['./header.component.scss'],
    templateUrl: './header.component.html',
})
export class HeaderComponent implements OnInit {

    @Input() position: string = 'normal';
    @Input() themeColor: string;
    @Input() buSelected: string;
    @Input() dropDownBUSelected: string;
    user: any;
    super_user: any;
    userMenu = [{title: 'Log out'}];

    @Output() dataToParentFromHeader = new EventEmitter<string>();

    constructor(private sidebarService: NbSidebarService,
                private menuService: NbMenuService,
                private userService: UserService,
                private cookieService: CookieService,
                private themeColorService: ThemeColorService,
                private buInUrlService: BuInUrlService,
    ) {

    }

    ngOnInit() {
        this.userService.getSessionData().then(data => {
            this.user = {name: this.userService.getUserName()};
            this.super_user = this.userService.isSuperAdmin();
        }).catch(err => {
            console.log(err);
        });
        this.buSelected = this.cookieService.getBUSelectedFromCookie();
        this.themeColor = this.themeColorService.getThemeColor(this.buSelected);
        this.updateDropDownBUSelected(this.buSelected);
        setTimeout(() => {
            this.toggleSidebar();
        }, 50);
    }

    toggleSidebar(): boolean {
        this.sidebarService.toggle(true, 'menu-sidebar');
        return false;
    }

    toggleSettings(): boolean {

        this.sidebarService.toggle(false, 'settings-sidebar');
        return false;
    }

    goToHome() {
        this.menuService.navigateHome();
    }

    async updateBU(value: string) {
        if (value == 'bu_cm') {
            this.cookieService.setBUSelectedCookie('CM');
            this.buSelected = 'CM';
        }
        if (value == 'bu_max') {
            this.cookieService.setBUSelectedCookie('MAX');
            this.buSelected = 'MAX';
        }
        this.dropDownBUSelected = value;
        await this.buInUrlService.triggerRedir();
        this.sendDataToParent();
    }

    updateDropDownBUSelected(value: string) {
        if (value == 'CM') {
            this.dropDownBUSelected = 'bu_cm';
        }
        if (value == 'MAX') {
            this.dropDownBUSelected = 'bu_max';
        }
    }

    sendDataToParent() {
        this.dataToParentFromHeader.emit("");
    }
}