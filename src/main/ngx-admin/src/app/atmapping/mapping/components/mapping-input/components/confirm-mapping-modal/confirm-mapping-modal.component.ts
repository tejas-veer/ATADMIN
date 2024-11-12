import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {MappingService} from "../../../../../../@core/data/mapping.service";
import {UtilService} from "../../../../../../@core/utils/util.service";
import {ToastingService} from "../../../../../../@core/utils/toaster.service";
import {CookieService} from "../../../../../../@core/data/cookie.service";
import {GoogleAnalyticsConfig} from "../../../../../../@core/data/ga-configs";
import {AnalyticsService} from "../../../../../../@core/utils";

@Component({
    selector: 'ngx-confirm-mapping-modal',
    templateUrl: './confirm-mapping-modal.component.html',
    styleUrls: ['./confirm-mapping-modal.component.scss']
})
export class ConfirmMappingModalComponent implements OnInit {

    @Input() mappingType: string = 'MANUAL';
    @ViewChild('mappingConfirmModal') confirmModal: NgbModal;
    private _data: Array<any>;
    modalRef: NgbModalRef;
    onClose: any;
    @Input() supply: string;
    @Input() supplyId: string;
    @Input() demand: string;
    @Input() demandId: string;
    loader: boolean = false;
    googleAnalyticsConfigs = GoogleAnalyticsConfig;

    get data(): Array<any> {
        return this._data;
    }

    set data(value: Array<any>) {
        this._data = UtilService.makeIterable(value);
    }

    constructor(private modalService: NgbModal,
                private mappingService: MappingService,
                private toastingService: ToastingService,
                private cookieService: CookieService,
                private analyticsService: AnalyticsService) {

    }

    ngOnInit() {
    }

    open(onClose: any): void {
        this.modalRef = this.modalService.open(this.confirmModal, {size: 'lg'});
        this.onClose = onClose;
    }

    confirm(): void {
      this.analyticsService.trackEventOnGA(this.googleAnalyticsConfigs.MI_BT_CONFIRM_MAPPING);
        this.loader = true;
        let cb = this.onClose;
        if (!cb)
            cb = console.warn;

        let items = UtilService.preProcessIterables(this.data, this.mappingType);
        let payload = {
            templates: items,
            mappingType: this.mappingType,
            hierarchyLevel: {
                supply: this.supply.toUpperCase(),
                supplyId: this.supplyId,
                demand: this.demand.toUpperCase(),
                demandId: this.demandId
            },
            buSelected: this.cookieService.getBUSelectedFromCookie()
        };
        this.mappingService.insertMappings(this.mappingType, payload).then(data => {
            console.info("Response for POST from Inserted Mappings", data);
            cb(true);
            this.toastingService.success("Update Success", "Your mappings have been added to the database");
        }).catch(err => {
            console.error(err);
            this.toastingService.error("Update Error", JSON.stringify(err));
            cb(false);
        }).then(() => {
            this.loader = false;
            this.modalRef.close();
        });
    }

}
