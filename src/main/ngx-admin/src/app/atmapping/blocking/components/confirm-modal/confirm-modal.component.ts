import {Component, OnInit, ViewChild} from '@angular/core';
import {BlockingService} from '../../../../@core/data/blocking.service';
import {NgbModal, NgbModalRef} from '@ng-bootstrap/ng-bootstrap';
import {ToastingService} from '../../../../@core/utils/toaster.service';
import {GoogleAnalyticsConfig} from "../../../../@core/data/ga-configs";
import {AnalyticsService} from "../../../../@core/utils";

@Component({
  selector: 'confirm-modal',
  templateUrl: './confirm-modal.component.html',
  styleUrls: ['./confirm-modal.component.scss']
})
export class ConfirmModalComponent implements OnInit {
  private _displayList: any;
  private _supplyId: string;
  private _supply: string;
  private _demand: string;
  private _demandId: string;
  private _status: any;
  modalClose: any;
  confirmLoader: boolean = false;
  btn;
  btnTxt;
  successMessage;
  title: string;
  @ViewChild('modalContent') modal: NgbModal;
  googleAnalyticsConfigs = GoogleAnalyticsConfig;

  constructor(private blockingService: BlockingService,
              private modalService: NgbModal,
              private toastingService: ToastingService,
              private analyticsService: AnalyticsService) {
  }

  ngOnInit() {
  }


  get status(): any {
    return this._status;
  }

  set status(value: any) {
    this._status = value;
    switch (this.status) {
      case 'W':
        this.title = 'Entries to be removed';
        this.btn = 'btn-danger';
        this.btnTxt = 'Confirm';
        this.successMessage = 'Entries removed';
        break;
      case 'B':
        this.title = 'New Entries to Add';
        this.btn = 'btn-success';
        this.btnTxt = 'Confirm';
        this.successMessage = 'New blocking rules added';
        break;
      default:
        this.title = 'Err:Unknown status';
    }
  }

  get displayList(): any {
    return this._displayList;
  }

  set displayList(value: any) {
    let typeCreativeMap = value;
    this._displayList = [];
    const sizes = Object.keys(typeCreativeMap);
    sizes.forEach(size => {

      let sizeObj = {size: size, creativeList: []};
      let creatives = Object.keys(typeCreativeMap[size]);
      creatives.forEach(creative => {
        if (creative)
          sizeObj.creativeList.push({
            id: creative,
            type: typeCreativeMap[size][creative]['Template'] ? 'Template' : 'Framework',
            verify: true
          })
      });

      if (sizeObj.creativeList) {
        this.displayList.push(sizeObj);
      }
    });
  }

  get supplyId(): any {
    return this._supplyId;
  }

  set supplyId(value: any) {
    this._supplyId = value;
  }

  get supply(): string {
    return this._supply;
  }

  set supply(value: string) {
    this._supply = value;
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

  public open(): NgbModalRef {
    return this.modalService.open(this.modal, {size: 'lg'});
    
  }

  public setCloser(close: any): void {
    this.modalClose = close;
  }

  confirmRules() {
    this.analyticsService.trackEventOnGA(this.googleAnalyticsConfigs.BI_BT_CONFIRM_BLOCKING);
    if (this._displayList) {
      this.confirmLoader = true;
      this.blockingService.addNewBlocking(this.displayList,
          {
            supply: this.supply.toUpperCase(),
            supplyId: this.supplyId,
            demand: this.demand.toUpperCase(),
            demandId: this.demandId
          }, this.status)
          .then(data => {
        console.info(data);
        this.toastingService.success('Update Success', this.successMessage);
        this.modalClose(data)
      }).catch(err => {
        console.error(err);
        this.toastingService.error('Failure to ' + this.btnTxt, err.message);
      }).then(() => {
        this.confirmLoader = false;
      });
    }
  }
}
