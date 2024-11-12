import {AfterViewInit, Component, Input, OnInit, ViewChild} from '@angular/core';
import {LocalDataSource} from 'ng2-smart-table';
import {ActivatedRoute} from '@angular/router';
import {BlockingService} from '../../../../@core/data/blocking.service';
import {BlockingStatusService} from '../../service/blocking-status.service';
import {BlockingSwitchComponent} from '../blocking-switch/blocking-switch.component';
import {PreviewrenderComponent} from '../previewrender/previewrender.component';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {ToastingService} from '../../../../@core/utils/toaster.service';
import {CookieService} from '../../../../@core/data/cookie.service';

@Component({
  selector: 'ngx-blockingtable',
  templateUrl: './blockingtable.component.html',
  styleUrls: ['./blockingtable.component.scss'],

})
export class BlockingtableComponent implements OnInit, AfterViewInit {

  @Input() type: string;
  @ViewChild('previewModal') previewModal: NgbModal;
  blockingData: LocalDataSource;
  settings: any;
  rawData: any;
  closeModal: any;
  blockingLoader: boolean = true;
  initialBlockingStatus: any;
  displayList: any;
  supplyId: string;
  supply: string;
  demand: string;
  demandId: string;
  view_init: any = false;
  disableBlockingButton: boolean = false;
  leng:Number = 0;


  ngAfterViewInit(): void {
    this.view_init = true;
    this.getAjaxData();
  }

  constructor(private route: ActivatedRoute,
    private blockingService: BlockingService,
    blockingStatus: BlockingStatusService,
    private modalService: NgbModal,
    private toastingService: ToastingService,
    private cookieService: CookieService) {

    this.route.params.subscribe(params => {
      this.supplyId = params.supplyId;
      this.supply = params.supply;
      this.demand = params.demand;
      this.demandId = params.demandId;
      if (this.view_init)
        this.getAjaxData();
    });

    blockingStatus.event.subscribe(data => {
      if (data.type == this.type) {
        let initial_row = data.tuple;
        this.blockingData.update(initial_row, { Status: data.status });
      }
    });
  }

  public getChangedData(): Promise<any> {
    let blockingData = Object.assign([], this.rawData);
    let changedData = [];
    blockingData.forEach(item => {
      item.type = this.type;
      if (this.initialBlockingStatus[item[this.type]][item['Template Size']] !== item.Status) {
        changedData.push(this.neutralize(item));
      }
    });
    return Promise.resolve(changedData);
  }

  private getAjaxData() {
    this.blockingLoader = true;
    this.disableBlockingButton = false;
    this.blockingService.getBlockingTable(this.supplyId, this.supply, this.demandId, this.demand, this.type).then(data => {
      this.rawData = data.data;
      this.makeDisplayList();
      if(data.aclStatus == 403 || data.aclStatus == 417){
        this.disableBlockingButton = true;
        if (this.type.toUpperCase() == 'TEMPLATE') {
            if(data.aclStatus == 403)
              this.toastingService.showToast("warning","Not Authorized", data.aclErrMessage);
            else
              this.toastingService.showToast("error","No Write Access - Authorization Data Inconsistent", data.aclErrMessage);
        }
      }

      this.initialBlockingStatus = {};
      data.data.forEach(row => {
        const id = row[this.type];
        const size = row['Template Size'];
        if (!this.initialBlockingStatus[id]) {
          this.initialBlockingStatus[id] = {};
        }
        this.initialBlockingStatus[id][size] = row['Status'];
      });
      this.blockingData = new LocalDataSource(data.data);
      this.leng = Object.values(this.blockingData.getAll())[1].length;
      const meta = data.meta;
      this.settings = {
        columns: {}, actions: false
      };

      meta.forEach(item => {
        if (item.name == this.type)
          this.settings.columns[item.name] = {
            title: item.name,
            filter: false,
            type: 'custom',
            renderComponent: PreviewrenderComponent
          };
        else {
          this.settings.columns[item.name] = { title: item.name, filter: false };
        }
      });

      if (!this.disableBlockingButton) {
        this.settings.columns['Status'] = {
          title: 'Delete',
          filter: false,
          type: 'custom',
          renderComponent: BlockingSwitchComponent,
        }
      }

    }).then(() => {
      this.blockingLoader = false;
    });

  }

  getSelectList(data, id) {
    let x = {};
    data.forEach(item => {
      x[item[id]] = 1;
    });
    let keys = Object.keys(x);
    let list = [];
    keys.forEach(item => {
      list.push({ value: item, title: item });
    });
    return list;
  };

  neutralize(item: any): any {
    let f_item = Object.assign({}, item);
    f_item.creative = f_item[this.type];
    delete f_item[this.type];
    return f_item;
  }

  modalUpdates(row, status): void {
    this.blockingData.update(row, { Status: status });
  }

  setCloser(c: any) {
    this.closeModal = c;
  }

  refresh(): void {
    this.getAjaxData();
  }

  preview(): void {
    this.modalService.open(this.previewModal, { size: 'lg' });
  }

  makeDisplayList(): void {
    let sizeMap = {};
    this.rawData.forEach(item => {
      const size = item['Template Size'];
      const creative = item[this.type];
      if (!sizeMap[size]) {
        sizeMap[size] = {};
      }
      sizeMap[size][creative] = item;
    });

    this.displayList = [];
    const sizes = Object.keys(sizeMap);
    sizes.forEach(item => {
      let sizeObj = { name: item, creatives: [] };
      Object.keys(sizeMap[item]).forEach(creative => {
        sizeObj.creatives.push(sizeMap[item][creative]);
      });
      if (sizeObj.creatives.length > 0)
        this.displayList.push(sizeObj);
    });
  }

  onSearch(query: string = ''){
    if(query.length > 0){
      this.blockingData.setFilter([
        {
          field: 'Template',
          search: query,
        },
        {
          field: 'Template Size',
          search: query,
        },
        {
          field: 'Framework',
          search: query,
        },
        {
          field: 'Admin Name',
          search: query,
        },
        {
          field: 'Date Added',
          search: query,
        },
          ],false);}
    else{
      this.blockingData.reset();
    }
  }

  ngOnInit() {
  }

}
