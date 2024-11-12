import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {CdkDragDrop, moveItemInArray} from '@angular/cdk/drag-drop';
import {CookieService} from '../../@core/data/cookie.service';
import {ReportingConstantsService} from "../../@core/data/reporting-constants.service";

@Component({
    selector: 'ngx-format-metrics',
    templateUrl: './format-metrics.component.html',
    styleUrls: ['./format-metrics.component.css'],
})
export class FormatMetricsComponent implements OnInit {
    metricColumnVisibilityData: any;
    isDragging: boolean = false;
    draggedIndex: number = -1;
    draggedZIndex: number = 2;
    dimensionHeadingsArray: Array<string>;
    selectedBu: any;
    selectAll: boolean = false;

    constructor(@Inject(MAT_DIALOG_DATA) public data: any,
                public dialogRef: MatDialogRef<FormatMetricsComponent>,
                private cookieService: CookieService,
                private reportingConstants: ReportingConstantsService) {
        if (data && data.currentColumns) {
            this.selectedBu = this.cookieService.getBUSelectedFromCookie();
            const allMetricColumns: Array<string> = this.initializeAllMetricsOptionBasedOnBu();
            const currentColumns = data.currentColumns;
            const columnHeadingTitles = Object.keys(currentColumns).map(key => currentColumns[key].title);
            const metricHeadingsFromCookie = this.getMetricsSettingsFromCookie();
            if (metricHeadingsFromCookie) {
                const metricsToBeShown = JSON.parse(metricHeadingsFromCookie);
            }
            this.dimensionHeadingsArray = columnHeadingTitles.slice(0,1);
            const metricHeadingArray = columnHeadingTitles.slice(1);
            this.initializeMetricsVisibilityData(metricHeadingArray, allMetricColumns);
        }
    }

    initializeAllMetricsOptionBasedOnBu(): Array<string> {
        if (this.selectedBu === 'MAX')
            return this.reportingConstants.MAX_REPORTING_METRICS;
        else
            return this.reportingConstants.CM_REPORTING_METRICS;
    }

    getMetricsHeadingLength() {
        if (this.selectedBu === 'MAX')
            return this.reportingConstants.MAX_REPORTING_METRICS.length;
        else return this.reportingConstants.CM_REPORTING_METRICS.length;
    }

    getMetricsSettingsFromCookie() {
        if (this.selectedBu === 'MAX')
            return this.cookieService.getMaxMetricsSettingCookie();
        else
            return this.cookieService.getCmMetricsSettingsCookie();
    }

    initializeMetricsVisibilityData(metricHeadingArray, allMetricColumns) {
        this.metricColumnVisibilityData = metricHeadingArray.map(name => ({
            name,
            visible: true,
        }));
        allMetricColumns
            .filter(name => !metricHeadingArray.includes(name))
            .map(name => this.metricColumnVisibilityData.push({name, visible: false}));
    }

    ngOnInit(): void {
    }

    toggleVisibility(item: any) {
        item.visible = !item.visible;
    }

    startDrag(index: number) {
        this.isDragging = true;
        this.draggedIndex = index;
    }

    endDrag() {
        this.isDragging = false;
        this.draggedIndex = -1;
    }

    onListDrop(event: CdkDragDrop<{ name: string, visible: boolean }[]>): void {
        moveItemInArray(this.metricColumnVisibilityData, event.previousIndex, event.currentIndex);
    }

    selectAllMetrics(event: any) {
        if (event.target.checked) {
            this.metricColumnVisibilityData.forEach(item => {
                item.visible = true;
            });
        }
        if (!event.target.checked) {
            this.metricColumnVisibilityData.forEach(item => {
                item.visible = false;
            });
        }
    }

    applyMetrics() {
        const metricsToBeShown = this.metricColumnVisibilityData.filter(item => item.visible).map(item => item.name);
        this.setMetricsInCookie(metricsToBeShown);
        const finalColumnHeadings = [...this.dimensionHeadingsArray, ...metricsToBeShown];
        this.dialogRef.close(finalColumnHeadings);
    }

    closeMetricsModal() {
        this.dialogRef.close();
    }

    setMetricsInCookie(metrics) {
        if (this.selectedBu === 'MAX') {
            this.cookieService.setMaxMetricSettingsCookie(JSON.stringify(metrics));
        } else {
            this.cookieService.setCmMetricSettingsCookie(JSON.stringify(metrics));
        }
    }
}
