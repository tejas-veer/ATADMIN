import {RequestComponentComponent} from './request-component/request-component.component';
import {ExtraOptions, RouterModule, Routes} from '@angular/router';
import {NgModule} from '@angular/core';
import {EntityIdGuard} from './atmapping/entity-id.guard';
import {BuSelectionComponent} from './bu-selection/bu-selection.component';
import {EntityDebugComponent} from './entity-debug/entity-debug.component';
import { AutoAssetComponent } from './auto-asset/auto-asset.component';
import {AutoAssetV2Component} from "./auto-asset-v2/auto-asset-v2.component";

export const routes: Routes = [
    {
        path: 'atreporting',
        loadChildren: () => import('./atreporting/atreporting.module')
            .then(m => m.ATReportingModule),
    },
    {
        path: 'at/:section',
        loadChildren: () => import('./atmapping/atmapping.module')
            .then(m => m.AtmappingModule),
    },
    {
        path: 'at/mapping',
        loadChildren: () => import('./atmapping/atmapping.module')
            .then(m => m.AtmappingModule),
    },
    {
        path: "",
        redirectTo: 'at/mapping',
        pathMatch: 'full'
    },
    {
        path: 'debug',
        component: EntityDebugComponent
    },
    {
        path: 'request',
        component: RequestComponentComponent
    },
    {
        path: 'buselection',
        component: BuSelectionComponent
    },
    {
        path: 'auto_asset/v1',
        loadChildren: () => import('./auto-asset/auto-asset.module').then(m => m.AutoAssetModule),
        component: AutoAssetComponent,
    },
    {
        path: 'auto_asset/v2',
        component: AutoAssetV2Component,
    },
];

const config: ExtraOptions = {
    useHash: true,
};

@NgModule({
    imports: [RouterModule.forRoot(routes, config)],
    exports: [RouterModule],
    providers: [EntityIdGuard],
})
export class AppRoutingModule {
}
