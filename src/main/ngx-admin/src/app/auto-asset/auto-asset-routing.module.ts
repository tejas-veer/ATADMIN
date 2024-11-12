import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ViewAssetComponent } from './view-asset/view-asset.component';
import { MapImageComponent } from './auto-suggest/map-image.component';
import { ReviewAssetComponent } from './review-asset/review-asset.component';
import { ConfigTabComponent } from './config-tab/config-tab.component';
import {ViewTitleAssetComponent} from "./view-title/view-title-asset.component";
import { C2aAssetComponent } from './c2a-asset/c2a-asset.component';


const routes: Routes = [
  { path: 'view_image_asset', component: ViewAssetComponent },
  { path: 'map_asset', component: MapImageComponent },
  { path: 'review_asset', component: ReviewAssetComponent },
  { path: 'title_asset', component: ViewTitleAssetComponent},
  { path: 'c2a_asset', component: C2aAssetComponent},
  { path: 'review_asset', component: ReviewAssetComponent },
  { path: '', redirectTo: 'view_image_asset', pathMatch: 'full' },
  { path: 'aa_config', component: ConfigTabComponent},
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class AutoAssetRoutingModule { }
