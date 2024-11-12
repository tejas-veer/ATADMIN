import {Component, ElementRef, Input, OnInit, ViewChild} from '@angular/core';
import {NgbModal, NgbModalRef} from '@ng-bootstrap/ng-bootstrap';
import {ngxCsv} from 'ngx-csv/ngx-csv';
import {Papa} from 'ngx-papaparse';
import {ConfigFetchService} from '../../../@core/data/configfetch.service';
import {ToastingService} from '../../../@core/utils/toaster.service';
import {MappingService} from '../../../@core/data/mapping.service';
import {CookieService} from "../../../@core/data/cookie.service";
import {SampleFileService} from '../../../@core/data/sample-file.service';
import {ThemeColorService} from '../../../@core/data/theme-color.service';
import {ReportingCsvService} from "../../../@core/data/reporting-csv.service";
import {BlockingCsvService} from "../../../@core/data/blocking-csv.service";
import {MappingCsvService} from "../../../@core/data/mapping-csv.service";
import {CmReportingService} from "../../../@core/data/cm-reporting.service";

@Component({
    selector: 'modal-page',
    templateUrl: './modal-page.component.html',
    styleUrls: ['./modal-page.component.scss']
})
export class ModalPageComponent implements OnInit {

    @Input() sector: String = 'Mapping';
    @ViewChild('bulkModal') bulkModal: NgbModal;
    @ViewChild('fileInput') fileInput: ElementRef;
    fileName;
    fileSelected: boolean = false;
    isProcessing: boolean = false;
    modalRef: NgbModalRef;
    themeColor: any;
    onClose: any;
    payload: any = {};
    finalData: any;
    STATUS_CODE_SUCCESS: any = 200;
    STATUS_CODE_FAILURE: any = 406;

    constructor(private modalService: NgbModal,
                private parseService: Papa,
                private configService: ConfigFetchService,
                private toastingService: ToastingService,
                private mappingService: MappingService,
                private cookieService: CookieService,
                private sampleFileService: SampleFileService,
                private themeColorService: ThemeColorService,
                private reportingCsvService: ReportingCsvService,
                private cmReportingService: CmReportingService,
                private blockingCsvService: BlockingCsvService,
                private mappingCsvService: MappingCsvService) {
    }

    ngOnInit() {
        this.themeColor = this.themeColorService.getThemeColor(this.cookieService.getBUSelectedFromCookie());
    }

    openModal(onClose): void {
        this.toastingService.clearToaster();
        this.clearData();
        this.modalRef = this.modalService.open(this.bulkModal);
        this.onClose = onClose;
    }

    clearData(): void {
        this.payload = [];
        this.fileSelected = false;
    }

    mappingSampleCsv(): void {
        new ngxCsv(this.sampleFileService.getSampleMappingCsv(), 'SampleManualTemplateToMap', {headers: this.mappingCsvService.validMappingHeaders});
    }

    unmappingSampleCsv(): void {
        new ngxCsv(this.sampleFileService.getSampleMappingCsv(), 'SampleManualTemplateToUnMap', {headers: this.mappingCsvService.validMappingHeaders});
    }

    blockingSampleCsv(): void {
        new ngxCsv(this.sampleFileService.getSampleBlockingCsv(), 'SampleManualTemplateToBlock', {headers: this.blockingCsvService.validBlockingHeaders});
    }

    fetchCsvData(csv: any): any {
        let rows = [];
        let inputRows = csv.split("\n");
        for (let i = 0; i < inputRows.length; i++) {
            if (inputRows[i].length != 0) {
                rows.push(inputRows[i]);
            }
        }
        let data = [];
        rows.forEach(row => {
            let rowData = row.split(',');
            let fData = [];
            rowData.forEach(rd => {
                fData.push(rd.replaceAll('\"', ''));
            });
            let lastEntry = fData[fData.length - 1];
            let split = lastEntry.split(/\r/g);
            fData[fData.length - 1] = split[0];
            data.push(fData);
        });
        let finalData = [];
        for (let i = 0; i < data.length; i++) {
            finalData.push(data[i]);
        }
        return finalData;
    }

    UploadCsv(file: any): void {
        this.clearData();
        if (file.name.toLowerCase().indexOf('.csv') < 0) {
            this.toastingService.error('Wrong File Type', 'The File is not CSV');
            return;
        }

        if (Math.round((file.size) / (1024 * 1024)) > 1) {
            this.toastingService.error('File Size Exceeded', 'The CSV file should be less than 1MB');
            return;
        }

        this.fileName = file.name;
        this.fileSelected = true;

        const promise = new Promise(resolve => {
            const reader: FileReader = new FileReader();
            reader.onload = () => {
                resolve(reader.result);
            };
            reader.readAsText(file);
        });

        promise.then(
            result => {
                let data;
                let csv = result;
                this.finalData = this.fetchCsvData(csv);
                if (this.sector.toUpperCase() == 'MAPPING') {
                    data = this.mappingCsvService.getMappingPayload(this.finalData);
                } else if (this.sector.toUpperCase() == 'BLOCKING') {
                    data = this.blockingCsvService.getBlockingPayload(this.finalData);
                } else if (this.sector.toUpperCase() == 'UNMAPPING') {
                    data = this.mappingCsvService.getMappingPayload(this.finalData);
                } else {
                    if (this.cookieService.getBUSelectedFromCookie() == 'MAX') {
                        data = this.reportingCsvService.getReportingData(this.finalData, true);
                    }else{
                        data = this.cmReportingService.getReportingCSVData(this.finalData);
                    }
                    if (data === undefined || data === null)
                        this.modalRef.close();
                }
                this.payload = {
                    buSelected: this.cookieService.getBUSelectedFromCookie(),
                    inputPayload: data,
                };
            },

            reject => {
                console.log("ERROR: ", reject);
            }
        )
    }

    processTemplateRequest(requestType: string, promise : Promise<any>): void {
        this.isProcessing = true;
        promise.then(data => {
            if (data.statusCode === this.STATUS_CODE_SUCCESS) {
                this.toastingService.success('Request ID :: ' + data.requestId, `${requestType} Request Added Successfully`);
            } else {
                if (data.failedAcl.length === 0) {
                    this.toastingService.error(`${requestType} Failed`, data.errorMessage);
                } else {
                    this.toastingService.error('Authorization Error', data.failedAcl);
                }
            }
        }).catch(err => {
            let errorToast = {
                msg: err.error.response.reason.replace("java.lang.Exception: ", ""),
            };
            this.toastingService.error(`${requestType} Error`, JSON.stringify(errorToast));
        }).then(() => {
            this.modalRef.close();
            this.isProcessing = false;
        });
    }

    AddMapping(): void {
        this.processTemplateRequest("Mapping", this.mappingService.insertBulkMapping(this.payload));
    }

    AddUnmapping(): void {
        this.processTemplateRequest("Unmapping", this.mappingService.insertBulkUnMapping(this.payload));
    }

    AddBlocking(): void {
        this.processTemplateRequest("Blocking", this.mappingService.insertBulkBlocking(this.payload));
    }

    showMetricsModal(): void {
        this.reportingCsvService.sendData(this.payload);
        this.toastingService.success('File valid', 'Generating Report...');
        this.modalRef.close();
    }

    isSumbitButtonDisabled() : boolean{
        if (this.payload.hasOwnProperty('inputPayload')) {
            return this.payload.inputPayload?.length === 0;
        }
        return true;
    }
}
