import {Component, EventEmitter, OnDestroy, OnInit, Output} from '@angular/core';
import {NbMediaBreakpointsService, NbMenuItem, NbMenuService, NbSidebarService, NbThemeService,} from '@nebular/theme';

import {StateService} from '../../../@core/data/state.service';

import {Subscription} from 'rxjs/Subscription';
import 'rxjs/add/operator/withLatestFrom';
import 'rxjs/add/operator/delay';
import {MenuItemsService} from '../../../@core/data/menu-items.service';
import {GlobalLoaderService} from "../../../@core/data/global-loader.service";
import {UserService} from "../../../@core/data/users.service";
import {CookieService} from "../../../@core/data/cookie.service";
import { ThemeColorService} from '../../../@core/data/theme-color.service';
import { BuSelectionComponent } from '../../../bu-selection/bu-selection.component';

// TODO: move layouts into the framework
@Component({
    selector: 'ngx-sample-layout',
    styleUrls: ['./sample.layout.scss'],
    template: `

        <nb-layout [center]="layout.id === 'center-column'" *ngIf="!isBUSelected()">
            <nb-layout-header fixed>
                <div class="bu-header-container bu-left">
                    <div class="bu-logo-container">
                        <div class="bu-logo">Auto Template</div>
                    </div>
                </div>
                <nb-actions
                        size="medium"
                        class="bu-header-container bu-right">
                    <nb-action>
                        <nb-user style="font-family: Exo;" [name]="user?.name"></nb-user>
                    </nb-action>
                    <nb-action>
                        <ngx-datetime ></ngx-datetime>
                    </nb-action>
                </nb-actions>
            </nb-layout-header>
            <nb-layout-column class="main-content">
                <ng-content select="ngx-bu-selection"></ng-content>
            </nb-layout-column>
        </nb-layout>

        <nb-layout [center]="layout.id === 'center-column'" *ngIf="isBUSelected()">
            <nb-layout-header fixed>
                <ngx-header
                        (dataToParentFromHeader)="eventFromChild($event)"
                ></ngx-header>
                <mat-progress-bar *ngIf="loaderConfig.enabled"
                                  [color]="loaderConfig.color"
                                  mode="indeterminate"
                                  style="position: absolute;left: 0;bottom: 0;height: 4px;">

                </mat-progress-bar>
            </nb-layout-header>
            <nb-sidebar class="menu-sidebar"
                        tag="menu-sidebar"
                        responsive
                        state="compacted"
                        [right]="sidebar.id === 'right'"
                        style="background-color: white;z-index: 3;"
                        >
                <ng-content select="ngx-menu-items"></ng-content>
            </nb-sidebar>
            <nb-layout-column class="main-content">
                <ng-content select="router-outlet"></ng-content>
            <ngx-bu-selection></ngx-bu-selection>
            </nb-layout-column>
        </nb-layout>
    `,
})
export class SampleLayoutComponent implements OnDestroy, OnInit {
    @Output() dataToParentFromSample = new EventEmitter<string>();

    layout: any = {};
    sidebar: any = {};
    user: any;
    loaderConfig: any = {enabled: true, color: 'primary'};
    protected layoutState$: Subscription;
    protected sidebarState$: Subscription;
    protected menuClick$: Subscription;

    constructor(protected stateService: StateService,
                protected menuService: NbMenuService,
                protected themeService: NbThemeService,
                protected bpService: NbMediaBreakpointsService,
                protected sidebarService: NbSidebarService,
                protected menuItemService: MenuItemsService,
                private globalLoaderService: GlobalLoaderService,
                private userService: UserService,
                private cookieService: CookieService,
                private themeColorService : ThemeColorService
    ) {
    }

    ngOnInit(): void {
        this.loaderConfig = {enabled: true, color: 'primary'};
        this.globalLoaderService.loaderObject = this.loaderConfig;

        this.userService.getSessionData().then(data => {
            this.user = {name: this.userService.getUserName()};
        }).catch(err => {
            console.log(err);
        });
    }

    isBUSelected(): boolean{
        let buSelected = this.cookieService.getBUSelectedFromCookie();
        if(buSelected == 'MAX' || buSelected == 'CM'){
            return true;
        }
        return false;
    }

    eventFromChild($event : any) {
        this.dataToParentFromSample.emit("");
    }

    ngOnDestroy() {
    }
}
