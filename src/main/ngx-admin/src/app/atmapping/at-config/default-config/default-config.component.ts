import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {ConfigFetchService} from "../../../@core/data/configfetch.service";
import {ConfigInputComponent} from "./components/config-input/config-input.component";
import {ConfigViewerComponent} from "./components/config-viewer/config-viewer.component";
import {ToastingService} from "../../../@core/utils/toaster.service";
import {NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {CookieService} from "../../../@core/data/cookie.service";


@Component({
    selector: 'at-default-config',
    templateUrl: './default-config.component.html',
    styleUrls: ['./default-config.component.scss'],
})
export class DefaultConfigComponent implements OnInit, AfterViewInit {

    addEvent;
    @ViewChild("configInput") configInput: ConfigInputComponent;
    @ViewChild("configViewer") configViewer: ConfigViewerComponent;
    @ViewChild("modalContent") modal: NgbModal;
    processedData: Array<any>;
    updates: any = [];
    level: any;
    entityId: any;
    configs: any;
    bkp: any;
    affectedTemplates: number;
    loader: any = true;
    featureMapping: boolean = false;
    featureModalMeta: any = false;
    meta: any;
    reset: boolean = false;
    modalLoader: boolean = false;
    disableConfigButton: boolean = false;
    private modalRef: NgbModalRef;

    constructor(private activatedRoute: ActivatedRoute,
                private configFetch: ConfigFetchService,
                private toastingService: ToastingService,
                private modalService: NgbModal,
                private cookieService: CookieService) {
        this.activatedRoute.params.subscribe(params => {
            this.loader = true;
            this.level = params.level;
            this.entityId = params.entityId;
            this.getConfigData();
        });
    }

    ngAfterViewInit(): void {
        if (this.addEvent) {
            this.addEvent.unsubscribe();
        }
        this.addEvent = this.configInput.updater;
        /*
        event to add a new Property
         */
        this.addEvent.subscribe(data => {
            if (data.featureMappingUpdate) {
                this.featureMappingSubmit(data);
                return;
            }
            let conf = this.processedData.filter(item => item.property == data.meta.entity);
            if (conf.length) {
                conf[0].value = data.update;
                conf[0].updation_date = "now";
                conf[0].level = this.level;
            } else {
                let newConf = this.makeConfigUpdate(data);
                this.processedData.push(newConf);
                this.processedData = JSON.parse(JSON.stringify(this.processedData));
            }
        });
    }

    private featureMappingSubmit(data: any) {
        const update = {
            featureMapping: true,
            additions: [],
            deletes: [],
            reset: false,
            buSelected: this.cookieService.getBUSelectedFromCookie()
        };

        if (data.update) {
            let saveConf = this.makeConfigUpdate(data);
            update.additions.push(saveConf);
        }

        this.configFetch.putEntityConfigs(this.entityId, this.level, update).then(data => {
            let featureMappingStatus = data.featureMappingUpdate.first;
            let featureMappingMessage = data.featureMappingUpdate.second;
            if (featureMappingStatus != 1000)
                this.toastingService.error("Feature Enabling Error", featureMappingMessage);
            else
                this.toastingService.success("Feature Enabling Success", featureMappingMessage);
            this.getConfigData();
        }
    ).catch(err => {
            this.toastingService.error("Error has occurred in updating", JSON.stringify(err));
        }).then(() => {
            this.configInput.closeFeatureMappingModal();
        });
    }

    private makeConfigUpdate(data: any) {
        let newConf = Object.assign({}, data.meta.attributes);
        newConf.property = data.meta.entity;
        newConf.value = data.update;
        newConf.updation_date = "now";
        newConf.level = this.level.toUpperCase();
        newConf.entity = this.entityId;
        return newConf;
    }

    getConfigData(): void {
        this.loader = true;
        this.disableConfigButton = false;
        this.configFetch.getEntityConfigs(this.entityId, this.level).then(
            data => {
                this.configs = data.data;
                this.meta = data.meta;
                this.processedData = DefaultConfigComponent.process(this.meta, this.configs);
                this.affectedTemplates = data["affected-templates"];
                this.featureMapping = data["featureMapping"];
                this.featureModalMeta = data["modalData"];
                this.reset = false;
                if(data.aclStatus == 403){
                    this.disableConfigButton = true;
                    this.toastingService.showToast("warning","Not Authorized", "You don't have write access to selected Entity");
                }
            }
        ).catch(
            err => {
                throw err;
            }
        ).then(() => {
            this.bkp = Object.assign({}, this.configs);
            console.info(this.configs);
            this.loader = false;
        });
    }

    resetViewer(): void {
        this.processedData = DefaultConfigComponent.process(this.meta, this.configs);
    }


    save(): void {
        let changes = [];
        let deletes = [];
        this.modalLoader = true;
        this.meta.forEach(meta => {
            let conf = this.configs.filter(item => item.property == meta.entity);
            let newConf = this.processedData.filter(item => item.property == meta.entity);
            if (conf.length && newConf.length && (conf[0].value != newConf[0].value)) {
                changes.push(newConf[0]);
            } else if (conf.length && !newConf.length) {
                deletes.push(conf[0]);
            } else if (newConf.length && !conf.length) {
                changes.push(newConf[0]);
            }
        });

        const update = {
            additions: changes,
            deletes: deletes,
            reset: this.reset,
            buSelected: this.cookieService.getBUSelectedFromCookie()
        };

        if (changes.length || deletes.length) {
            this.configFetch.putEntityConfigs(this.entityId, this.level, update).then(
                data => {
                    this.modalLoader = false;
                    this.modalRef.close();
                    this.getConfigData();
                }
            ).catch(err => {
                this.toastingService.error("Error has occurred in updates", JSON.stringify(err));
            });
        } else {
            this.toastingService.warning("Nothing to Update", "There have been no updates on this page please make some changes to push");
            this.modalLoader = false;
            this.modalRef.close();
        }

    }

    public static process(meta, configs): any {
        let newConfigs = [];
        meta.forEach(meta => {
            let conf = configs.filter(item => item.property == meta.entity);
            if (conf.length) {
                let item = Object.assign({}, conf[0]);
                item = Object.assign(item, meta.attributes);
                newConfigs.push(item);
            }
        });
        return newConfigs;
    }

    openModal(status: boolean) {
        this.reset = status;
        this.modalRef = this.modalService.open(this.modal);
    }

    ngOnInit() {

    }


}
