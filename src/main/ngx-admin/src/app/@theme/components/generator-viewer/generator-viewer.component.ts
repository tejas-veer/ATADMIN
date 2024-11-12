import { InsertUpdateService } from './insert-update.service';
import { Component, Input, OnChanges, SimpleChanges, ViewChild } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';
import { GeneratorService } from '../../../@core/data/generator.service';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { ToastingService } from '../../../@core/utils/toaster.service';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { HttpClient } from "@angular/common/http";
import { EntityService } from '../../../@core/data/entity-fetch.service';
@Component({
  selector: 'generator-viewer',
  templateUrl: './generator-viewer.component.html',
  styleUrls: ['./generator-viewer.component.scss']
})
export class GeneratorViewerComponent implements OnChanges {

  private modalRef: NgbModalRef;
  private useproxy: string = null;
  private reportIssueModalRef: NgbModalRef;
  whiteListUpdateLoader: boolean;


  ngOnChanges(changes: SimpleChanges): void {
    if (changes['entity']) {
      if (this.entity == null) {
        return;
      }
      console.log('changes in generator entity');
      console.log(this.entity);
      this.myInit();
    }
  }

  @Input() entity;

  @Input() size;
  loader: boolean = true;
  error: boolean = false;
  queueError: boolean = false;
  flatTemplateData: any;
  showInserted: boolean;
  showAdditional: boolean;
  showSuggested: boolean;
  maxheight = Math.floor(window.innerHeight * 0.6);
  urls: Array<any>;
  request: string;
  templateStatus: any = {};
  init: boolean = true;
  templateData: any;
  selectedTemplates: any = [];
  insertBusy: boolean = false;
  progressStatus: any;
  progressText: string;
  progressBarSmooth: any;
  whiteListTemplatesList = [];
  private nocache: boolean = false;
  private timeoutControl: any;
  private queueTimeout: any;
  errorHeader: string;
  errorText: string;
  @ViewChild("insertConfirmation") whiteListConfirmationModal: NgbModal;
  whiteListConfirmationModalRef: NgbModalRef;

  constructor(private domSan: DomSanitizer,
    private generatorService: GeneratorService,
    private modalService: NgbModal,
    private toastService: ToastingService,
    private router: Router,
    private entityService: EntityService,
    private route: ActivatedRoute,
    private insertUpdate: InsertUpdateService,
    private httpClient: HttpClient) {
    this.route.params.subscribe((params: Params) => {
      if (params['useproxy'] || params['useproxy'] === '0') {
        this.useproxy = params['useproxy'];
      }
      else {
        this.useproxy = null;
      }

      this.nocache = params['nocache'] === 'true';
    });
  }

  disable(hash): void {
    console.log(hash + ' -> disabled');
    this.templateStatus[hash] = false;
  }

  enable(hash): void {
    console.log(hash + ' -> enabled');
    this.templateStatus[hash] = true;
  }

  myInit(): void {

    if (this.timeoutControl) {
      clearTimeout(this.timeoutControl);
    }

    if (this.queueTimeout) {
      clearTimeout(this.queueTimeout);
    }
    this.showInserted = false;
    this.showAdditional = false;
    this.showSuggested = false;
    this.progressText = 'fetching URLS';
    this.progressStatus = 10;
    this.progressBarSmooth = 0;
    // this.smoothProgress(100);
    this.init = true;
    this.urls = [];
    this.templateStatus = {};
    this.templateData = [];
    this.flatTemplateData = [];
    this.queueError = false;
    console.log('hitting -> ' + this.entity.id);
    this.generatorService.createRequest(this.entity.id, this.useproxy, this.nocache).then(data => {
      this.urls = [];
      for (let i = 0; i < data.urls.length; i++) {
        this.urls.push(data.urls[i]);
      }
      this.request = JSON.parse(data.generator);
      this.request['request-id'] = this.request['request-id'].split('|')[1];
      this.progressStatus += 10;
      this.progressText = 'Polling Generator';
      this.poll(5);
    }).catch(err => {
      this.error = true;
      if (err.status == 422) {
        this.errorHeader = 'Error : No Urls Found on Druid';
        this.errorText = 'No Urls were found on Druid for the given Input , Generator cannot generate Templates';
      } else {
        this.errorHeader = 'Error ' + err.status + ' ! could not queue Templates';
        this.errorText = ' Try Refreshing the page and retry ';
      }
      this.loader = false;
    }).then(() => {
      this.init = false;
    });
  }

  initTemplateStatus(data) {
    this.templateStatus = {};
    this.flatTemplateData = [];
    for (let i = 0; i < data.length; i++) {
      for (let j = 0; j < data[i].value.length; j++) {
        let modifiedData = data[i].value[j];
        modifiedData.framework = data[i].key;
        this.flatTemplateData.push(modifiedData);
        data[i].value[j].uniqueTemplateHash = this.encodeUniqueHash(data[i].value[j].uniqueTemplateHash, modifiedData.framework);
        switch (data[i].value[j].insertionStatus) {
          case 0:
            this.templateStatus[data[i].value[j].uniqueTemplateHash] = false;
            this.showAdditional = true;
            break;
          case 2:
            this.templateStatus[data[i].value[j].uniqueTemplateHash] = true;
            this.showSuggested = true;
            break;
          default:
            this.templateStatus[data[i].value[j].uniqueTemplateHash] = null;
            this.showInserted = true;
        }
      }
    }
  }

