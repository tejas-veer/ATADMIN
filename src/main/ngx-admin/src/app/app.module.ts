import {MatPaginatorIntl, MatPaginatorModule} from '@angular/material/paginator';
import {NgSelectModule} from '@ng-select/ng-select';
import {NgMultiSelectDropDownModule} from 'ng-multiselect-dropdown';
import {MatAutocompleteModule} from '@angular/material/autocomplete';
import {IgxDropDownModule} from 'igniteui-angular';
import {NgSelect2Module} from 'ng-select2';
import {FormsModule} from '@angular/forms';
import {TagInputModule} from 'ngx-chips';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {MatButtonModule} from '@angular/material/button';
import {MatTooltipModule} from '@angular/material/tooltip';
import {MatIconModule} from '@angular/material/icon';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatTableModule} from '@angular/material/table';
import {HttpModule} from '@angular/http';
import {MatTableDataSource} from '@angular/material/table'
import {MatInputModule} from '@angular/material/input';
import {BrowserModule} from '@angular/platform-browser';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {ErrorHandler, NgModule} from '@angular/core';
import {HttpClientModule} from '@angular/common/http';
import {CoreModule} from './@core/core.module';
import {ThemeModule} from './@theme/theme.module';
import {AppComponent} from './app.component';
import {AppRoutingModule} from './app-routing.module';
import {ToasterModule} from 'angular2-toaster';
import {BuSelectionComponent} from './bu-selection/bu-selection.component';
import {
    NbChatModule,
    NbDatepickerModule,
    NbDialogModule,
    NbMenuModule,
    NbSidebarModule,
    NbToastrModule,
    NbWindowModule,
    NbCardModule,
    NbActionsModule,
    NbProgressBarModule,

} from '@nebular/theme';
import {SampleLayoutComponent} from "./@theme/layouts/sample/sample.layout";
import {APP_BASE_HREF} from '@angular/common';
import {GlobalErrorHandlerService} from './@core/utils/global-error-handler.service';
import {EntityDebugComponent} from './entity-debug/entity-debug.component';
import {DebugDataViewerComponent} from './entity-debug/debug-data-viewer/debug-data-viewer.component';
import {
    BlockedTemplatesComponent
} from './entity-debug/debug-data-viewer/blocked-templates/blocked-templates.component';
import {PageTestDataComponent} from './entity-debug/debug-data-viewer/page-test-data/page-test-data.component';
import {NgbTooltipConfig, NgbTooltipModule} from '@ng-bootstrap/ng-bootstrap';
import {RequestComponentComponent} from './request-component/request-component.component';
import {Ng2SmartTableModule} from 'ng2-smart-table';
import {AutoAssetComponent} from './auto-asset/auto-asset.component';
import {AutoAssetV2Component} from './auto-asset-v2/auto-asset-v2.component';
import {DialogModule} from 'primeng/dialog';
import {DividerModule} from 'primeng/divider';
import {MatRippleModule} from '@angular/material/core';
import {MatSlideToggleModule} from "@angular/material/slide-toggle";
import {SidebarModule} from 'primeng-lts/sidebar';
import {TableModule} from 'primeng/table';
import {AutoAssetV2Module} from './auto-asset-v2/auto-asset-v2.module';
import {AutoAssetModule} from "./auto-asset/auto-asset.module";
import {SharedModule} from "./auto-asset-v2/shared/shared.module";

@NgModule({
    declarations: [AppComponent,
        BuSelectionComponent,
        EntityDebugComponent,
        DebugDataViewerComponent,
        BlockedTemplatesComponent,
        PageTestDataComponent,
        RequestComponentComponent,
        AutoAssetV2Component,
        AutoAssetComponent,
    ],
    bootstrap: [AppComponent],
    imports: [
        BrowserModule,
        MatInputModule,
        BrowserAnimationsModule,
        HttpClientModule,
        HttpModule,
        AppRoutingModule,
        NbSidebarModule.forRoot(),
        NbMenuModule.forRoot(),
        NbDatepickerModule.forRoot(),
        NbDialogModule.forRoot(),
        NbWindowModule.forRoot(),
        NbToastrModule.forRoot(),
        NbChatModule.forRoot({
            messageGoogleMapKey: 'AIzaSyA_wNuCzia92MAmdLRzmqitRGvCF7wCZPY',
        }),
        CoreModule.forRoot(),
        ThemeModule.forRoot(),
        ToasterModule,
        NbCardModule,
        FormsModule,
        MatTableModule,
        NbActionsModule,
        MatFormFieldModule,
        MatIconModule,
        MatButtonModule,
        MatTooltipModule,
        NgbModule,
        IgxDropDownModule,
        NgSelect2Module,
        TagInputModule,
        MatAutocompleteModule,
        NgMultiSelectDropDownModule.forRoot(),
        NgSelectModule,
        NbProgressBarModule,
        NgbTooltipModule,
        MatPaginatorModule,
        AutoAssetModule,
        Ng2SmartTableModule,
        DialogModule,
        DividerModule,
        MatRippleModule,
        MatSlideToggleModule,
        SidebarModule,
        TableModule,
        AutoAssetV2Module,
        SharedModule,
    ],

    exports: [MatTableModule],

    providers: [
        {provide: APP_BASE_HREF, useValue: '/'},
        {
            provide: ErrorHandler,
            useClass: GlobalErrorHandlerService,
        },
    ],
})
export class AppModule {
}
