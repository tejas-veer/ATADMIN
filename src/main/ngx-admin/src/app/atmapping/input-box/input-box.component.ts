import {EntityService} from './../../@core/data/entity-fetch.service';
import {AfterViewInit, Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {BaseUrlService} from '../../@core/data/base-url.service';
import {UserService} from '../../@core/data/users.service';
import {ToastingService} from '../../@core/utils/toaster.service';
import {GlobalLoaderService} from '../../@core/data/global-loader.service';
import {SupplySideComponentWrapper} from './supply-side/supply-side-component-wrapper.component';
import {DemandSideComponent} from './demand-side/demand-side.component';
import {CookieService} from '../../@core/data/cookie.service';
import {ModalPageComponent} from './modal-page/modal-page.component';
import {Subscription} from 'rxjs';
import {BuInUrlService} from '../../@core/data/bu-in-url.service';
import {GoogleAnalyticsConfig} from "../../@core/data/ga-configs";
import {AnalyticsService} from "../../@core/utils";

@Component({
    selector: 'app-input-box',
    templateUrl: './input-box.component.html',
    styleUrls: ['./input-box.component.scss']
})
export class InputBoxComponent implements OnInit, AfterViewInit, OnDestroy {
    ngAfterViewInit(): void {
    }
    inputLoader: boolean;
    enableDemandSide: boolean = false;
    toShowMaxCheckBox: boolean = false;
    level: any;
    selectedLevel: string = 'Customer';
    searchUrl: string;
    domain: string;
    adtag: string;
    domainSearchUrl: string;
    adtagSearchUrl: string;
    levelList = ['Customer', 'Domain', 'AdTag', 'Entity', 'Portfolio', 'AdDomain', 'Advertiser', 'Campaign', 'AdGroup', 'Ad'];
    selectedId: any;
    searchId: string;
    globalEntity: string = 'GLOBAL';
    sector: string;
    searchDomain: any;
    searchAdtag: any;
    shouldReroute: boolean = true;
    private redirSubscription: Subscription;
    googleAnalyticsConfigs = GoogleAnalyticsConfig;

    @ViewChild(SupplySideComponentWrapper) supplySide: SupplySideComponentWrapper;
    @ViewChild(DemandSideComponent) demandSide: DemandSideComponent;
    @ViewChild('bulkModalPage') bulkModalPage: ModalPageComponent;
    @ViewChild('unmapBulkModalPage') unmapBulkModalPage: ModalPageComponent;

    constructor(private baseUrlService: BaseUrlService,
                private  userService: UserService,
                private router: Router,
                private route: ActivatedRoute,
                private toastingService: ToastingService,
                private entityService: EntityService,
                private loaderService: GlobalLoaderService,
                private cookieService: CookieService,
                private buInUrlService: BuInUrlService,
                private analyticsService: AnalyticsService,
                ) {
        this.inputLoader = true;
        this.userService.getSessionData().then(data => {
            if (this.userService.isSuperAdmin()) {
                this.levelList = ['Global', 'Partner'].concat(this.levelList);
            }
            this.inputLoader = false;
        });

        this.route.params.subscribe(params => {
            this.sector = params.section;
            this.sector = this.sector.substr(0, 1).toUpperCase() + this.sector.substr(1);
            if(params.demand && params.demand !== "GLOBAL") {
                this.enableDemandSide = true;
            }
        });

        this.searchUrl = this.baseUrlService.getCustomerSearchUrl();
        this.adtagSearchUrl = this.baseUrlService.getAdtagSearchUrl();
        this.domainSearchUrl = this.baseUrlService.getDomainSearchUrl();

        this.redirSubscription = this.buInUrlService.redir$.subscribe(() => {
            this.redirForBu();
            this.redir();
        });
    }

    ngOnDestroy() {
        this.redirSubscription.unsubscribe();
    }

    ngOnInit() {
        let buSelectedFromCookie = this.cookieService.getBUSelectedFromCookie();
        if(buSelectedFromCookie == 'MAX'){
            this.toShowMaxCheckBox = true;
        }else{
            this.toShowMaxCheckBox = false;
        }
        this.loaderService.disable_inside()
    }

    idKeyup(e): void {
        this.selectedId = this.searchId;
    }

    domainKeyup(e): void {
        console.log("keyup domain", this.searchDomain);
        this.domain = this.searchDomain;
    }

    adtagKeyup(e): void {
        this.adtag = this.searchAdtag;
    }


    changedLevel(e): void {
        this.searchUrl = this.baseUrlService.getSearchUrlForDimension(e.target.value);
        this.selectedId = "";
        this.searchId = "";
        this.domain = "";
        this.adtag = "";
    }

    changedId(e): void {
        this.selectedId = e[this.selectedLevel];
    }

    redirForBu() : boolean{
        this.route.params.subscribe(params => {
            const keys = Object.keys(params);
            if (keys.length < 2)
                this.shouldReroute = false;
            else
                this.shouldReroute = true;
        });
        return this.shouldReroute;
    }

    //Global Search
    redir(): void {
        if(this.shouldReroute) {
            this.getSupplyDemandParams()
                .then(params => {
                    const sectorLowerCase = this.sector ? this.sector.toLowerCase() : '';
                    const keys = Object.keys(params);
                    if (sectorLowerCase !== '' && keys.length > 2) {
                        this.router.navigate(['/at/' + sectorLowerCase + '/query', params]);
                    }
                })
                .catch(error => {
                    this.toastingService.error("Error", error.message);
                });
        }
        this.shouldReroute = true;
        if (this.sector.toUpperCase() === 'MAPPING')
            this.analyticsService.trackEventOnGA(this.googleAnalyticsConfigs.MI_INP_SEARCH);
        else
            this.analyticsService.trackEventOnGA(this.googleAnalyticsConfigs.BI_INP_SEARCH);
    }

    openModal() : void {
        this.bulkModalPage.openModal(status =>{
            console.log("Status : ", status);
        });
    }

    openUnmappingModal() : void {
        this.unmapBulkModalPage.openModal(status =>{
            console.log("Status : ", status);
        });
    }

    async getEntityId(): Promise<string> {
        const domain = this.supplySide.childComponent.domain;
        const adTag = this.supplySide.childComponent.adTag;
        if (!domain || !adTag) {
            throw new Error("Invalid input you have not entered Domain or AdTag");
        }
        this.toastingService.info("Fetching Entity ", "Please Wait for a moment while the Entity Id is Being Fetched");
        return await this.entityService.getEntityId(domain, adTag);
    }

    async getSupplyDemandParams(): Promise<Object> {
        let params = {};
        if(this.supplySide) {
            if(this.supplySide.selectedLevel.toUpperCase() === 'ENTITY') {
                const entityId = await this.getEntityId();
                if(!entityId) {
                    throw new Error("Entity not Found, The Domain AdTag Combination is not currently serving AutoTemplate");
                }
                else {
                    params['supply'] = this.supplySide.selectedLevel.toUpperCase();
                    params['supplyId'] = entityId;
                }
            }
            else {
                if(this.supplySide.getMappedInputValue().length > 0) {
                    params['supply'] = this.supplySide.selectedLevel.toUpperCase();
                    params['supplyId'] = this.supplySide.getMappedInputValue();
                }
                else {
                    params['supply'] = 'GLOBAL';
                    params['supplyId'] = '';
                }
            }
        }

        if (this.demandSide && this.cookieService.getBUSelectedFromCookie() === 'MAX') {
            params['demand'] = this.demandSide.selectedLevel.toUpperCase();
            params['demandId'] = this.demandSide.getMappedInputValue();
        }
        else if (this.demandSide && this.cookieService.getBUSelectedFromCookie() === 'CM'){
            params['demand'] = '';
            params['demandId'] = '';
            if (params['supply'] === 'GLOBAL'){
                params['supply'] = "no_toast_msg";
            }
        }
        else {
            params['demand'] = '';
            params['demandId'] = '';
        }

        params['time'] = Date.now();
        params['bu'] = this.cookieService.getBUSelectedFromCookie();

        return params;
    }

    disableDemandSide(): boolean {
        return this.supplySide === undefined ? false : this.supplySide.selectedLevel.toLowerCase() === 'portfolio';
    }
}
