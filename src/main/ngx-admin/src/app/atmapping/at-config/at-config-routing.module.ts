import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {ConfigInputBoxComponent} from "./config-input-box/config-input-box.component";
import {DefaultConfigComponent} from "./default-config/default-config.component";

const routes: Routes = [
  {
    path: 'i',
    component: ConfigInputBoxComponent
  },
  {
    path: 'd/:level/:entityId',
    component: DefaultConfigComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AtConfigRoutingModule { }