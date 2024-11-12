import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {RouterModule, Routes} from '@angular/router';
import {EntityInputComponent} from './entity-input/entity-input.component';
import {CmReportingComponent} from "./cm-reporting/cm-reporting.component";
import {ATReportingComponent} from "./atreporting.component";

const routes: Routes = [
    {
        path: '',
        component: ATReportingComponent,
        children: [
            { path: 'MAX', component: EntityInputComponent },
            { path: 'CM', component: CmReportingComponent },
        ],
    },
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule],
})

export class ATReportingRoutingModule {
}
