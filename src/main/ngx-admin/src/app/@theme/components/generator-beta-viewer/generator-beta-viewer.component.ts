import { InsertUpdateService } from './../generator-viewer/insert-update.service';
import { EntityService } from './../../../@core/data/entity-fetch.service';
import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {GeneratorBetaService} from "../../../@core/data/generator-beta.service";
import {ToastingService} from "../../../@core/utils/toaster.service";
import {DomSanitizer} from "@angular/platform-browser";
import {String} from 'typescript-string-operations'
import {WhitelistingModalComponent} from "./whitelisting-modal/whitelisting-modal.component";
import {ConfirmationModalComponent} from "./confirmation-modal/confirmation-modal.component";
import { ReportIssueModalComponent } from './report-issue-modal/report-issue-modal.component';

export const DisplayStatus = {
    'FETCH_CUSTOMIZATION': 0,
    'ALLOW_GENERATION': 1,
    'DISPLAY_PROGRESS_BAR': 2,
    'DISPLAY_TEMPLATES': 3,
    'ERROR_STATUS': 4
};

export const GENERATOR_BASE_URL = "http://attg.srv.media.net/TemplateGenerator/?ad_size={0}&device_type=DESKTOP&url={1}";

@Component({
    selector: 'generator-beta-viewer',
    templateUrl: './generator-beta-viewer.component.html',
    styleUrls: ['./generator-beta-viewer.component.scss']
})
export class GeneratorBetaViewerComponent implements OnInit {
    @Input() entity: any;
    private useProxy: boolean;
    private noCache: boolean;
    private urlList: Array<any>;
    private interval: any;
    displayStatus: any;
    confirmation: any;
    whitelistLater: any;
    confirmWhitelist: any;
    reportProblems: any;

    response: any;
    private error: boolean;
    private errorHeader: string;
    private errorText: string;

    templates: any;
    templatesToWhitelist: any;
    templateCollection: any;
    customisation: any;
    originalCustomisation: any;
    state: number;
    progressStatus: any;
    progressText: any;
    generatorUrl: string;

    @ViewChild("whitelistingModal") whitelistingModal: WhitelistingModalComponent;
    @ViewChild("confirmationModal") confirmationModal: ConfirmationModalComponent;
    @ViewChild("reportIssueModal") reportIssueModal: ReportIssueModalComponent;

    constructor(private generatorBetaService: GeneratorBetaService,
                private toastService: ToastingService,
                private refreshService: InsertUpdateService,
                private entityService: EntityService,
                public sanitizer: DomSanitizer) {
    }

    ngOnInit() {
        this.useProxy = true;
        this.templateCollection = {"INSERTED": [], "EXTRA": [], "SUGGESTED": []};
        this.state = DisplayStatus.FETCH_CUSTOMIZATION;
        this.progressStatus = 20;
        this.progressText = "Starting Generation";
        this.displayStatus = DisplayStatus;
        this.urlList = [];
        this.templatesToWhitelist = [];
        this.originalCustomisation = {"headerText": null, "adAttribution": {}};
        this.generatorBetaService.getCustomization(this.entity.id).then(customisation => {
                this.state = DisplayStatus.ALLOW_GENERATION;
                this.customisation = customisation;
                this.originalCustomisation.headerText = this.customisation.headerText;
                if (this.customisation.adAttribution) {
                    this.originalCustomisation.adAttribution.link = this.customisation.adAttribution.link;
                    this.originalCustomisation.adAttribution.text = this.customisation.adAttribution.text;
                } else {
                    this.customisation.adAttribution = {};
                    this.originalCustomisation.adAttribution = {};
                }
            }
        );
        this.confirmation = () => {
            this.confirmTemplates()
        };
        this.confirmWhitelist = () => {
            this.sendWhitelistConfirmation();
        };
        this.whitelistLater = () => {
            this.delayWhitelisting();
        };
        this.reportProblems = () => {
            this.reportIssue();
        };

    }

    delayWhitelisting(): any {
        this.ngOnInit();
        this.refreshService.next();
        this.whitelistingModal.closeModal();
    }

    reportIssue(): any {
        this.whitelistingModal.closeModal();
        this.reportIssueModal.openModal();
    }

    sendWhitelistConfirmation(): void {
        let whiteListTemplatesList = [];
        this.templatesToWhitelist.forEach(template => {
            if (template.selected)
                whiteListTemplatesList.push(template.templateId);
        });
        console.log(this.templatesToWhitelist);
        console.log(whiteListTemplatesList);
        this.whitelistingModal.loader = true;
        this.entityService.updateGeneratorEntity(this.entity.id, whiteListTemplatesList, []).then(data => {
            this.toastService.success("Templates have been Whitelisted", data.message);
            this.whitelistingModal.loader = false;
            this.refreshService.next();
            this.ngOnInit();
        }).catch(err => {
            this.toastService.error("Error in whitelisting", err.message);
            console.log(err);
        }).then(() => {
            this.whitelistingModal.closeModal();
            this.ngOnInit();
        });
    }

