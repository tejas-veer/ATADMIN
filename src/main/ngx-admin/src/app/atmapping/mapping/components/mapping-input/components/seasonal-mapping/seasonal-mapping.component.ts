import {AfterViewInit, Component, Input, OnChanges, OnInit, SimpleChanges, ViewChild} from '@angular/core';
import {isNumeric} from "rxjs/util/isNumeric";
import {ConfirmMappingModalComponent} from "../confirm-mapping-modal/confirm-mapping-modal.component";
import { ErrorStackViewerComponent } from '../../../../../../@theme/components/error-stack-viewer/error-stack-viewer.component';
import {MappingTableComponent} from "../mapping-table/mapping-table.component";
import {MappingEntriesViewerComponent} from "../../../mapping-entries-viewer/mapping-entries-viewer.component";
import * as XLSX from "xlsx";
@Component({
  selector: 'ngx-seasonal-mapping',
  templateUrl: './seasonal-mapping.component.html',
  styleUrls: ['./seasonal-mapping.component.scss'],
  
})
export class SeasonalMappingComponent implements OnInit, AfterViewInit, OnChanges {

  leng: Number;
  @Input() sizeList;
  @Input() supply: string;
  @Input() supplyId: string;
  @Input() demand: string;
  @Input() demandId: string;
  @Input() entriesViewer: MappingEntriesViewerComponent;

  @ViewChild('seasonalMappingInputTable') inputViewer: MappingTableComponent;
  @ViewChild('errorViewer') errorStack: ErrorStackViewerComponent;
  @ViewChild('confirmSeasonalMappingModal') confirmModal: ConfirmMappingModalComponent;
  @ViewChild('fileInput') fileInput;
  acSizeList = [];
  templatesList: string = "";
  selectedSizes: Array<any> = [];
  startDate: any = null;
  endDate: any = null;
  displayedColumns: Array<string> = ['Templates', 'Sizes', 'Start Date', 'Expiry Date', 'Action'];

  constructor() {
  }

  ngOnChanges(changes: SimpleChanges): void {
      if (changes.sizeList) {
          if (this.sizeList)
              this.acSizeList = this.sizeList;
      }
  }

  ngAfterViewInit(): void {
      this.inputViewer.columns = ["Start Date", "Expiry Date"];
      this.inputViewer.sizeList = this.sizeList;
      this.entriesViewer.fetchData();
  }

  addMapping() {
      if (!this.validate())
          return;
      let sizes = Object.assign([], this.selectedSizes);
      if (sizes.filter(item => item.value == 'ALL').length > 0) {
          sizes = [];
          this.sizeList.forEach(size => {
              sizes.push({display: size, value: size});
          })
      }
      const item = {
          Templates: this.templatesList,
          Sizes: sizes,
          startDate: this.startDate,
          endDate: this.endDate
      };
      this.inputViewer.add(item);
      this.leng = this.inputViewer.data.length;
      this.selectedSizes = [];
      this.templatesList = "";
      this.startDate = null;
      this.endDate = null;
  }

  saveData(): void {
      this.confirmModal.data = this.inputViewer.data;
      this.confirmModal.open(status => {
          if (status) {
              this.reset();
          }
      });
  }

  reset(): void {
      this.inputViewer.clearData();
      this.entriesViewer.fetchData();
  }

  ngOnInit() {

  }

  validate = () => this.validateParams(this.templatesList, this.selectedSizes, this.startDate, this.endDate);

  validateParams(templatesList, selectedSizes, startDate, endDate, append = ""): boolean {
      this.errorStack.clear();

      if (templatesList.length == 0) {
          this.errorStack.pushError({
              title: "No Templates Selected",
              message: "Please enter Comma Separated Templates to the Input" + append,
              type: "error"
          });
          return false;
      }

      if (selectedSizes.length == 0) {
          this.errorStack.pushError({
              title: "No Sizes Selected",
              message: "Please Select a size to add the Mapping" + append,
              type: "error"
          });
          return false;
      }

      if (!startDate) {
          this.errorStack.pushError({
              title: "No Start Date Selected",
              message: "Please Select a Start Date to add the Mapping" + append,
              type: "error"
          });
          return false;
      }

      if (!endDate) {
          this.errorStack.pushError({
              title: "No Expiry Date Selected",
              message: "Please Select a Expiry Date to add the Mapping" + append,
              type: "error"
          });
          return false;
      }

      if (new Date(startDate).getTime() > new Date(endDate).getTime()) {
          this.errorStack.pushError({
              title: "Invalid Date Selection",
              message: "Start Date must be before expiry date" + append,
              type: "error"
          });
          return false;
      }

      let numeric = templatesList.replace(/\s*,\s*/g, ",").split(",").filter(template => template.length > 0).reduce((previous, current) => previous && isNumeric(current.trim()), true);

      if (!numeric) {
          this.errorStack.pushError({
              title: "Invalid Templates",
              message: "Templates must be comma separated numbers" + append,
              type: "error"
          });
          return false;
      }
      return true;
  }

}

