import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {ConfigFetchService} from "../../../../@core/data/configfetch.service";
import {ManualMappingComponent} from "./components/manual-mapping/manual-mapping.component";
import {SeasonalMappingComponent} from "./components/seasonal-mapping/seasonal-mapping.component";
import {CookieService} from "../../../../@core/data/cookie.service";

@Component({
  selector: 'ngx-mapping-input',
  templateUrl: './mapping-input.component.html',
  styleUrls: ['./mapping-input.component.scss']
})
export class MappingInputComponent implements OnInit, AfterViewInit {

  @ViewChild('manualMappingInput') manualMappingComponent: ManualMappingComponent;
  @ViewChild('seasonalMappingInput') seasonalMappingComponent: SeasonalMappingComponent;
  sizeList;
  supplyValue: string;
  supply: string;
  demandValue: string;
  demand: string;
  pendingReset: boolean = false;

  ngAfterViewInit(): void {
      if (this.pendingReset) {
          this.reset();
          this.pendingReset = false;
      }
  }


  constructor(private configService: ConfigFetchService,
              private activatedRoute: ActivatedRoute,
              private cookieService: CookieService) {

      this.configService.getValidSizes().then(data => {
          this.sizeList = ['ALL', ...data];
      });

      this.activatedRoute.params.subscribe(params => {
          this.supplyValue = params.supplyId;
          this.supply = params.supply;
          this.demand = params.demand;
          this.demandValue = params.demandId;
          if (this.seasonalMappingComponent && this.manualMappingComponent) {
              this.reset();
          } else {
              this.pendingReset = true;
          }
      })
  }

  ngOnInit() {
  }

  private reset() {

      setTimeout(() => {
          /**
           * Buffer needed for input flow
           */
          this.manualMappingComponent.reset();
          this.seasonalMappingComponent.reset();
      }, 500);

  }
}
