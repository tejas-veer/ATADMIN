import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CsvService } from '../../../@core/data/csv.service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { AutoAssetService } from '../../../@core/data/auto_asset/auto-asset.service';
import { ngxCsv } from 'ngx-csv';
import { AutoAssetConstantsService } from '../../../@core/data/auto-asset-constants.service';
import { ToastingService } from '../../../@core/utils/toaster.service';
import { UtilService } from '../../../@core/utils/util.service';

@Component({
  selector: 'bulk-upload-modal',
  templateUrl: './bulk-upload-modal.component.html',
  styleUrls: ['./bulk-upload-modal.component.css']
})
export class BulkUploadModalComponent implements OnInit {

  @Input() csvColumns: any;
  @Input() sampleCsvData: any;
  @Input() sampleCsvColumns: any;
  @Input() parentComponentName: any;
  @Input() modalHeader: any;
  @Input() modalSaveButtonName: any;
  @Output() generateEvent = new EventEmitter<any>();

  MAX_CSV_SIZE = 1024 * 1024;
  MAX_CSV_ROW = 500;
  csvFile: File;
  parsingMsg: string;

  parsedBulkData: any[];

  constructor(private csvService: CsvService,
    public autoAssetService: AutoAssetService,
    public configConstantService: AutoAssetConstantsService,
    private toastingService: ToastingService,
    private utilService : UtilService,
    public modal: NgbActiveModal) { }

  ngOnInit(): void {
  }

  generateBulk() {
    if (!this.utilService.isSet(this.parsingMsg)) {
      this.generateEvent.emit(this.parsedBulkData);
      this.modal.close();
    }
  }

  handleCsvUpload(event) {
    this.csvFile = event.target.files[0];
    this.checkCSV();
  }

  getCsvFileSizeMB() {
    if (this.csvFile) {
      return (this.csvFile.size / (1024 * 1024)).toFixed(2);
    }
  }

  async checkCSV(): Promise<void> {
    if (!this.csvFile) {
      this.parsingMsg = 'Please select a CSV file.';
      return;
    }

    try {
      this.parsedBulkData = await this.csvService.parseCSV(this.csvFile);
      this.parsingMsg = this.checkMandatoryFields(this.csvColumns);
      if (this.parsingMsg) {
        return;
      }
      if (this.parsedBulkData.length > this.MAX_CSV_ROW) {
        this.parsingMsg = `Number of rows exceeds the <strong>${this.MAX_CSV_ROW}</strong> limit`;
        return;
      }
      else if (this.modalHeader == "Bulk Title Generation" && !this.isValid(this.parsedBulkData)) {
        this.parsingMsg = "Check if 'demand_kwd' is empty for any row. If the row has a non-empty prompt, verify whether '{{DEMAND_KEYWORD}}' is present in the prompt.";
        return;
      }
    } catch (error) {
      this.parsingMsg = 'Error while parsing the CSV.';
    }
  }

  checkMandatoryFields(columns) {
    return this.csvService.checkMandatoryFields(this.parsedBulkData, columns);
  }

  isValid(data) {
    return data.every(obj => {
      return obj && obj.hasOwnProperty('demand_keyword') && obj.demand_keyword && ((obj.hasOwnProperty('prompt') && obj.prompt.length <= 0) ||(obj.hasOwnProperty('prompt') && obj.prompt && obj.prompt.includes("{{DEMAND_KEYWORD")));
    })
  }

  downloadSampleCsv() {
    const csvName = this.modalHeader + '_Sample_Csv_Upload_Data';
    const data = this.sampleCsvData;
    const headers = this.sampleCsvColumns;
    new ngxCsv(data, csvName, { headers: headers });
  }

  getFields() {
    return this.csvColumns.join(', ');
  }

  resetParsingMsg() {
    this.parsingMsg = '';
  }

  resetCsvFile() {
    this.csvFile = null;
  }

  resetSelectedFile() {
    this.resetCsvFile();
    this.resetParsingMsg();
  }


}
