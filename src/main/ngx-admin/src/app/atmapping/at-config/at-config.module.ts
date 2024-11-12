import { NbCardModule } from '@nebular/theme';


import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AtConfigRoutingModule } from './at-config-routing.module';
import { ConfigInputBoxComponent } from './config-input-box/config-input-box.component';
import { DefaultConfigComponent } from './default-config/default-config.component';
import { ConfigInputComponent } from './default-config/components/config-input/config-input.component';
import { ConfigViewerComponent } from './default-config/components/config-viewer/config-viewer.component';
import { MatTableModule } from '@angular/material/table';
import { ThemeModule } from '../../@theme/theme.module';
import {Ng2SmartTableModule} from "ng2-smart-table";
import {NguiAutoCompleteModule} from "@ngui/auto-complete";
import {MatSlideToggleModule} from '@angular/material/slide-toggle';
import {MatInputModule} from '@angular/material/input';
import {MatChipsModule} from '@angular/material/chips';
import {MatCheckboxModule} from '@angular/material/checkbox';
import { MatSortModule } from '@angular/material/sort';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatFormFieldModule } from '@angular/material/form-field';
import {TagInputModule} from "ngx-chips";
import { ATReportingModule } from '../../atreporting/atreporting.module';
import {MatTooltipModule} from '@angular/material/tooltip';


@NgModule({
  declarations: [
    ConfigInputBoxComponent,
    DefaultConfigComponent,
    ConfigInputComponent,
    ConfigViewerComponent
  ],

  imports: [
 
    AtConfigRoutingModule,
    ThemeModule,
    CommonModule,
    NguiAutoCompleteModule,
    Ng2SmartTableModule,
    MatSlideToggleModule,
    MatInputModule,
    MatTableModule,
    MatChipsModule,
    MatCheckboxModule,
    MatSortModule,
    MatPaginatorModule,
    MatFormFieldModule,
    ATReportingModule,
    TagInputModule,
    NbCardModule,
    MatTooltipModule,
  ],
  entryComponents: [],
  providers: []
})
export class AtConfigModule { }
