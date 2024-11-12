import {AfterViewInit, Component, HostListener, Input, OnChanges, OnInit, SimpleChanges, ViewChild, ViewEncapsulation} from '@angular/core';
import {MappingTableComponent} from "../mapping-table/mapping-table.component";
import { ErrorStackViewerComponent } from '../../../../../../@theme/components/error-stack-viewer/error-stack-viewer.component';
import {isNumeric} from "rxjs/util/isNumeric";
import {ConfirmMappingModalComponent} from "../confirm-mapping-modal/confirm-mapping-modal.component";
import {MappingEntriesViewerComponent} from "../../../mapping-entries-viewer/mapping-entries-viewer.component";
import * as XLSX from 'xlsx'
import {GoogleAnalyticsConfig} from "../../../../../../@core/data/ga-configs";
import {AnalyticsService} from "../../../../../../@core/utils";

@Component({
    selector: 'manual-mapping',
    templateUrl: './manual-mapping.component.html',
    styleUrls: ['./manual-mapping.component.scss'],
    
})
export class ManualMappingComponent implements OnInit, AfterViewInit, OnChanges {

    leng: Number;
    ngOnChanges(changes: SimpleChanges): void {
        if (changes.sizeList) {
            if (this.sizeList)
                this.acSizeList = this.sizeList;
        }
    }

    @Input() sizeList;
    @Input() supply: string;
    @Input() supplyId: string;
    @Input() demand: string;
    @Input() demandId: string;
    @Input() entriesViewer: MappingEntriesViewerComponent;
    @ViewChild('fileInput') fileInput;
    @ViewChild('manualMappingInputTable') inputViewer: MappingTableComponent;
    @ViewChild('errorViewer') errorStack: ErrorStackViewerComponent;
    @ViewChild('confirmManualMappingModal') confirmModal: ConfirmMappingModalComponent;
    
    acSizeList = [];
    templatesList: string = "";
    selectedSizes: Array<any> = [];
    displayedColumns: Array<string> = ['Templates', 'Sizes', 'Action'];
    googleAnalyticsConfigs = GoogleAnalyticsConfig;

    constructor(private analyticsService: AnalyticsService) {

    }

    ngAfterViewInit(): void {
        this.inputViewer.sizeList = this.sizeList;
        this.inputViewer.columns = [];
        if (this.entriesViewer)
            this.entriesViewer.fetchData();
    }


    addMapping() {
        if (!this.validate())
            return;
        let sizes = Object.assign([], this.selectedSizes);
        if (sizes.filter(item => item.value == 'ALL').length > 0) {
            sizes = [];
            this.sizeList.forEach(size => {
                sizes.push({display: size, value: size});
            })
        }
        const item = {Templates: this.templatesList, Sizes: sizes};
        this.inputViewer.add(item);
        this.selectedSizes = [];
        this.templatesList = "";
        this.leng = this.inputViewer.data.length;
    }

    saveData(): void {
        this.analyticsService.trackEventOnGA(this.googleAnalyticsConfigs.MI_BT_SAVE_MAPPING_CHANGES);
        this.confirmModal.data = this.inputViewer.data;
        this.confirmModal.open(status => {
            if (status) {
                this.reset();
            }
        });
    }

    reset(): void {
        this.inputViewer.clearData();
        this.entriesViewer.fetchData();
    }

    ngOnInit() {


    }

    validate(): boolean {
        return this.validateParams(this.templatesList, this.selectedSizes, "");
    }

    validateParams(templatesList: string, selectedSizes: Array<any>, details: string): boolean {
        if (!details)
            this.errorStack.clear();

        if (!templatesList || templatesList.length == 0) {
            this.errorStack.pushError({
                title: "No Templates Selected",
                message: "Please enter Comma Separated Templates to the Input" + details,
                type: "error"
            });
            return false;
        }

        if (selectedSizes.length == 0) {
            this.errorStack.pushError({
                title: "No Sizes Selected",
                message: "Please Select a size to add the Mapping" + details,
                type: "error"
            });
            return false;
        }


        let templates = templatesList.replace(/\s*,\s*/g, ",").split(",");

        let numeric = true;
        templates = templates.filter(template => template.length > 0);
        templates.forEach(item => numeric = numeric && isNumeric(item));

        if (!numeric) {
            this.errorStack.pushError({
                title: "Invalid Templates",
                message: "Templates must be comma separated numbers" + details,
                type: "error"
            });
            return false;
        }
        return true;
    }
}
