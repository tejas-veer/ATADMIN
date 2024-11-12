import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedComponent } from './shared.component';
import { NoDataComponent } from './no-data/no-data.component';



@NgModule({
  declarations: [
    SharedComponent,
    NoDataComponent,
  ],
  imports: [
    CommonModule,
  ],
  exports: [
    NoDataComponent,
  ]
})
export class SharedModule { }
