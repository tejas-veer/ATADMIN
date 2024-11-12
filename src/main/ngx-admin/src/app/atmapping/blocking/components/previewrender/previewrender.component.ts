import {Component, Input, OnInit} from '@angular/core';
import {ViewCell} from 'ng2-smart-table';
import {DomSanitizer} from '@angular/platform-browser';

import {ActivatedRoute} from '@angular/router';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';

import {EmitterService} from '../../../../atreporting/EntityData/modal.service';
import { EntityService } from '../../../../@core/data/entity-fetch.service';
@Component({
  selector: 'ngx-previewrender',
  templateUrl: './previewrender.component.html',
  styleUrls: ['./previewrender.component.scss']
})
export class PreviewrenderComponent implements OnInit, ViewCell {

  url;
  loader: boolean = true;
  public renderValue;
  public data;
  public size;

  @Input() value;
  @Input() rowData: any;
  type: string;

  constructor(private doms: DomSanitizer,
              private emitter: EmitterService,
              private modalService: NgbModal,
              private route: ActivatedRoute) {
  }

  open(content) {
    this.modalService.open(content, {size: 'lg'});
  }

  ngOnInit() {
    this.renderValue = this.value;
    this.data = this.rowData;
    this.size = this.rowData['Template Size'];
    let x = "";
    if (this.rowData.Template) {
      this.type = 'TEMPLATE';
      this.url = this.doms.bypassSecurityTrustResourceUrl('http://ab.cm.reports.mn/ResourceProxy/TemplatePreviewImage?templateId=' + EntityService.processedAdtag(this.value) + '&templateSize=300x250&pageType=keywords-only&templateType=creative');
    }
    else {
      this.type = 'FRAMEWORK';
      this.url = this.doms.bypassSecurityTrustResourceUrl('http://ab.cm.reports.mn/ResourceProxy/TemplatePreviewImage?frameworkId=' + EntityService.processedAdtag(this.value) + '&templateSize=300x250&pageType=keywords-only&templateType=creative');
    }
  }


}
