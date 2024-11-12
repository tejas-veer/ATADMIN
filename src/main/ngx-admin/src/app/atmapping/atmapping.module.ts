import { NgSelectModule } from "@ng-select/ng-select";
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatCheckboxModule } from '@angular/material/checkbox';
import {MatInputModule} from '@angular/material/input';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { Ng2SmartTableModule } from 'ng2-smart-table';
import { MatChipsModule } from '@angular/material/chips';
import { ErrorStackViewerComponent } from './../@theme/components/error-stack-viewer/error-stack-viewer.component';
import { NbActionsModule, NbCardModule, NbTabsetModule, NbUserModule } from '@nebular/theme';
import { SampleFileService } from './../@core/data/sample-file.service';
import { BlockingStatusService } from './blocking/service/blocking-status.service';
import { BlockingService } from './../@core/data/blocking.service';
import { FormsModule } from '@angular/forms';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AtmappingComponent } from './atmapping.component';
import { ThemeModule } from '../@theme/theme.module';
import { ToastingService } from '../@core/utils/toaster.service';
import { NguiAutoCompleteModule } from '@ngui/auto-complete';
import { InputBoxComponent } from './input-box/input-box.component';
import { MappingComponent } from './mapping/mapping.component';
import { BlockingComponent } from './blocking/blocking.component';
import { BlockingInputComponent } from './blocking/components/blocking-input/blocking-input.component';
import { BlockingRulesComponent } from './blocking/components/blocking-input/components/blocking-rules/blocking-rules.component';
import { BlockingSwitchComponent } from './blocking/components/blocking-switch/blocking-switch.component';
import { BlockingtableComponent } from './blocking/components/blockingtable/blockingtable.component';
import { ConfirmModalComponent } from './blocking/components/confirm-modal/confirm-modal.component';
import { PreviewrenderComponent } from './blocking/components/previewrender/previewrender.component';
import { DemandSideComponent } from './input-box/demand-side/demand-side.component';
import { LevelSelectorComponent } from './input-box/level-selector/level-selector.component';
import { ModalPageComponent } from './input-box/modal-page/modal-page.component';
import { SupplySideTemplateComponent } from './input-box/supply-side/supply-side-template/supply-side-template.component';
import { SupplySideComponentWrapper } from './input-box/supply-side/supply-side-component-wrapper.component';
import { MappingEntriesViewerComponent } from './mapping/components/mapping-entries-viewer/mapping-entries-viewer.component';
import { MappingInputComponent } from './mapping/components/mapping-input/mapping-input.component';
import { PreviewElementComponent } from './mapping/components/preview-element/preview-element.component';
import { ConfirmMappingModalComponent } from './mapping/components/mapping-input/components/confirm-mapping-modal/confirm-mapping-modal.component';
import { ManualMappingComponent } from './mapping/components/mapping-input/components/manual-mapping/manual-mapping.component';
import { SeasonalMappingComponent } from './mapping/components/mapping-input/components/seasonal-mapping/seasonal-mapping.component';
import { ZeroColorMappingComponent } from './mapping/components/mapping-input/components/zero-color-mapping/zero-color-mapping.component';
import { MappingTableComponent } from './mapping/components/mapping-input/components/mapping-table/mapping-table.component';
import { MatTableModule } from '@angular/material/table';
import { MatIconModule } from '@angular/material/icon';
import { AtmappingRoutingModule } from './atmapping-routing.module';
import { NbCheckboxModule } from '@nebular/theme';
import { CdkTableModule } from '@angular/cdk/table';
import { NbTabComponent } from '@nebular/theme';
import { NgbTooltipConfig, NgbTooltipModule, NgbModule } from '@ng-bootstrap/ng-bootstrap';
import {MatTooltipModule} from '@angular/material/tooltip';

@NgModule({
    declarations: [
        AtmappingComponent,
        BlockingComponent,
        InputBoxComponent,
        MappingComponent,
        BlockingInputComponent,
        BlockingRulesComponent,
        BlockingSwitchComponent,
        BlockingtableComponent,
        ConfirmModalComponent,
        PreviewrenderComponent,
        ErrorStackViewerComponent,
        DemandSideComponent,
        LevelSelectorComponent,
        ModalPageComponent,
        SupplySideTemplateComponent,
        SupplySideComponentWrapper,
        MappingEntriesViewerComponent,
        MappingInputComponent,
        PreviewElementComponent,
        ConfirmMappingModalComponent,
        ManualMappingComponent,
        SeasonalMappingComponent,
        ZeroColorMappingComponent,
        MappingTableComponent,
    ],
    imports: [
        NgbModule,
        NgbTooltipModule,
        CommonModule,
        NbCardModule,
        FormsModule,
        NbTabsetModule,
        NguiAutoCompleteModule,
        MatIconModule,
        AtmappingRoutingModule,
        MatChipsModule,
        ThemeModule,
        MatSlideToggleModule,
        MatInputModule,
        MatTableModule,
        MatChipsModule,
        MatCheckboxModule,
        MatSortModule,
        MatPaginatorModule,
        MatFormFieldModule,
        NbCheckboxModule,
        CdkTableModule,
        NbActionsModule,
        NbUserModule,
        MatFormFieldModule,
        NgSelectModule,
        Ng2SmartTableModule,
        MatTooltipModule,
    ],
    entryComponents: [
        BlockingComponent,
        BlockingtableComponent,
        BlockingInputComponent,
        BlockingRulesComponent,
        ConfirmModalComponent,
        ModalPageComponent,
        NbTabComponent,
    ],
    providers: [BlockingService, BlockingStatusService, SampleFileService,  NgbTooltipConfig],
    exports: [MatTableModule, ModalPageComponent],

})
export class AtmappingModule {
    constructor(private toastingService: ToastingService) {
    }
}
