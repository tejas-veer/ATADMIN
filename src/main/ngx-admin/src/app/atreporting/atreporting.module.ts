import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {EntityInputComponent} from './entity-input/entity-input.component';
import {ATReportingRoutingModule} from './atreporting-routing.module';
import {NbCalendarRangeModule, NbCardModule, NbIconModule} from '@nebular/theme';
import {AtmappingModule} from '../atmapping/atmapping.module';
import {Ng2SmartTableModule} from 'ng2-smart-table';
import {ThemeModule} from '../@theme/theme.module';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatIconModule} from '@angular/material/icon';
import {NbInputModule} from '@nebular/theme';
import {NgSelectModule} from '@ng-select/ng-select';
import {NguiAutoCompleteModule} from '@ngui/auto-complete';
import {FormsModule} from '@angular/forms';
import {DateRangeSelectorComponent} from './date-range-selector/date-range-selector.component';
import {DrilldownComponent} from './custom-template-for-table/drilldown.component';
import {RouterModule} from '@angular/router';
import {ClipboardModule} from '@angular/cdk/clipboard';
import {MatSnackBarModule} from '@angular/material/snack-bar';
import {CmReportingComponent} from './cm-reporting/cm-reporting.component';
import {CmTemplateComponent} from './cm-reporting/cm-template/cm-template.component';
import {ATReportingComponent} from './atreporting.component';
import {MatDialogModule} from '@angular/material/dialog';
import { FormatMetricsComponent } from './format-metrics/format-metrics.component';
import { NbCheckboxModule } from '@nebular/theme';
import { DragDropModule } from '@angular/cdk/drag-drop';
import {MatTooltipModule} from '@angular/material/tooltip';
import { SelectModeComponent } from './select-mode/select-mode.component';
import {OverlayModule} from '@angular/cdk/overlay';
import {MatCardModule} from '@angular/material/card';
import {MatMenuModule} from '@angular/material/menu';
import {MatButtonModule} from '@angular/material/button';
import {MatListModule} from '@angular/material/list';
import {MatDividerModule} from '@angular/material/divider';
import {MatChipsModule} from "@angular/material/chips";
import { FiltersComponent } from './filters/filters.component';

@NgModule({
    declarations: [
        EntityInputComponent,
        DateRangeSelectorComponent,
        DrilldownComponent,
        CmReportingComponent,
        CmTemplateComponent,
        ATReportingComponent,
        FormatMetricsComponent,
        SelectModeComponent,
        FiltersComponent,
    ],
    imports: [
        CommonModule,
        ATReportingRoutingModule,
        NbCardModule,
        AtmappingModule,
        Ng2SmartTableModule,
        ThemeModule,
        MatFormFieldModule,
        MatInputModule,
        MatIconModule,
        NbInputModule,
        NgSelectModule,
        NguiAutoCompleteModule,
        FormsModule,
        NbCalendarRangeModule,
        RouterModule,
        ClipboardModule,
        MatSnackBarModule,
        NbIconModule,
        MatDialogModule,
        NbCheckboxModule,
        DragDropModule,
        MatTooltipModule,
        MatListModule,
        MatDividerModule,
        OverlayModule,
        MatMenuModule,
        MatButtonModule,
        MatCardModule,
        MatChipsModule,
    ],
})
export class ATReportingModule {
}
