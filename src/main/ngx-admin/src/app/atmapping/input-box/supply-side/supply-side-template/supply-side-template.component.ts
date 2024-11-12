import {AfterViewInit, Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {BaseUrlService} from '../../../../@core/data/base-url.service';
import {ActivatedRoute} from '@angular/router';
import {EntityTypeMapping} from '../../../../@beans/EntityTypeMapping';
import {EntityType} from '../../../../@beans/EntityType';
import {UtilService} from '../../../../@core/utils/util.service';
import {AbstractQuerySide} from '../../../../@beans/AbstractQuerySide';
import {EntityService} from '../../../../@core/data/entity-fetch.service';
import {ToastingService} from '../../../../@core/utils/toaster.service';
import {CookieService} from "../../../../@core/data/cookie.service";

@Component({
  selector: 'supply-side-template',
  templateUrl: './supply-side-template.component.html',
  styleUrls: ['./supply-side-template.component.scss']
})
export class SupplySideTemplateComponent extends AbstractQuerySide implements OnInit, AfterViewInit {

  @ViewChild('childComponentTemplate') childComponentTemplate: TemplateRef<any>;
  adTag: string;
  domain: string;

  constructor(baseUrlService: BaseUrlService, private route: ActivatedRoute, private entityService: EntityService,
              private toastingService: ToastingService, protected cookieService: CookieService) {
      super('Supply',
          [
              new EntityTypeMapping(EntityType.CUSTOMER, (x: string) => UtilService.extractCustomerId(x)),
              new EntityTypeMapping(EntityType.GLOBAL_CUSTOMER, (x: string) => UtilService.extractCustomerId(x)),
              new EntityTypeMapping(EntityType.PARTNER, (x: string) => UtilService.extractPartnerId(x)),
              new EntityTypeMapping(EntityType.DOMAIN, (x: string) => x.toString().toLowerCase()),
              new EntityTypeMapping(EntityType.AD_TAG, (x: string) => UtilService.extractId(x)),
              new EntityTypeMapping(EntityType.GLOBAL_AD_TAG, (x: string) => UtilService.extractId(x)),
              new EntityTypeMapping(EntityType.ENTITY, (x: string) => x.toString().toUpperCase()),
              new EntityTypeMapping(EntityType.PORTFOLIO, (x: string) => UtilService.extractIdFromDruidFormat(x.toString().toUpperCase())),
              new EntityTypeMapping(EntityType.GLOBAL_PORTFOLIO, (x: string) => UtilService.extractIdFromDruidFormat(x.toString().toUpperCase())),
              new EntityTypeMapping(EntityType.ITYPE, (x: string) => x.toString().toUpperCase()),
              new EntityTypeMapping(EntityType.GLOBAL, (x: string) => EntityType.GLOBAL.normalize().toUpperCase()),
          ],
          baseUrlService, cookieService);
  }

  ngOnInit() {
  }

  getDomainSearchUrl(): string {
      if (this.cookieService.getBUSelectedFromCookie() === 'MAX') {
          return this.baseUrlService.getSearchUrlForDimension('Publisher_Domain');
      } else return this.baseUrlService.getSearchUrlForDimension('domain');
  }

  getDomainListFormatter(): string {
      if (this.cookieService.getBUSelectedFromCookie() === 'MAX') {
          return 'Publisher_Domain';
      } else return 'Domain';
  }

  getAdTagSearchUrl(): string {
      return this.baseUrlService.getAdtagSearchUrl();
  }

  changeAdTag(e): void {
      this.adTag = e[EntityType.AD_TAG.normalize()];
  }

  changeDomain(e): void {
      this.domain = e[EntityType.DOMAIN.normalize()];
  }

  idKeyup(e): void {
  }

  ngAfterViewInit(): void {
      setTimeout(() => {
          this.route.params.subscribe(params => {
              this.selectedLevel = this.getLevelFromParam(params.supply ? params.supply : '');
              this.selectedValue = params.supplyId ? params.supplyId : '';

              if (this.selectedLevel.toUpperCase() === 'ENTITY') {
                  this.entityService.getEntity(this.selectedValue)
                      .then(json => {
                          if (json.hasOwnProperty('response')) {
                              this.adTag = json.response.adtag;
                              this.domain = json.response.domain;
                          } else {
                              this.toastingService.error('INVALID_ENTITY_ID', 'No Domain AdTag Combination Found');
                          }
                      });
              }
          });
      }, 1000);
  }

}

