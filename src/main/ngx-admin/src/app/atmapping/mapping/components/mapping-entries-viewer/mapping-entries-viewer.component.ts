import {AfterViewInit, Component, ElementRef, Input, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {MappingService} from '../../../../@core/data/mapping.service';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import {NgbModal, NgbModalRef} from '@ng-bootstrap/ng-bootstrap';
import {ToastingService} from '../../../../@core/utils/toaster.service';
import {CookieService} from '../../../../@core/data/cookie.service';
@Component({
  selector: 'ngx-mapping-entries-viewer',
  templateUrl: './mapping-entries-viewer.component.html',
  styleUrls: ['./mapping-entries-viewer.component.scss'],
})
export class MappingEntriesViewerComponent implements OnInit, AfterViewInit, OnDestroy {
  ngOnDestroy(): void {
      clearInterval(this.intervalButton);
  }

  ngAfterViewInit(): void {
      this.intervalButton = setInterval(() => {
          let y = this.container.nativeElement.getBoundingClientRect().top;
          this.showFloat = (y) < (window.screen.height - 200);
      }, 100);
  }

  @ViewChild('viewContainer') container: ElementRef;
  @ViewChild(MatSort) sort: MatSort;
  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild('deleteMappingsPreviewModal') previewModal: NgbModal;
  private modalRef: NgbModalRef;
  intervalButton: any;
  @Input() mappingType: string;
  @Input() supplyValue: string;
  @Input() supply: string;
  @Input() demand: string;
  @Input() demandValue: string;
  @Input() sizeList: Array<string>;
  showFloat: boolean = false;
  displayedColumns: Array<string>;
  dataSource: MatTableDataSource<any>;
  private _columns: Array<string>;
  private _data: Array<any> = [];
  private _loader: boolean = true;
  viewList: Array<any>;
  isFiltered: boolean;
  selectionAll: boolean = false;
  modalLoader: boolean = false;
  viewMessage: boolean;
  disableMappingButton: boolean = false;
  static readonly SEPARATOR: string = '$';

  constructor(private mappingService: MappingService,
              private modalService: NgbModal,
              private toastingService: ToastingService,
              private cookieService: CookieService) {
      this.viewMessage = true;
  }

  ngOnInit() {

  }

  applyFilter(filterValue: string) {

      filterValue = filterValue.trim(); // Remove whitespace
      filterValue = filterValue.toLowerCase(); // MatTableDataSource defaults to lowercase matches
      this.dataSource.filter = filterValue;

  }


  fetchData() {
      this.data = [];
      this.loader = true;
      this.disableMappingButton = false;
      this.mappingService.getMappings(this.supplyValue, this.supply, this.demandValue, this.demand, this.mappingType)
          .then(data => {
              this.data = MappingEntriesViewerComponent.process(data.mappings);
              this.dataSource = new MatTableDataSource(this.data);
              this.columns = data.fields;
              this.loader = false;
                if(data.aclStatus == 403 || data.aclStatus == 417){
                  this.disableMappingButton = true;
                  if(this.mappingType.toUpperCase() == 'MANUAL'){
                            if(data.aclStatus == 403)
                                this.toastingService.showToast("warning","Not Authorized", data.aclErrMessage);
                            else
                                this.toastingService.showToast("error","No Write Access - Authorization Data Inconsistent", data.aclErrMessage);

                  }
              }
              setTimeout(() => {
                  /**
                   * Timeout is necessary as a buffer for Views to initialize
                   */
                  this.dataSource.sort = this.sort;
                  this.dataSource.paginator = this.paginator;
              }, 500);
          }).catch(err => {
              throw err;
      });
  }


  get columns(): Array<string> {
      return this._columns;
  }

  set columns(value: Array<string>) {
      this.displayedColumns = ['preview'].concat(value).concat(['selection']);
      this._columns = value;
  }

  get loader(): boolean {
      return this._loader;
  }

  set loader(value: boolean) {
      this._loader = value;
  }

  get data() {
      return this._data;
  }

  set data(value) {
      this._data = value;
  }

  modal(filtered: boolean): void {
      this.isFiltered = filtered;
      this.viewList = filtered ? this.getTablePreviewData() : this.getConfirmDeletePreview();
      this.viewList = this.viewList.filter(item => item.templates.length > 0);
      this.modalRef = this.modalService.open(this.previewModal, {   size: 'lg'});
  }

  getTablePreviewData(): Array<any> {
      let templateArray = this.data.filter(element => element.selection);
      if (templateArray.length == 0) templateArray = this.data;
      let iterable = [];
      this.sizeList.forEach(sizeName => {
          let sizeObj = {name: sizeName, templates: []};
          let templates = templateArray.filter(template => template.templateSize == sizeName);
          if (templates.length > 0) {
              sizeObj.templates = templates;
              iterable.push(sizeObj);
          }
      });
      return iterable;
  }

  getConfirmDeletePreview(): Array<any> {
      let templateArray = this.data;
      let iterable = [];
      this.sizeList.forEach(sizeName => {
          let sizeObj = {name: sizeName, templates: []};
          let templates = templateArray.filter(template => (template.templateSize == sizeName) && template.selection);
          if (templates.length) {
              sizeObj.templates = templates;
              iterable.push(sizeObj);
          }
      });
      return iterable;
  }

  confirmDelete(): void {
      this.modalLoader = true;
      let items = this.data
          .filter(item => item.selection)
          .map(item => {
              let temp = {
                  templateId: item.templateId.trim(),
                  templateSize: item.templateSize.trim(),
              };

              if (this.mappingType == 'SEASONAL') {
                  temp['startDate'] = item.startDate;
                  temp['endDate'] = item.endDate;
              }
              return temp;
          });

      let payload = {
          templates: items,
          mappingType: this.mappingType,
          hierarchyLevel: {
              supply: this.supply.toUpperCase(),
              supplyId: this.supplyValue,
              demand: this.demand.toUpperCase(),
              demandId: this.demandValue
          },
          buSelected: this.cookieService.getBUSelectedFromCookie()
      };

      this.mappingService.deleteMappings(this.mappingType, payload).then(resp => {
          console.info("Delete OK", resp);
          this.fetchData();
      }).catch(err => {
          this.toastingService.error(err.error.response.name, err.error.response.reason);
      }).then(() => {
          this.modalLoader = false;
          this.modalRef.close();
      });
  }

  selectAll(value): void {
      let templateArray = this.dataSource.filteredData;
      templateArray.forEach(element => {
          element.selection = value;
      });
  }

  static process(data: Array<any>): Array<any> {
      data.forEach(item => {
          item.selection = false;
          if (item.startDate) {
              item.startDate = item.startDate.split(" ")[0];
          }

          if (item.endDate) {
              item.endDate = item.endDate.split(" ")[0];
          }

      });
      return data;
  }

  showReminder(): void {
      if (this.viewMessage)
          this.toastingService.info("Dont forget to Apply changes", "You have selected template(s) for deletion, Clicking the dustbin at the bottom will take you to confirm changes");
      this.viewMessage = false;
  }

  isDualEntity(val: string): boolean {
      if (val) {
          return val.split(MappingEntriesViewerComponent.SEPARATOR).length > 1;
      }
      return false;
  }

  valueBeforeSeparator(val: string): string {
      if (val) {
          return val.split(MappingEntriesViewerComponent.SEPARATOR)[0];
      }
      return "";
  }

  valueAfterSeparator(val: string): string {
      if (this.isDualEntity(val)) {
          return val.split(MappingEntriesViewerComponent.SEPARATOR)[1];
      }
      return "";
  }
}

