import { InsertUpdateService } from './../../generator-viewer/insert-update.service';
import {Component, OnInit, ViewChild} from '@angular/core';
import {NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {GeneratorService} from "../../../../@core/data/generator.service";

@Component({
  selector: 'report-issue-modal',
  templateUrl: './report-issue-modal.component.html',
  styleUrls: ['./report-issue-modal.component.scss']
})
export class ReportIssueModalComponent implements OnInit {

  @ViewChild('reportIssueModalContent') modal: NgbModal;
    private modalRef: NgbModalRef;

    constructor(private modalService: NgbModal,
                private generatorService: GeneratorService,
                private refreshService: InsertUpdateService) {
    }

    ngOnInit() {
    }

    openModal() {
        this.modalRef = this.modalService.open(this.modal, {
            backdrop: 'static',
            keyboard: false
        });
    }

    reportIssue(option, text) {
        this.generatorService.postIssue(option + "\n" + text);
        this.modalRef.close();
        this.ngOnInit();
        this.refreshService.next();
    }


}
