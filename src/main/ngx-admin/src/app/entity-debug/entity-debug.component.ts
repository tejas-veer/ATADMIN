import { Component, OnInit } from '@angular/core';
import { Overlay, OverlayContainer, ScrollStrategyOptions } from "@angular/cdk/overlay";
import { ScrollDispatcher, ViewportRuler } from "@angular/cdk/scrolling";
import { MatPaginatorIntl } from "@angular/material/paginator";
import { UniqueSelectionDispatcher } from "@angular/cdk/collections";
import { ToastingService } from "../@core/utils/toaster.service";
import { ActivatedRoute, Router } from "@angular/router";
import { GlobalLoaderService } from "../@core/data/global-loader.service";
import { DebugService } from "../@core/data/debug.service";
@Component({
  selector: 'entity-debug',
  templateUrl: './entity-debug.component.html',
  styleUrls: ['./entity-debug.component.scss'],
  providers: [MatPaginatorIntl, Overlay, UniqueSelectionDispatcher, ScrollStrategyOptions,
    ScrollDispatcher, ViewportRuler, OverlayContainer]
})
export class EntityDebugComponent {

  templateDebugData: any;
  debugURL: string;
  testURL: string;
  rawURL: string;
  showloader: boolean = false;
  advancedoptions: boolean = false;
  params_final: any[] = [];
  cacheThreshold: any[] = [];
  cacheThresholdCopy: any[] = [];
  tsize: string = "300x250";
  addMoreParams: boolean = false;
  boxAddress: string;

  constructor(private toasting: ToastingService,
    private route: ActivatedRoute,
    private router: Router,
    private globalLoader: GlobalLoaderService,
    private debugService: DebugService) {
    this.globalLoader.disable();
    this.route.params.subscribe(params => {
      console.info('redirect fired' + params);
      this.myInit();
      if (params['durl']) {
        this.debugURL = params['durl'];
        this.testURL = this.debugURL + '&test';
        this.rawURL = this.debugURL + '&debug';
        this.getDataFromAPI(this.debugURL);
        this.showloader = true;
      }
    });
  }

  myInit() {
    this.templateDebugData = null;
    this.debugURL = null;
    this.testURL = null;
    this.rawURL = null;
    this.advancedoptions = false;
    this.cacheThreshold = null;
    this.cacheThresholdCopy = null;
  }

  getDataFromAPI(url: string) {
    this.debugService.getTemplateDebugDataFromURL(url + '&debug')
      .then(data => {
        this.showloader = false;
        this.templateDebugData = JSON.parse(data.response);
        this.cleanURL();
        this.setVariables();
        if (this.templateDebugData['tPtd']['RESPONSE_STATE'] !== 2) {
          this.toasting.info("", this.getReason(this.templateDebugData['tPtd']['RESPONSE_STATE']));

        }
        if (this.templateDebugData['cachedThreshold']['EntityBandit::applyTopFrameworkFiltering'] > 80) {
          this.toasting.info("", "Please set the EntityBandit::applyTopFrameworkFiltering flag < 80 in advanced parameters to ensure filtering or refresh.");
          this.toasting.warning("Framework Filtering", "Top framework filtering is not applied for this call");
        }
        else {
          this.toasting.info("", "Green rows contain templates that pass top framework filtering");
        }
      }).catch(() => {

        this.showloader = false;
      });
  }

  showAdvancedOptions() {
    this.advancedoptions = !this.advancedoptions;
  }


  redir(): void {
    if (!this.advancedoptions) {
      this.cleanURL();
    }
    console.log(this.params_final);
    this.makeDebugURL();
    const param = { durl: this.debugURL, adv: this.advancedoptions };
    this.router.navigate(['/debug', param]);
  }


  makeDebugURL() {
    this.debugURL = this.debugService.makeDebugURL(this.boxAddress, this.params_final, this.cacheThreshold);
    this.advancedoptions = !this.advancedoptions;
  }

  cleanURL() {
    let cleanedURL = this.debugService.cleanURL(this.debugURL);
    this.params_final = cleanedURL['paramsFinal'];
    this.tsize = cleanedURL['tsize'];
    this.boxAddress = cleanedURL['boxAddress'];
  }

  setVariables() {
    if (this.templateDebugData['cachedThreshold']) {
      this.cacheThresholdCopy = this.templateDebugData['cachedThreshold']
      this.makecacheThresholdObj();
      this.rawURL = this.debugURL + "&debug";
      this.testURL = this.debugURL + "&test";
    }
  }

  makecacheThresholdObj() {
    this.cacheThreshold = this.debugService.makecacheThresholdObj(this.cacheThresholdCopy);
  }

  addparam(key: string, value: string) {
    if (key.length === 0) {
      this.toasting.error("Key empty", "Please enter a non empty key")
    }
    else if (this.containsKey(key)) {
      this.toasting.error("Duplicate Found", "key already exists");
    }
    else {
      this.params_final.push({ 'key': key, 'value': value });
    }
    this.addMoreParams = false;
  }

  containsKey(key: string): boolean {
    for (let params of this.params_final) {
      if (params['key'].toLowerCase() === key.toLowerCase()) return true;
    }
    return false;
  }

  toggleMoreParams() {
    this.addMoreParams = !this.addMoreParams;
  }

  getReason(responseState: number): string {
    switch (responseState) {
      case 1:
        return "Auto-Template Disabled";
      case 3:
        return "template API error";
      case 4:
        return "invalid request";
      case 5:
        return "Templates less than threshold";
      default:
        return "abc"
    }
  }

}
