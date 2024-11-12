import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {Subject} from "rxjs/Subject";
import {ToastingService} from "../../../../../@core/utils/toaster.service";
import {NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";

@Component({
    selector: 'config-input',
    templateUrl: './config-input.component.html',
    styleUrls: ['./config-input.component.scss']
})
export class ConfigInputComponent implements OnInit, OnChanges {
    @Input() meta;
    @Input() hide;
    @Input() featureMapping;
    @Input() featureMappingModalMeta;
    @Input() isDisable;
    private _updater: Subject<any> = new Subject<any>();
    featureMappingInput: any;
    selectedMeta;
    updateValue;

    featureMappingModal: NgbModalRef;
    modalLoader: boolean;

    constructor(private toastService: ToastingService, private modalService: NgbModal) {
    }

    open(content) {
        this.featureMappingModal = this.modalService.open(content, {size: 'lg'});
        if (this.featureMappingModalMeta.data)
            this.featureMappingInput = (this.featureMappingModalMeta.data[0].value)
    }

    public closeFeatureMappingModal(): void {
        this.modalLoader = false;
        this.featureMappingModal.close();
    }

    updateAndEnable() {
        let metaObj = this.meta.filter(item => item.entity == this.featureMappingModalMeta.meta);
        this.modalLoader = true;
        this._updater.next({
            meta: metaObj[0],
            update: '' + this.featureMappingInput,
            featureMappingUpdate: true
        });
    }

    get updater(): Subject<any> {
        return this._updater;
    }

    set updater(value: Subject<any>) {
        this._updater = value;
    }

    ngOnChanges(changes: SimpleChanges): void {
        if (changes.meta && changes.meta.currentValue) {
            this.selectedMeta = changes.meta.currentValue[0];
        }
    }

    ngOnInit(): void {

    }

    inputChange(e): void {
        console.log(this.selectedMeta);
        this.updateValue = this.selectedMeta.attributes.type == 'checkbox' ? false : null;
    }

    log(e): void {
        console.log(e);
    }

    update(metaVal, update): void {
        if (metaVal.entity == "HEADER_TEXT") {

            if (!update || update.trim().length == 0) {
                update = "&amp;nbsp;";
            }
        } else if (!update && update !== false) {
            this.toastService.warning("Invalid Text in Input", "Please enter a valid value for Input");
            return;
        }
        //TODO: find an elegant solution
        this._updater.next({meta: metaVal, update: '' + update});
    }

}
