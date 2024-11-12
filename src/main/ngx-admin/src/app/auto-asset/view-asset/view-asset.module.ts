import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ThemeModule } from '../../@theme/theme.module';
import { Ng2SmartTableModule } from 'ng2-smart-table';
import { AssetImageComponent } from '../shared-component/asset-image/asset-image.component';
import { NgSelectModule } from "@ng-select/ng-select";
import { AssetBlockUnblockComponent } from '../shared-component/asset-block-unblock/asset-block-unblock.component';
import { MatTooltipModule } from '@angular/material/tooltip';
import { NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { FormsModule } from '@angular/forms';


@NgModule({
  declarations: [
    AssetImageComponent,
    AssetBlockUnblockComponent,
  ],
  imports: [
    CommonModule,
    ThemeModule,
    Ng2SmartTableModule,
    NgSelectModule,
    NgbModule,
    BrowserAnimationsModule,
    FormsModule,
    MatTooltipModule,
  ]
})
export class ViewAssetModule { }
