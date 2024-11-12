import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {ThemeModule} from '../@theme/theme.module';
import {Ng2SmartTableModule} from 'ng2-smart-table';
import {NgSelectModule} from '@ng-select/ng-select';
import {NgbModule, NgbNavModule, NgbProgressbarModule} from '@ng-bootstrap/ng-bootstrap';
import {BrowserModule} from '@angular/platform-browser';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {FormsModule} from '@angular/forms';
import {MatTooltipModule} from '@angular/material/tooltip';
import {SidebarModule} from 'primeng/sidebar';
import {ButtonModule} from 'primeng/button';
import {TableModule} from 'primeng-lts/table';
import {FileUploadModule} from 'primeng-lts/fileupload';
import {SharedModule} from './shared/shared.module';
import {MatButtonToggleModule} from '@angular/material/button-toggle';
import {MatIconModule} from '@angular/material/icon';
import {MatChipsModule} from '@angular/material/chips';
import {MatRippleModule} from '@angular/material/core';
import {MatDividerModule} from '@angular/material/divider';
import {NbCheckboxModule} from '@nebular/theme';
import {MatSidenavModule} from '@angular/material/sidenav';
import {MatProgressBarModule} from '@angular/material/progress-bar';
import {MatPaginatorModule} from '@angular/material/paginator';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {MatSlideToggleModule} from '@angular/material/slide-toggle';
import {DialogModule} from 'primeng/dialog';
import {CheckboxModule} from 'primeng-lts/checkbox';
import {HttpClientModule} from '@angular/common/http';
import {DividerModule} from 'primeng/divider';
import {ToastModule} from 'primeng/toast';
import {NoDataComponent} from './shared/no-data/no-data.component';

@NgModule({
  declarations: [
  ],
  imports: [
    CommonModule,
    ThemeModule,
    Ng2SmartTableModule,
    NgSelectModule,
    NgbModule,
    BrowserModule,
    BrowserAnimationsModule,
    FormsModule, // Keep only once
    MatTooltipModule,
    SidebarModule,
    ButtonModule,
    TableModule,
    FileUploadModule,
    SharedModule,
    NgbNavModule,
    MatButtonToggleModule,
    MatIconModule,
    MatChipsModule,
    MatRippleModule,
    MatDividerModule,
    NbCheckboxModule,
    MatSidenavModule,
    MatProgressBarModule,
    NgbProgressbarModule,
    MatPaginatorModule,
    MatProgressSpinnerModule,
    MatSlideToggleModule,
    DialogModule,
    CheckboxModule,
    HttpClientModule,
    DividerModule,
    ToastModule,
  ],
})
export class AutoAssetV2Module { }
