import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {DomSanitizer, SafeHtml} from "@angular/platform-browser";
import {NgSelectComponent} from "@ng-select/ng-select";
import {RowAdditionService} from "../../../@core/data/row-addition.service";
import {ConversionMapService} from "../../../@core/data/conversion-map.service";

@Component({
    selector: 'cm-template',
    template: `
        <div class="drilldown-column" [style.padding-left.px]="getPaddingLeft()">
            <div class="button-html-container">
<!--                <button *ngIf="!disableDrilldownButton" nbButton status="primary" class="drilldown-button"-->
<!--                        (click)="toggleDropdownForDrilldown();">-->
<!--                    &raquo;-->
<!--                </button>-->
                <div [innerHTML]="html"></div>
<!--                <button (click)="removeRows()" *ngIf="showCloseButton" class="close-button">&#10006;</button>-->
            </div>
<!--            <ng-container *ngIf="isDropdownOpen">-->
<!--                <ng-select [(ngModel)]="selectedOption" appendTo="body"-->
<!--                           (ngModelChange)="onOptionChange()" [loading]="loader"-->
<!--                           [items]="dropdownOptions" dropdownPosition="bottom" #mySelect>-->
<!--                </ng-select>-->
<!--            </ng-container>-->
        </div>
    `,
    styleUrls: ['./cm-template.component.css']
})
export class CmTemplateComponent implements OnInit {
    @Input() rowData: any;
    @Input() html: SafeHtml;
    groupBySelectedOption: Array<string> = ['Template'];
    readonly ALL_DROPDOWN_OPTIONS: Array<string> = ['Template', 'Template Size', 'Domain', 'Ad Tag',  'Partner'];

    isDropdownOpen = false;
    selectedOption: string;
    dropdownOptions: Array<string>;
    globalFilterHeadings: any;
    disableDrilldownButton: boolean = false;
    // @ViewChild('mySelect') mySelect: NgSelectComponent;
    showCloseButton: boolean = false;
    loader: boolean = false;


    constructor(private sanitizer: DomSanitizer,
                private rowAdditionService: RowAdditionService,
                private conversionMapService: ConversionMapService) {
    }

    ngOnInit(): void {
        if (this.rowData['DisableDrilldownButton'])
            this.disableDrilldownButton = true;

        if (this.rowData['enableCloseLevelButton'] === true)
            this.showCloseButton = true;

        const rowKeys = Object.keys(this.rowData);
        let firstKey: string;
        firstKey = this.getFirstKey(rowKeys);

        this.html = this.sanitizer.bypassSecurityTrustHtml(this.rowData[firstKey]);
        this.rowAdditionService.groupBySelectedOption$.subscribe((option) => {
            this.groupBySelectedOption = option;
        });
        this.rowAdditionService.globalFilters$.subscribe((filters) => {
            this.globalFilterHeadings = Object.keys(filters).join(',');
        });
        this.loader = false;
        this.initializeDropdownOptions();
    }

    // ngOnDestroy() {
    //     window.removeEventListener('scroll', this.onScroll, true);
    // }

    private getFirstKey(rowKeys: string[]) {
        let firstKey: string;
        if (rowKeys.includes('Heading')) {
            firstKey = 'Heading';
        } else if (rowKeys.includes('Template')) {
            firstKey = 'Template';
        } else
            firstKey = rowKeys[0];
        return firstKey;
    }

    // private onScroll = (event: any) => {
    //     if (this.mySelect && this.mySelect.isOpen) {
    //         const isScrollingInScrollHost = (event.target.className as string).indexOf('ng-dropdown-panel-items') > -1;
    //         if (isScrollingInScrollHost) {
    //             return;
    //         }
    //         this.mySelect.close();
    //     }
    // }

    initializeDropdownOptions() {
        const rowDataFiltersToExclude = this.rowData['ParentRowFilters'];
        const currentRowDimension = this.rowData['currentRowDimension'];
        const dropdownOptionsToExclude = [...this.groupBySelectedOption];
        if (rowDataFiltersToExclude) {
            const filterKeys = Object.keys(rowDataFiltersToExclude);
            dropdownOptionsToExclude.push(...filterKeys);
        }
        if (currentRowDimension)
            dropdownOptionsToExclude.push(currentRowDimension);

        this.dropdownOptions = [...this.ALL_DROPDOWN_OPTIONS];
        dropdownOptionsToExclude.forEach(optionToExclude => {
            for (const key in this.conversionMapService.frontendToDrilldownMultiMap) {
                if (this.conversionMapService.frontendToDrilldownMultiMap[key].includes(optionToExclude)) {
                    const indexToRemove = this.dropdownOptions.indexOf(key);
                    if (indexToRemove !== -1) {
                        this.dropdownOptions.splice(indexToRemove, 1);
                    }
                }
            }
        });
    }

    // toggleDropdownForDrilldown() {
    //     this.isDropdownOpen = !this.isDropdownOpen;
    //     if (this.isDropdownOpen === true) {
    //         setTimeout(() => {
    //             this.mySelect.open();
    //         }, 1);
    //     }
    //     window.addEventListener('scroll', this.onScroll, true);
    // }
    //
    // onOptionChange() {
    //     this.loader = true;
    //     this.rowAdditionService.addRowsStartingFrom(this.rowData, this.selectedOption);
    // }
    //
    // removeRows() {
    //     this.rowAdditionService.removeRowsStartingFrom(this.rowData);
    // }

    getPaddingLeft() {
        const level = this.rowData['Level'];
        if (level !== undefined) {
            return 26 * level;
        }
        return 0;
    }
}
