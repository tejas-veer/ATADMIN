import {Component, OnInit, ViewChild} from '@angular/core';
import {Subject} from 'rxjs/Subject';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {BlockingService} from '../../../../../../@core/data/blocking.service';
import {ActivatedRoute} from '@angular/router';
import {ConfirmModalComponent} from '../../../confirm-modal/confirm-modal.component';
import {GoogleAnalyticsConfig} from "../../../../../../@core/data/ga-configs";
import {AnalyticsService} from "../../../../../../@core/utils";


@Component({
  selector: 'blocking-rules',
  templateUrl: './blocking-rules.component.html',
  styleUrls: ['./blocking-rules.component.scss']
})
export class BlockingRulesComponent implements OnInit {
  displayList: any[];
  @ViewChild('confirmModal') confirmModal: ConfirmModalComponent;
  sizeList: any;
  ruleList: Array<NewBlockingRule> = [];
  private _supply: string;
  private _supplyId: string;
  private _demand: string;
  private _demandId: string;
  public maxitems: any;
  saveEvent: Subject<any>;
  googleAnalyticsConfigs = GoogleAnalyticsConfig;

  constructor(private blockingService: BlockingService,
              private route: ActivatedRoute,
              private analyticsService: AnalyticsService) {
    this.route.params.subscribe(params => {
      this.supply = params.supply ? params.supply.toUpperCase() : "";
      this.supplyId = params.supplyId ? params.supplyId : "";
      this.demand = params.demand ? params.demand.toUpperCase() : "";
      this.demandId = params.demandId ? params.demandId: "";
    })
  }


  ngOnInit() {
  }

  public addRule(idsToBlock: string, type: string, sizes: Array<string>) {
    this.ruleList.push({idsToBlock: idsToBlock, type: type, sizes: sizes});
  }

  deleteRule(rule: NewBlockingRule) {
    const index: number = this.ruleList.indexOf(rule);
    this.ruleList.splice(index, 1);
  }

  tagAdded(i, type) {
    if (type == 'Framework') {
      if (this.ruleList[i].sizes.filter(item => item['value'] === 'ALL').length > 0) {
        this.ruleList[i].sizes = [{value: 'ALL', display: 'ALL'}];
      }
    }
  }

  public _setSizeList(sizeList: Array<string>) {
    this.sizeList = sizeList;
  }


  set supply(value: string) {
    this._supply = value;
  }


  get supply(): string {
    return this._supply;
  }


  get supplyId(): string {
    return this._supplyId;
  }

  set supplyId(value: string) {
    this._supplyId = value;
  }

  get demandId(): string {
    return this._demandId;
  }

  set demandId(value: string) {
    this._demandId = value;
  }
  get demand(): string {
    return this._demand;
  }

  set demand(value: string) {
    this._demand = value;
  }

  public _setMaxItems(maxitems: any) {
    this.maxitems = maxitems;
  }

  public _getSaveEvent(): Subject<any> {
    if (!this.saveEvent) {
      this.saveEvent = new Subject<any>();
    }
    return this.saveEvent;
  }

  save(): void {
    this.analyticsService.trackEventOnGA(this.googleAnalyticsConfigs.BI_BT_SAVE_BLOCKING_CHANGES);
    this.confirmModal.supply = this.supply;
    this.confirmModal.supplyId = this.supplyId;
    this.confirmModal.demand = this.demand;
    this.confirmModal.demandId = this.demandId;
    this.confirmModal.displayList = this.getDisplayMap();
    this.confirmModal.open().result.then(data => {
      this.ruleList = [];
      this.saveEvent.next(data);
    }).catch(err => {
      throw err;
    });
    this.confirmModal.status = 'B';
  }

  reset(): void {
    this.ruleList = [];
  }

  getDisplayMap() {
    let typeCreativeMap = {};
    this.ruleList.forEach(rule => {
      rule.sizes.forEach(size => {
        if (!typeCreativeMap[size]) {
          typeCreativeMap[size] = {};
        }
        rule.idsToBlock.replace(/\s/gi, '').split(',').forEach(creative => {
          if (!typeCreativeMap[size][creative]) {
            typeCreativeMap[size][creative] = {};
          }
          typeCreativeMap[size][creative][rule.type] = true;
        })
      })
    });
    return typeCreativeMap;
  }
}

export interface NewBlockingRule {
  idsToBlock: string;
  type: string;
  sizes: Array<any>;
}
