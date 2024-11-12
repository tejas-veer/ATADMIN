import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { MatIconModule } from '@angular/material/icon';
import { IframeComponent } from './components/loader-iframe/iframe.component';
import { BuSelectionComponent } from './../bu-selection/bu-selection.component';
import { InsertUpdateService } from './components/generator-viewer/insert-update.service';
import { ModuleWithProviders, NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  NbActionsModule,
  NbLayoutModule,
  NbMenuModule,
  NbSearchModule,
  NbSidebarModule,
  NbUserModule,
  NbContextMenuModule,
  NbButtonModule,
  NbSelectModule,
  NbIconModule,
  NbThemeModule,
  NbCardModule,
} from '@nebular/theme';
import { NbEvaIconsModule } from '@nebular/eva-icons';
import { NbSecurityModule } from '@nebular/security';

import {
  ErrorStackViewerComponent,
  FooterComponent,
  HeaderComponent,
  // SearchInputComponent,
} from './components';
import {
  CapitalizePipe,
  PluralPipe,
  RoundPipe,
  TimingPipe,
  NumberWithCommasPipe,
} from './pipes';
import { DEFAULT_THEME } from './styles/theme.default';
import { COSMIC_THEME } from './styles/theme.cosmic';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {
  OneColumnLayoutComponent,
  SampleLayoutComponent,
  ThreeColumnsLayoutComponent,
  TwoColumnsLayoutComponent,
} from './layouts';
import { MenuItemsComponent } from './components/menu-items/menu-items.component';
import { ThemeSettingsComponent } from './components/theme-settings/theme-settings.component';
import { ThemeSwitcherComponent } from './components/theme-switcher/theme-switcher.component';
import { DatetimeComponent } from './components/datetime/datetime.component';
import { LoaderComponent } from './components/loader/loader.component';
import { GeneratorBetaViewerComponent } from './components/generator-beta-viewer/generator-beta-viewer.component';
import { ConfirmationModalComponent } from './components/generator-beta-viewer/confirmation-modal/confirmation-modal.component';
import { WhitelistingModalComponent } from './components/generator-beta-viewer/whitelisting-modal/whitelisting-modal.component';
import { GeneratorViewerComponent } from './components/generator-viewer/generator-viewer.component';
import { ReportIssueModalComponent } from './components/generator-beta-viewer/report-issue-modal/report-issue-modal.component';
import { CreativeViewerComponent } from './components/creative-viewer/creative-viewer.component';
import { FloatingButtonComponent } from './components/floating-button/floating-button.component';
import { MatButtonModule } from '@angular/material/button';
import { MatTooltipModule } from '@angular/material/tooltip';

const BASE_MODULES = [CommonModule, FormsModule, ReactiveFormsModule];


const NB_MODULES = [
  NbLayoutModule,
  NbMenuModule,
  NbUserModule,
  NbActionsModule,
  NbSearchModule,
  NbSidebarModule,
  NbContextMenuModule,
  NbSecurityModule,
  NbButtonModule,
  NbSelectModule,
  NbIconModule,
  NbEvaIconsModule,
  FormsModule,
  NbCardModule,
  NbActionsModule,
  NbUserModule,
  MatIconModule,
  MatButtonModule,
  NgbModule,
  MatTooltipModule
];
const COMPONENTS = [
  HeaderComponent,
  FooterComponent,
  // SearchInputComponent,
  OneColumnLayoutComponent,
  ThreeColumnsLayoutComponent,
  TwoColumnsLayoutComponent,
  SampleLayoutComponent,
  MenuItemsComponent,
  ThemeSwitcherComponent,
  ThemeSettingsComponent,
  DatetimeComponent,
  LoaderComponent,
  IframeComponent,
  CreativeViewerComponent,
  FloatingButtonComponent,
  GeneratorViewerComponent,
];
const PIPES = [
  CapitalizePipe,
  PluralPipe,
  RoundPipe,
  TimingPipe,
  NumberWithCommasPipe,
];

const NB_THEME_PROVIDERS = [
  ...NbThemeModule.forRoot(
      {
          name: 'cosmic',
      },
      [DEFAULT_THEME, COSMIC_THEME],
  ).providers,
  ...NbSidebarModule.forRoot().providers,
  ...NbMenuModule.forRoot().providers,
  InsertUpdateService
];

@NgModule({
  imports: [CommonModule, ...NB_MODULES, ...BASE_MODULES],
  exports: [CommonModule,
     ...PIPES,
      ...COMPONENTS,
      ...NB_MODULES, 
      ...BASE_MODULES],
  declarations: [
    ...COMPONENTS, ...PIPES, 
    MenuItemsComponent, 
    ThemeSettingsComponent, 
    ThemeSwitcherComponent, 
    DatetimeComponent, 
    LoaderComponent, GeneratorBetaViewerComponent, ConfirmationModalComponent, 
    ReportIssueModalComponent, 
    WhitelistingModalComponent, GeneratorViewerComponent, ReportIssueModalComponent, CreativeViewerComponent, FloatingButtonComponent
  ],
})
export class ThemeModule {
  static forRoot(): ModuleWithProviders<ThemeModule> {
    return {
      ngModule: ThemeModule,
      providers: [
        ...NbThemeModule.forRoot(
          {
            name: 'default',
          },
          [ DEFAULT_THEME, COSMIC_THEME, 
          ],
        ).providers,
      ],
    };
  }
}
