import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CheckBoxComponent } from '../shared-component/check-box/check-box.component'
import { Ng2SmartTableModule } from 'ng2-smart-table';
import { RemovePreviewRowComponent } from '../shared-component/preview-modal/remove-preview-row/remove-preview-row.component';
import { NgSelectModule } from "@ng-select/ng-select";
import { MatChipsModule } from '@angular/material/chips';
import { MatRippleModule } from '@angular/material/core';
import { MatIconModule } from '@angular/material/icon';
import {MatSidenavModule} from '@angular/material/sidenav';


@NgModule({
    declarations: [
        CheckBoxComponent,
        RemovePreviewRowComponent,
    ],
    imports: [
        CommonModule,
        FormsModule,
        Ng2SmartTableModule,
        NgSelectModule,
        MatChipsModule,
        MatRippleModule,
        MatIconModule,
        MatSidenavModule
    ]
})
export class AutoSuggestModule {
}

