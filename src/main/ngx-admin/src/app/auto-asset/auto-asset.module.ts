import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ThemeModule } from '../@theme/theme.module';
import { Ng2SmartTableModule } from 'ng2-smart-table';
import { AssetImageComponent } from './shared-component/asset-image/asset-image.component';
import { NgSelectModule } from "@ng-select/ng-select";
import { AssetBlockUnblockComponent } from './shared-component/asset-block-unblock/asset-block-unblock.component';
import { AutoAssetRoutingModule } from './auto-asset-routing.module';
import { ViewAssetComponent } from "./view-asset/view-asset.component";
import { MapImageComponent } from './auto-suggest/map-image.component';
import { AutoSuggestModule } from './auto-suggest/auto-suggest.module';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { NgbNavModule } from '@ng-bootstrap/ng-bootstrap';
import { ReviewAssetComponent } from './review-asset/review-asset.component';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { ImageUploadModalComponent } from './shared-component/image-upload-modal/image-upload-modal.component';
import { TriToggleComponent } from './shared-component/tri-toggle/tri-toggle.component';
import { MatIconModule } from '@angular/material/icon';
import { PreviewModalComponent } from './shared-component/preview-modal/preview-modal.component';
import { MatChipsModule } from '@angular/material/chips';
import { MatRippleModule } from '@angular/material/core';
import { MatDividerModule } from '@angular/material/divider';
import { NbCheckboxModule } from '@nebular/theme';
import { BulkUploadModalComponent } from './shared-component/bulk-upload-modal/bulk-upload-modal.component';
import { MatSidenavModule } from '@angular/material/sidenav';
import { SidebarModule } from 'primeng-lts/sidebar';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { NgbProgressbarModule } from '@ng-bootstrap/ng-bootstrap';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ViewTitleAssetComponent } from './view-title/view-title-asset.component';
import { ConfigTabComponent } from './config-tab/config-tab.component';
import { SidePanelComponent } from './shared-component/side-panel/side-panel.component';
import { C2aAssetComponent } from './c2a-asset/c2a-asset.component';
import { ViewC2aAssetComponent } from './c2a-asset/view-c2a-asset/view-c2a-asset.component';
import { MapC2aAssetComponent } from './c2a-asset/map-c2a-asset/map-c2a-asset.component';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { FormsModule } from '@angular/forms';
import { NotesComponent } from './c2a-asset/notes/notes.component';
import { ImageWithToggleButtonComponent } from './shared-component/image-with-toggle-button/image-with-toggle-button.component';
import { DynamicWidthCellComponent } from './shared-component/dynamic-width-cell/dynamic-width-cell.component';

@NgModule({
    declarations: [
        ViewAssetComponent,
        AssetImageComponent,
        AssetBlockUnblockComponent,
        MapImageComponent,
        ReviewAssetComponent,
        ImageUploadModalComponent,
        TriToggleComponent,
        PreviewModalComponent,
        BulkUploadModalComponent,
        ConfigTabComponent,
        ViewTitleAssetComponent,
        SidePanelComponent,
        C2aAssetComponent,
        ViewC2aAssetComponent,
        MapC2aAssetComponent,
        NotesComponent,
        ImageWithToggleButtonComponent,
        DynamicWidthCellComponent,
    ],
    imports: [
        CommonModule,
        FormsModule,
        ThemeModule,
        Ng2SmartTableModule,
        NgSelectModule,
        AutoAssetRoutingModule,
        AutoSuggestModule,
        NgbModule,
        NgbNavModule,
        MatButtonToggleModule,
        MatIconModule,
        NgbNavModule,
        MatChipsModule,
        MatRippleModule,
        MatDividerModule,
        NbCheckboxModule,
        MatSidenavModule,
        SidebarModule,
        MatProgressBarModule,
        NgbProgressbarModule,
        MatPaginatorModule,
        MatProgressSpinnerModule,
        MatSlideToggleModule
    ]
})
export class AutoAssetModule { }
