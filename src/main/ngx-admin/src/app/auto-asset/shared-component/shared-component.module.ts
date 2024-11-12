import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgSelectModule } from "@ng-select/ng-select";
import { ImageUploadModalComponent } from './image-upload-modal/image-upload-modal.component';
import { MatIconModule } from '@angular/material/icon';
import { NgbNavModule } from '@ng-bootstrap/ng-bootstrap';
import { TriToggleComponent } from './tri-toggle/tri-toggle.component';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { PreviewModalComponent } from './preview-modal/preview-modal.component';
import { Ng2SmartTableModule } from 'ng2-smart-table';
import { BulkUploadModalComponent } from './bulk-upload-modal/bulk-upload-modal.component';
import { SidePanelComponent } from './side-panel/side-panel.component';
import { FormsModule } from '@angular/forms';
import { MatPaginatorModule } from '@angular/material/paginator';
import { SidebarModule } from 'primeng-lts/sidebar';
import { ImageWithToggleButtonComponent } from './image-with-toggle-button/image-with-toggle-button.component';
import { AssetImageComponent } from './asset-image/asset-image.component';
import { AssetBlockUnblockComponent } from './asset-block-unblock/asset-block-unblock.component';
import { DynamicWidthCellComponent } from './dynamic-width-cell/dynamic-width-cell.component';

@NgModule({
  declarations: [
    ImageUploadModalComponent,
    TriToggleComponent,
    PreviewModalComponent,
    BulkUploadModalComponent,
    SidePanelComponent,
    ImageWithToggleButtonComponent,
    AssetImageComponent,
    AssetBlockUnblockComponent,
    DynamicWidthCellComponent,
  ],
  imports: [
    CommonModule,
    NgSelectModule,
    MatIconModule,
    NgbNavModule,
    MatButtonToggleModule,
    Ng2SmartTableModule,
    SidebarModule,
    MatPaginatorModule,
    FormsModule
  ]
})
export class SharedComponentModule { }
