import { Component, OnInit, Input } from '@angular/core';
import {NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {DomSanitizer} from "@angular/platform-browser";

@Component({
  selector: 'confirmation-modal',
  templateUrl: './confirmation-modal.component.html',
  styleUrls: ['./confirmation-modal.component.scss']
})
export class ConfirmationModalComponent implements OnInit {

  @Input() templates: any;
  @Input() entity: any;
  @Input() confirmation: any;
  loader: any;
  private modalRef: NgbModalRef;

  constructor(private modalService: NgbModal,
              private sanitizer: DomSanitizer) {

  }

  ngOnInit() {
      this.loader = true;
  }

  open(content) {
      this.modalRef = this.modalService.open(content, {size: "lg"});
      this.loader = false;
  }

  closeModal() {
      this.modalRef.close();
  }

  save() {
      this.loader = true;
      this.confirmation();
  }

  hasTemplates(): boolean {
      let displayNoTemplates = false;
      this.templates.forEach(template => {
          displayNoTemplates = displayNoTemplates || template.selected;
      });

      return displayNoTemplates;
  }

}
