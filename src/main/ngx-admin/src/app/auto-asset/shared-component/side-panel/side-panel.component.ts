import { Component, EventEmitter, Input, OnInit, Output, SimpleChanges } from '@angular/core';
import { AutoAssetService } from '../../../@core/data/auto_asset/auto-asset.service';
import { ToastingService } from '../../../@core/utils/toaster.service';
import { ConversionMapService } from '../../../@core/data/conversion-map.service';
import { AutoAssetConstantsService } from '../../../@core/data/auto-asset-constants.service';
import { NgbModal, NgbProgressbarConfig } from '@ng-bootstrap/ng-bootstrap';
import { ActivatedRoute, Router } from '@angular/router';
import { catchError, switchMap, takeUntil, takeWhile } from 'rxjs/operators';
import { EMPTY, Subject, Subscription, timer } from 'rxjs';
import { Clipboard } from '@angular/cdk/clipboard';
import { ngxCsv } from 'ngx-csv';
import { HttpParams } from '@angular/common/http';
import { BulkUploadService as BulkUploadService } from '../../../@core/data/auto_asset/bulk-upload.service';


@Component({
  selector: 'side-panel',
  templateUrl: './side-panel.component.html',
  styleUrls: ['./side-panel.component.css']
})
export class SidePanelComponent implements OnInit {
  @Input() showSidePanel: any;
  @Output() closeSidePanel: EventEmitter<any> = new EventEmitter<any>();
  @Output() viewAsset: EventEmitter<any> = new EventEmitter<any>();

  private destroy$ = new Subject<void>();
  private aaRequestIntervalTimer: Subscription | null = null;

  requestDetailsList = [];
  csvDataToDownload: any;
  requestIdInput: any;

  loader = {
    initialLoader: 1,
    requestDetailsLoader: false,
    downloadLoader: false,
    refreshLoader: false
  }

  pageData: any[] = [];
  totalPaginationItems = 0;
  pageSize = 10;
  currentPage = 0;
  requestOffset = 0;
  downloadLoader: boolean;

  toFetchRequestTypes = ["TITLE_GENERATION"]

  constructor(public autoAssetService: AutoAssetService,
    public modalService: NgbModal,
    private bulkUploadService: BulkUploadService,
    private toastingService: ToastingService,
    private configConstantService: AutoAssetConstantsService,
    config: NgbProgressbarConfig,
    private route: ActivatedRoute,
    private clipboard: Clipboard,
    private router: Router
  ) { }

