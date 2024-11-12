import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {DomSanitizer} from "@angular/platform-browser";

@Component({
    selector: 'whitelisting-modal',
    templateUrl: './whitelisting-modal.component.html',
    styleUrls: ['./whitelisting-modal.component.scss']
})
export class WhitelistingModalComponent implements OnInit {
    @Input() templates: any;
    @Input() entity: any;
    @Input() whitelistLater: any;
    @Input() confirmWhitelist: any;
    @Input() reportProblems: any;
    loader: any;
    private modalRef: NgbModalRef;
    @ViewChild('modalContent') modal: NgbModal;

    constructor(private modalService: NgbModal,
                private sanitizer: DomSanitizer) {
    }

    ngOnInit() {
        this.loader = false;
    }

    closeModal() {
        this.modalRef.close();
    }

    openModal() {
        this.modalRef = this.modalService.open(this.modal, {
            size: 'lg',
            backdrop: 'static',
            keyboard: false
        });
    }

    hasTemplates(): boolean {
        for (let i = 0; i < this.templates.length; i++) {
            if (this.templates[i])
                return true;
        }
        return false;
    }


}