import { AfterViewInit, Component, Input, OnInit, ViewChild } from '@angular/core';
import { ConfigFetchService } from '../../../../@core/data/configfetch.service';
import { BlockingRulesComponent } from './components/blocking-rules/blocking-rules.component';
import { ErrorStackViewerComponent } from '../../../../@theme/components/error-stack-viewer/error-stack-viewer.component';
import { ToastingService } from '../../../../@core/utils/toaster.service';
import { TagInputComponent } from 'ngx-chips';
import { UtilService } from '../../util/util.service';
import { Subject } from 'rxjs/Subject';
@Component({
  selector: 'ngx-blocking-input',
  templateUrl: './blocking-input.component.html',
  styleUrls: ['./blocking-input.component.scss']
})
export class BlockingInputComponent implements OnInit, AfterViewInit {

  @ViewChild('blockingRules') blockingRules: BlockingRulesComponent;
  @ViewChild('errorStack') errorStack: ErrorStackViewerComponent;
  @ViewChild('tagsInput') tagsInput: TagInputComponent;

  @Input() title;
  @Input() isDisable;

  All: string = 'ALL';
  selectedType: string = 'Template';
  maxitems: any = { Template: 1, Framework: 100 };
  idsToBlock: string;
  selectedSizes: Array<any> = [];
  sizeList: any = { Template: [], Framework: [] };
  private _ruleInserts: Subject<any> = new Subject<any>();
  get ruleInserts(): Subject<any> {
    if (!this._ruleInserts)
      this.ruleInserts = new Subject<any>();
    return this._ruleInserts;
  }


  set ruleInserts(value: Subject<any>) {
    this._ruleInserts = value;
  }

  constructor(private configService: ConfigFetchService, private toastingService: ToastingService) {
    this.configService.getValidSizes().then(data => {
      this.sizeList.Template = [this.All].concat(data);
      this.sizeList.Framework = [this.All].concat(data);
    }).catch(err => {
      throw 'Not yet caught this error' + err;
    });
  }

  ngAfterViewInit(): void {
    this.blockingRules._setSizeList(this.sizeList);
    this.blockingRules._setMaxItems(this.maxitems);
    this.blockingRules._getSaveEvent().subscribe(saveItems => this.consumeItemsToSave(saveItems));
  }

  ngOnInit() {

  }

  addNewRule(): void {
    if (!this.validate())
      return;
    this.blockingRules.addRule(this.idsToBlock, this.selectedType, this.selectedSizes);
    this.resetInputs();
  }

  validate(): boolean {
    this.errorStack.clear();
    let error = false;
    if (!this.idsToBlock) {
      this.errorStack.pushError({
        title: 'Id not entered',
        message: 'Please Enter a Valid Id or set of ids',
        type: 'error'
      });
      error = true;
    }

    if (this.selectedSizes.length == 0) {
      this.errorStack.pushError({ title: 'No size Selected ', message: 'Please Select a size', type: 'error' });
      error = true;
    }

    if (this.idsToBlock) {
      const transmutedIds = this.idsToBlock.replace(/,/gi, '0');
      if (!UtilService.isNumeric(transmutedIds)) {
        this.errorStack.pushError({
          title: 'Invalid Ids to Block',
          message: 'Ids can only be comma separated numbers',
          type: 'error'
        });
        return false;
      }
    }
    return !error;
  }

  tagAdded(e) {
    if (this.listContainsAll()) {
      this.selectedSizes = [{ value: this.All, display: this.All }];
    }
  }

  listContainsAll(): boolean {
    let items = this.selectedSizes.filter(item => {
      return item.value === this.All;
    });
    return (items.length > 0);
  }

  resetInputs(): void {
    this.selectedSizes = [];
    this.idsToBlock = '';
  }

  consumeItemsToSave(saveItems: any): void {
    this.ruleInserts.next(saveItems);
  }

}