    enqueue() {
        this.updateCustomization();
        this.state = DisplayStatus.DISPLAY_PROGRESS_BAR;
        this.progressText = "Queued for Generation";
        this.generatorBetaService.createRequest(this.entity.id, this.useProxy, this.noCache).then(response => {
            this.progressStatus = 40;
            this.progressText = 'Generating Templates';
            response.urls.forEach(url => this.urlList.push(url));
            let encodedUrls = [];
            response.urls.forEach(url => encodedUrls.push(encodeURIComponent(url)));
            this.generatorUrl = String.Format(GENERATOR_BASE_URL, this.entity.size, String.Join('|', encodedUrls));
            this.response = JSON.parse(response.generator);
            this.response['requestId'] = this.response['requestId'].split('|')[1];
            /* Removing network polling */
            this.pollGenerator(5);
        }).catch(err => {
            this.state = DisplayStatus.ERROR_STATUS;
            if (err.status == 422) {
                this.errorLog('Error : No Urls Found on Druid', 'No Urls were found on Druid for the given Input , Generator cannot generate Templates');
            } else {
                this.errorLog('Error ' + err.status + ' ! could not queue Templates', ' Try Refreshing the page and retry ');
            }
        });
    }

    updateCustomization() {
        let customisationHeader = this.customisation.headerText;
        let customisationLink = this.customisation.adAttribution.link;
        if (this.originalCustomisation) {
            if (this.originalCustomisation.headerText == customisationHeader)
                customisationHeader = '';
            if (this.originalCustomisation.adAttribution.link == customisationLink)
                customisationLink = '';
        }

        if (!customisationHeader) customisationHeader = '';
        if (!customisationLink) customisationLink = '';
        this.generatorBetaService.addCustomization(this.entity.id, ('' + customisationHeader).trim(), ('' + customisationLink).trim());
    }

    pollGenerator(retry_count: number): void {
        this.state = DisplayStatus.DISPLAY_PROGRESS_BAR;
        this.incrementProgress();

        this.generatorBetaService.poll(this.response['requestId']).then(response => {
            console.log(response);
            if (response.status == 'QUEUED' || response.status == 'RUNNING') {
                setTimeout(() => {
                    this.pollGenerator(5);
                }, 5000);
            } else if (response.status == 'COMPLETED') {
                this.response = JSON.parse(response.response);
                if (this.response['error']) {
                    this.toastService.error(response.status, JSON.stringify(this.response));
                    this.errorLog('Oh Snap! Failure to Generate Templates', 'Please Try Again after later');
                    return;
                }
                this.templates = this.response['templates'];
                console.log(this.templates);
                this.progressText = 'Finished';
                this.templates.forEach(template => {
                    template.selected = (template.templateStatus == 'SUGGESTED');
                    this.templateCollection[template.templateStatus].push(template);
                });
                this.state = DisplayStatus.DISPLAY_TEMPLATES;
                return;
            } else {
                /* FAILED OR UNKNOWN */
                this.errorLog('Oh Shoot! Failure to Generate Templates', 'Please Try Again after later');
                this.toastService.error(response.status, 'Error has occured while generating templates');
            }
        }).catch(err => {
            console.warn('Polling error', 'retries left:' + retry_count);
            if (retry_count == 0) {
                this.errorLog('Error This might be a network issue',
                    'press go near the inputs and check this tab again');
            } else {
                this.pollGenerator(retry_count - 1);
            }
        });
    }

    errorLog(errorHeader: string, errorText: string): void {
        this.state = DisplayStatus.ERROR_STATUS;
        this.errorHeader = errorHeader;
        this.errorText = errorText;
    }

    confirmTemplates(): void {
        let templatesToInsert = [];
        this.templates.forEach(template => {
            if (template.selected)
                templatesToInsert.push(template)
        });
        console.log(templatesToInsert);
        this.confirmationModal.loader = true;
        this.generatorBetaService.insert(this.entity.id, JSON.stringify(templatesToInsert)).then(response => {
            this.toastService.success('Templates Inserted Successfully', "");
            response = response.json();
            this.templatesToWhitelist = [];
            response.templates.forEach(template => {
                template.selected = true;
                this.templatesToWhitelist.push(template)
            });
            this.confirmationModal.loader = false;
            if (response.needConfirmation) {
                this.confirmationModal.closeModal()
                this.whitelistingModal.openModal();
            } else {
                this.confirmationModal.closeModal();
                this.ngOnInit();
                this.refreshService.next();
            }
        }).catch(err => {
            this.toastService.error('Error in Inserting Templates', "");
            console.error(err.toString());
            this.ngOnInit();
        }).then(() => {
            this.refreshService.next();
        });
    }

    incrementProgress(): void {
        if (this.progressStatus < 60) {
            this.progressStatus += 10;
        } else if (this.progressStatus < 90) {
            this.progressStatus += 5;
        } else if (this.progressStatus < 95) {
            this.progressStatus += 1;
        } else if (this.progressStatus < 98) {
            this.progressStatus += 0.1;
        }
    }
}