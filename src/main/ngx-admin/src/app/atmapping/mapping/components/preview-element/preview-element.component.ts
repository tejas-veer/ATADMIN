import { EntityService } from './../../../../@core/data/entity-fetch.service';
import { Component, Input, OnInit } from '@angular/core';
import { DomSanitizer } from "@angular/platform-browser";
import { NgbModal } from "@ng-bootstrap/ng-bootstrap";
@Component({
  selector: 'preview-element',
  templateUrl: './preview-element.component.html',
  styleUrls: ['./preview-element.component.scss']
})
export class PreviewElementComponent implements OnInit {

  url;
  loader: boolean = true;
  @Input() size: string;
  @Input() creative: string;
  @Input() type: string;

  constructor(private doms: DomSanitizer, private modalService: NgbModal) {
  }

  open(content) {
    this.modalService.open(content, { size: 'lg' });
  }

  ngOnInit() {
    switch (this.type) {
      case 'FRAMEWORK':
        this.url = this.doms.bypassSecurityTrustResourceUrl('http://ab.cm.reports.mn/ResourceProxy/TemplatePreviewImage?frameworkId=' + EntityService.processedAdtag(this.creative) + '&templateSize=300x250&pageType=keywords-only&templateType=creative');
        break;
      default:
        this.url = this.doms.bypassSecurityTrustResourceUrl('http://ab.cm.reports.mn/ResourceProxy/TemplatePreviewImage?templateId=' + EntityService.processedAdtag(this.creative) + '&templateSize=300x250&pageType=keywords-only&templateType=creative');
    }
  }

}

