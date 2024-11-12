import { AfterViewInit, Component, OnInit } from '@angular/core';
import { AbstractQuerySide } from "../../../@beans/AbstractQuerySide";
import { BaseUrlService } from "../../../@core/data/base-url.service";
import { EntityTypeMapping } from "../../../@beans/EntityTypeMapping";
import { EntityType } from "../../../@beans/EntityType";
import { UtilService } from "../../../@core/utils/util.service";
import { ActivatedRoute } from "@angular/router";
import {CookieService} from "../../../@core/data/cookie.service";
@Component({
  selector: 'ngx-demand-side',
  templateUrl: './demand-side.component.html',
  styleUrls: ['./demand-side.component.scss']
})
export class DemandSideComponent extends AbstractQuerySide implements OnInit, AfterViewInit {


  constructor(baseUrlService: BaseUrlService, private route: ActivatedRoute, protected cookieService: CookieService) {
    super("Demand",
      [
        new EntityTypeMapping(EntityType.AD_GROUP, (x: string) => UtilService.extractId(x)),
        new EntityTypeMapping(EntityType.CAMPAIGN, (x: string) => UtilService.extractIdFromDruidFormat(x)),
        new EntityTypeMapping(EntityType.AD_DOMAIN, (x: string) => x.toString().toLowerCase()),
        new EntityTypeMapping(EntityType.ADVERTISER, (x: string) => UtilService.extractIdFromDruidFormat(x)),
      ],
      baseUrlService, cookieService);
  }

  ngOnInit() {
  }

  idKeyup(e): void {

  }

  ngAfterViewInit(): void {
    setTimeout(() => {
      this.route.params.subscribe(params => {
        this.selectedLevel = this.getLevelFromParam(params.demand ? params.demand : "");
        this.selectedValue = params.demandId ? params.demandId : "";
      });
    }, 1000);
  }


}
