import {RouterModule, Routes} from '@angular/router';
import {NgModule} from '@angular/core';
import {InputBoxComponent} from './input-box/input-box.component';
import {AtmappingComponent} from './atmapping.component';
import {EntityIdGuard} from './entity-id.guard';

const routes: Routes = [
    {
        path: '',
        component: InputBoxComponent,
    },
    {
        path: 'cfg',
        loadChildren: () => import('./at-config/at-config.module')
          .then(m => m.AtConfigModule),
      },
    {
        canActivate: [EntityIdGuard],
        path: 'query',
        component: AtmappingComponent,
    },
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule],
})

export class AtmappingRoutingModule {
}
