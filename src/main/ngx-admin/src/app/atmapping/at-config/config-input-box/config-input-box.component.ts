import {EntityService} from './../../../@core/data/entity-fetch.service';
import {Component, OnDestroy, OnInit} from '@angular/core';
import {BaseUrlService} from "../../../@core/data/base-url.service";
import {UserService} from "../../../@core/data/users.service";
import {ActivatedRoute, Router} from "@angular/router";
import {ToastingService} from "../../../@core/utils/toaster.service";
import {GlobalLoaderService} from "../../../@core/data/global-loader.service";
import {CookieService} from "../../../@core/data/cookie.service";
import {BuInUrlService} from "../../../@core/data/bu-in-url.service";
import {Subscription} from 'rxjs';

@Component({
  selector: 'config-input-box',
  templateUrl: './config-input-box.component.html',
  styleUrls: ['./config-input-box.component.scss']
})
export class ConfigInputBoxComponent implements OnInit, OnDestroy {
  inputLoader: boolean;

  level: any;
  selectedLevel: string = 'Customer';
  searchUrl: string;
  domain: string;
  adtag: string;
  domainSearchUrl: string;
  adtagSearchUrl: string;
  levelList = ['Customer', 'Domain', 'AdTag', 'Entity'];
  selectedId: any;
  searchId: string;
  globalEntity: string = 'GLOBAL';
  sector: string;
  searchDomain: any;
  searchAdtag: any;
  buFromUrl: string;
  showToasterError: boolean = true;
  private configRedirSubscription: Subscription;


  constructor(private baseUrlService: BaseUrlService, private  userService: UserService,
              private router: Router, private route: ActivatedRoute, private toastingService: ToastingService,
              private entityService: EntityService, private loaderService: GlobalLoaderService,
              private cookieService: CookieService, private buInUrlService: BuInUrlService) {
    this.selectedLevel = 'Customer';
    this.inputLoader = true;
    this.userService.getSessionData().then(data => {
      if (this.userService.isSuperAdmin()) {
        this.levelList = ['Global', 'Partner'].concat(this.levelList);
      }
      this.inputLoader = false;
    });

    this.route.params.subscribe(params => {
      this.sector = params.section;
      this.buFromUrl = params.bu;
      if (this.buFromUrl !== undefined && this.buFromUrl !== null) {
        this.cookieService.setBUSelectedCookie(params.bu);}
      this.sector = this.sector.substr(0, 1).toUpperCase() + this.sector.substr(1);
      if (params.name) {
        this.selectedId = params.name;
        this.searchId = params.name;
      } else if(params.entityId) {
        this.selectedId = params.entityId;
        this.searchId = params.entityId;
      }

      if (params.domain) {
        this.searchDomain = params.domain;
        this.domain = params.domain;
      }
      if (params.adtag) {
        this.searchAdtag = params.adtag;
        this.adtag = params.adtag;
      }

      if (params.level)
        this.selectedLevel = params.level;
    });

    this.searchUrl = this.baseUrlService.getCustomerSearchUrl();
    this.adtagSearchUrl = this.baseUrlService.getAdtagSearchUrl();
    this.domainSearchUrl = this.baseUrlService.getDomainSearchUrl();
  }

  ngOnInit() {
    this.loaderService.disable_inside();
    this.configRedirSubscription = this.buInUrlService.configRedir$.subscribe(() => {
      this.redirectForBu();
    });
  }

  ngOnDestroy(): void {
    this.configRedirSubscription.unsubscribe();
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

  changedDomain(e): void {
    if (e["Domain"]) {
      this.domain = e["Domain"];
    } else {
      this.domain = this.searchDomain;
    }
  }

  changedAdtag(e): void {
    if (e["AdTag"]) {
      this.adtag = e["AdTag"];
    } else {
      this.adtag = this.searchAdtag;
    }
  }

  redir(): void {
    const path = "/at/config/cfg/d/";
    if (this.selectedLevel == 'Entity') {
      console.log(this.domain, this.adtag);
      if (!this.domain || !this.adtag) {
        this.toastingService.error("Invalid Input", "You have not Entered Domain or Adtag");
        return;
      }
      this.toastingService.info("Fetching Entity ", "Please Wait for a moment while the Entity Id is Being Fetched");
      this.inputLoader = true;

      this.entityService.getEntityId(this.domain, this.adtag).then(data => {
        console.log("Entity Id", data);
        if (!data) {
          this.toastingService.error("Entity not Found", "The Domain Adtag Combination is not currently serving autotemplate");
          this.inputLoader = false;
          return;
        }
        this.router.navigate([path + this.selectedLevel + '/' + data, {
          time: Date.now(),
          domain: this.domain,
          adtag: this.adtag,
          bu: this.cookieService.getBUSelectedFromCookie(),
        }]);
        this.inputLoader = false;
      }).catch(err => {

      });
      return;
    }

    if (this.selectedLevel == 'Global') {
      this.router.navigate([path + this.selectedLevel + '/' + this.globalEntity,
        {time: Date.now(), bu: this.cookieService.getBUSelectedFromCookie()}]);
      return
    }

    if (this.selectedId)
      this.router.navigate([path + '/' + this.selectedLevel + '/' + EntityService.processedAdtag(this.selectedId), {
        time: Date.now(),
        name: this.selectedId,
        bu: this.cookieService.getBUSelectedFromCookie(),
      }]);
    else if (this.searchId)
      this.router.navigate([path + '/' + this.selectedLevel + '/' + EntityService.processedAdtag(this.searchId), {
        time: Date.now(),
        name: this.searchId,
        bu: this.cookieService.getBUSelectedFromCookie(),
      }]);
    else if (this.showToasterError)
      this.toastingService.error('Invalid Parameters', 'You have not entered a valid Id');
  }

  redirectForBu(): void {
    this.showToasterError = false;
    this.redir();
    this.showToasterError = true;
  }

}