  getTemplatePreviewUrlText(template, size): any {
    return 'http://cm.internal.reports.mn/common/preview.php?TID=' + template['defaultTemplateId'] + '&Page=keywords-only-' + size + '&isiframe=1&jsonData=' + encodeURIComponent(template['templateCustomizationJson']);
  }

  getTemplatePreviewUrl(template, size): any {
    return this.domSan.bypassSecurityTrustResourceUrl('http://cm.internal.reports.mn/common/preview.php?TID=' + template['defaultTemplateId'] + '&Page=keywords-only-' + size + '&isiframe=1&jsonData=' + encodeURIComponent(template['templateCustomizationJson']));
  }

  encodeUniqueHash(hasher: string, frameworkid: any) {
    return hasher + '|' + frameworkid;
  }

  decodeUniqueHash(hasher: string) {
    return hasher.split('|')[0];
  }

  insertTemplates() {
    console.log(this.selectedTemplates);
    this.insertBusy = true;

    this.generatorService.insert(this.entity.id, JSON.stringify(this.selectedTemplates)).then(data => {

      this.toastService.success('Creation Success', "");
      this.modalRef.close();
      data = data.json();
      if (data.needConfirmation) {
        this.confirmTemplates(data);
      } else {
        this.insertUpdate.next();
      }
    }).catch(err => {
      this.toastService.error('Error in Inserting Templates', "");
      console.error(err.toString());
    }).then(() => {
      this.insertBusy = false;
    });
  }

  confirmTemplates(data) {
    this.whiteListUpdateLoader = false;
    this.whiteListTemplatesList = [];
    data["template-list"].forEach(template => this.whiteListTemplatesList.push(template["template-id"]));
    this.whiteListConfirmationModalRef = this.modalService.open(this.whiteListConfirmationModal, {
      size: 'lg',
      backdrop: 'static',
      keyboard: false
    });
  }

  reportIssueModal(content) {
    this.whiteListConfirmationModalRef.close();
    this.reportIssueModalRef = this.modalService.open(content, { backdrop: "static", keyboard: false });
  }

  reportIssue(option, text) {
    this.generatorService.postIssue(option + "\n" + text);
    this.reportIssueModalRef.close();
    this.insertUpdate.next();
  }

  sendWhiteListUpdate() {
    this.whiteListUpdateLoader = true;
    this.entityService.updateEntity(this.entity.id, this.whiteListTemplatesList, []).then(data => {
      this.toastService.success("Templates have been Whitelisted", data.message);
      this.whiteListUpdateLoader = false;
      this.insertUpdate.next();
    }
    ).catch(err => {
      this.toastService.error("Error in whitelisting", err.message);
      console.log(err);
    }).then(() => {
      this.whiteListConfirmationModalRef.close();
    });
  }

  whiteListLater() {
    this.whiteListConfirmationModalRef.close();
    this.insertUpdate.next();
  }

  smoothProgress(dur) {
    this.timeoutControl = setTimeout(() => {
      this.progressBarSmooth += 1;
      if (this.loader) {
        this.smoothProgress(dur + 0.01 * dur);
      }
    }, dur * 10);
  }

  confirmInsert(content): void {
    let temp = [];
    this.templateData.forEach(item => {
      let tempfr = [];
      let inserts = 0;
      item.value.forEach(templ => {
        if (this.templateStatus[templ.uniqueTemplateHash]) {
          let template = Object.assign({}, templ);
          template.uniqueTemplateHash = this.decodeUniqueHash(templ.uniqueTemplateHash);
          inserts++;
          tempfr.push(template);
        }
      });
      if (inserts > 0)
        temp.push({ key: item.key, value: tempfr });
    });
    if (temp.length)
      this.selectedTemplates = temp;
    this.modalRef = this.modalService.open(content, { size: 'lg' });
  }

  poll(retry_count: number): void {
    this.loader = true;

    if (this.progressStatus < 60) {
      this.progressStatus += 10;
    } else if (this.progressStatus < 90) {
      this.progressStatus += 5;
    } else if (this.progressStatus < 95) {
      this.progressStatus += 1;
    } else if (this.progressStatus < 98) {
      this.progressStatus += 0.1;
    }

    this.generatorService.poll(this.request['request-id']).then(data => {
      console.log(data);
      this.progressText = data.status;
      if (data.status == 'QUEUED' || data.status == 'RUNNING') {
        setTimeout(() => {
          this.poll(5);
        }, 5000);
      }
      else if (data.status == 'COMPLETED') {
        let obj = JSON.parse(data.response);
        if (obj['error']) {
          this.toastService.error(data.status, JSON.stringify(obj));
          this.error = true;
          this.errorHeader = 'Oh Snap! Failure to Generate Templates';
          this.errorText = 'Please Try Again after later';
          return;
        }
        this.queueTimeout = setTimeout(() => {
          let resp = obj['modified-color-properties'];
          this.templateStatus = {};
          this.initTemplateStatus(resp);
          this.templateData = resp;
          this.loader = false;
        }, 2000);
        this.progressStatus = 100;
      } else {
        this.toastService.error(data.status, 'Error has occured while generating templates');
        this.queueError = true;
      }
    }).catch(err => {
      console.warn('Polling error', 'retries left:' + retry_count)
      if (retry_count == 0) {
        this.error = true;
        this.errorHeader = 'Error This might be a network issue';
        this.errorText = 'press go near the inputs and check this tab again';
        this.loader = false;
      } else {
        this.poll(retry_count - 1);
      }

    });
  }

}