  ngOnInit(): void {
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
  

  ngOnChanges(changes: SimpleChanges) {
    if (changes.showSidePanel && changes.showSidePanel.currentValue === true) {
      this.viewRequestDetails();
    }
  }

  closeSidePanelEvent() {
    this.closeSidePanel.emit();
  }

  resetRequestIdInput() {
    this.requestIdInput = null;
  }

  getBaseUrl() {
    const currentUrl = window.location.href;
    const segments = currentUrl.split('/#/');
    let url = segments.length > 1 ? segments[0] : currentUrl;
    if (segments.length > 1) {
      url += "/#/" + segments[1].split('?')[0];
    }  
    return url;
  }

  generateAndCopySidebarRequestDetailsUrl(requestId) {
    const baseUrl = this.getBaseUrl();
    const params = new HttpParams()
      .set('request_id', encodeURIComponent(requestId));

    const sharableLink = this.constructUrl(baseUrl, params);
    this.copyToClipboard(sharableLink);
  }


  constructUrl(baseUrl: string, params: HttpParams) {
    const paramsUrl = params.toString();
    const url = baseUrl + (paramsUrl ? '?' + paramsUrl : '');
    return url;
  }

  copyToClipboard(content): void {
    this.clipboard.copy(content);
    this.toastingService.success("URL copied", "URL successfully copied");
  }

  restartGetAARequestIntervalTimer() {
    if (this.aaRequestIntervalTimer) {
      this.aaRequestIntervalTimer.unsubscribe();
    }

    this.aaRequestIntervalTimer = timer(this.configConstantService.AA_GET_REQUEST_DETAILS_INTERVAL, this.configConstantService.AA_GET_REQUEST_DETAILS_INTERVAL).pipe(
      switchMap(() => this.autoAssetService.getAARequest(this.pageSize, this.requestOffset,this.toFetchRequestTypes).pipe(
        catchError(error => {
          console.error('Error occurred:', error);
          return EMPTY;
        })
      )),
      takeWhile(() => this.isAARequestProcessing()),
      takeUntil(this.destroy$)
    ).subscribe(
      (response: any) => {
        this.requestDetailsList = response;
        this.handleAARequestTotalCount(response);
      },
      (error: any) => {
        console.error('Error occurred in interval subscription:', error);
      }
    );
  }

  getRequestDetails() {
    this.loader.initialLoader -= 1;
    this.autoAssetService.getAARequest(this.pageSize, this.requestOffset,this.toFetchRequestTypes )
      .subscribe(
        (response) => {
          this.requestDetailsList = response;
          this.handleAARequestTotalCount(response);
        },
        (error) => {
          this.toastingService.error("Fetch Failed", `Failed to fetch request details`)
          this.loader.refreshLoader = false;
          this.loader.initialLoader = -1;
        },
        () => {
          this.loader.refreshLoader = false;
          this.loader.initialLoader = -1;
        }
      );
  }

  isAARequestProcessing() {
    const hasUnprocessedOrProcessing = this.requestDetailsList.some(obj => obj.isActive == 1);
    return hasUnprocessedOrProcessing;
  }

  viewRequestDetails() {
    this.getRequestDetails();
    this.restartGetAARequestIntervalTimer();
  }

  refreshRequestDetails() {
    this.loader.refreshLoader = true;
    this.getRequestDetails();
  }

  onPageChange(event) {
    this.loader.refreshLoader = true;
    this.currentPage = event.pageIndex;
    this.requestOffset = this.currentPage * this.pageSize;
    this.getRequestDetails();
  }

  handleAARequestTotalCount(response) {
    if (Array.isArray(response) && response.length > 0 && response[0].hasOwnProperty("totalCount")) {
      this.totalPaginationItems = response[0]["totalCount"];
    } else {
      this.totalPaginationItems = 0;
    }
  }

  calculateProgressPercentageValue(request) {
    if (request.isActive != -1) {
      return ((request.rowsProcessed / request.rowsGenerated) * 100).toFixed(2);
    }
    return 0;
  }

  calculateProgressPercentageText(request) {
    if (request.isActive != -1) {
      return ((request.rowsProcessed / request.rowsGenerated) * 100).toFixed(2) + "%";
    }
    return "100%";
  }

  isRequestProcessing(requestDetails) {
    return requestDetails.isActive == 1;
  }

  addLoaders(data) {
    return data.map((item) => {
      item['downloadLoader'] = false;
      item['suspendLoader'] = false;
      return item;
    })
  }
  
  viewAssetFromRequestId(requestId) {
    this.viewAsset.emit(requestId);
  }

  processCsvData(data) {
    return data.map(obj =>
      this.bulkUploadService.validTitleGenerationAssetHeadersForDownload.reduce((acc, key) => {
        if (obj.hasOwnProperty(key)) {
          acc[key] = obj[key];
        }
        else {
          acc[key] = "";
        }
        return acc;
      }, {})
    );
  }


  async getDataForCSVDownloadFromRequestId(requestId) {
    await this.autoAssetService.getGeneratedAssetListByRequestId(requestId)
      .then((response) => {
        this.csvDataToDownload = response;
      })
      .catch((error) => {
        throw error;
      })
  }

  processAndDownloadSDData(requestId) {
    const csvNameSucceed = 'AutoAssetRequest-' + "Title_Generation_" + requestId + "(Success)";
    const csvData = this.processCsvData(this.csvDataToDownload);
    new ngxCsv(csvData, csvNameSucceed, { headers: this.bulkUploadService.validTitleGenerationAssetHeadersForDownload });
  }

  async csvDownloadFromRequestId(request) {
    try {
      request.downloadLoader = true;
      const requestId = request.requestId;
      await this.getDataForCSVDownloadFromRequestId(requestId)
        .then(() => {
          this.processAndDownloadSDData(requestId)
          request.downloadLoader = false;
        })
    }
    catch {
      request.downloadLoader = false;
      this.toastingService.error('CSV Download Failed', 'Unable to download requested CSV');
    }
  }

  async downloadCsvFromRequestIdInput(requestId) {
    if (requestId != null && requestId != "") {
      try {
        this.loader.downloadLoader = true;
        await this.getDataForCSVDownloadFromRequestId(requestId)
          .then(() => {
            this.processAndDownloadSDData(requestId)
            this.loader.downloadLoader = false;
          })
      }
      catch {
        this.loader.downloadLoader = false;
        this.toastingService.error('CSV Download Failed', 'Unable to download requested CSV');
      }
    }
  }

  suspendRequest(request) {
    request.suspendLoader = true;
    this.autoAssetService.suspendAARequest(request.requestId)
      .then((response) => {
        this.getRequestDetails();
        request.suspendLoader = false;
        this.toastingService.success("Request Cancelled", `Successfully cancelled the request with id ${request.requestId}`)
      })
      .catch(() => {
        request.suspendLoader = false;
        this.toastingService.error("Cancel Request Failed", `Unable to cancel request with id ${request.requestId}`)

      })
  }
}
