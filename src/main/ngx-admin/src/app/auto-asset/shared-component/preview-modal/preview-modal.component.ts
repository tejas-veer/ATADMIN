import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { LocalDataSource } from 'ng2-smart-table';
import { AssetImageComponent } from '../asset-image/asset-image.component';
import { RemovePreviewRowComponent } from './remove-preview-row/remove-preview-row.component';
import { AutoAssetService } from '../../../@core/data/auto_asset/auto-asset.service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { DomSanitizer } from '@angular/platform-browser';
import { AutoAssetConstantsService } from '../../../@core/data/auto-asset-constants.service';
import { ToastingService } from '../../../@core/utils/toaster.service';

@Component({
  selector: 'preview-modal',
  templateUrl: './preview-modal.component.html',
  styleUrls: ['./preview-modal.component.css']
})
export class PreviewModalComponent implements OnInit {

  @Input() activeMappedAssetList: any[] = [];
  @Input() existingMappedAssetList: any[] = [];
  @Input() entityName: any;
  @Input() entityValue: any = [];
  @Input() previewModalName;
  @Input() previewData: any[];
  @Input() receivedColumns;
  @Input() imageUrlKeyName;
  @Output() removeRowEvent = new EventEmitter<any>();
  @Output() saveButtonEvent = new EventEmitter<any>();

  settings = {};
  assetData = [];
  source: LocalDataSource = new LocalDataSource();
  columns = {};
  actions: {
    add: false,
    edit: false,
    delete: false,
  }

  assetRenderedColumn = {
    asset: {
      title: 'Asset',
      type: 'custom',
      filter: false,
      renderComponent: AssetImageComponent,
      valuePrepareFunction: (cell, row) => {
        return row[this.imageUrlKeyName];
      }
    }
  }

  removeColumn = {
    remove: {
      title: "Remove",
      filter: false,
      type: "custom",
      renderComponent: RemovePreviewRowComponent,
      onComponentInitFunction: (instance) => {
        instance.removeRow.subscribe((rowData) => {
          this.removePreviewRow(rowData);
        });
      }
    }
  }

  viewAssetIsActiveColumn = {
    isActive: {
      title: 'Action',
      type: 'html',
      filter: false,
      valuePrepareFunction: (cell, row) => {
        const color = row.isActive ? '#40dc7e' : '#EE4B2B';
        const status = row.isActive ? 'Unblocked' : 'Blocked';
        const html = `<div style="color: ${color}">${status}</div>`;
        return this.sanitizer.bypassSecurityTrustHtml(html);
      }
    }
  }

  reviewAssetIsActiveColumn = {
    isActive: {
      title: 'Action',
      type: 'html',
      filter: false,
      valuePrepareFunction: (cell, row) => {
        const color = row.isActive == "1" ? '#40dc7e' : '#EE4B2B';
        const status = row.isActive == "1" ? 'Accepted' : 'Rejected';
        const html = `<div style="color: ${color}">${status}</div>`;
        return this.sanitizer.bypassSecurityTrustHtml(html);
      }
    }
  }

  entityValueMap: any = {};
  limitMappingForReviewMap: any = new Map();
  limitMappingForReviewArray = [];
  currentCount;
  activeCount;

  constructor(public activeModal: NgbActiveModal,
    public configConstantService: AutoAssetConstantsService,
    private autoAssetService: AutoAssetService,
    private sanitizer: DomSanitizer,
    private toastingService: ToastingService
  ) {
  }

  ngOnInit(): void {
    this.handleColumns()
    this.settings = {
      columns: this.columns,
      actions: this.actions,
      noDataMessage: '',
    };
    this.source.load(this.previewData);
    this.addUniqueKeyToReviewActiveAsset()
    this.handleEntityMap();
  }

  handleColumns() {
    if (this.previewModalName === this.configConstantService.VIEW_ASSET_PREVIEW_MODAL) {
      this.columns = { ...this.assetRenderedColumn, ...this.receivedColumns, ...this.viewAssetIsActiveColumn, ...this.removeColumn }
    }
    else if (this.previewModalName === this.configConstantService.REVIEW_IMAGE_ASSET_PREVIEW_MODAL) {
      this.columns = { ...this.assetRenderedColumn, ...this.receivedColumns, ...this.reviewAssetIsActiveColumn, ...this.removeColumn }
    }
    else if (this.previewModalName === this.configConstantService.REVIEW_TITLE_ASSET_PREVIEW_MODAL) {
      this.columns = { ...this.receivedColumns, ...this.reviewAssetIsActiveColumn, ...this.removeColumn };
    }
    else if (this.previewModalName === this.configConstantService.KBB_MAP_IMAGE_ASSET_PREVIEW_MODAL) {
      this.columns = { ...this.assetRenderedColumn, ...this.receivedColumns, ...this.removeColumn }
    }
    else if (this.previewModalName === this.configConstantService.SD_MAP_ASSET_PREVIEW_MODAL) {
      this.columns = { ...this.assetRenderedColumn, ...this.receivedColumns, ...this.removeColumn }
    }
    else if (this.previewModalName === this.configConstantService.KBB_MAP_TITLE_ASSET_PREVIEW_MODAL) {
      this.columns = { ...this.receivedColumns, ...this.removeColumn }
    }
    else {
      this.columns = { ...this.receivedColumns}
    }
  }

  handleEntityMap() {
    if (this.previewModalName == this.configConstantService.REVIEW_IMAGE_ASSET_PREVIEW_MODAL) {
      this.setMapLimitMapForReview();
    }
    else {
      this.setEntityValueMap();
    }
  }

  extractAndAddUniqueEntitiesWithSedId(data) {
    const keyValueSetIdKeySet = new Set();
    data.forEach(item => {
      if (item.hasOwnProperty('keyValue') && item.hasOwnProperty('setId')) {
        item['keyValueAndSetId'] = item.keyValue + "-" + item.setId;
        keyValueSetIdKeySet.add(item.keyValue + "-" + item.setId);
      }
    });
    return Array.from(keyValueSetIdKeySet);
  }

  addUniqueKeyToReviewActiveAsset() {
    if (this.previewModalName == this.configConstantService.REVIEW_IMAGE_ASSET_PREVIEW_MODAL) {
      this.activeMappedAssetList.forEach((item) => {
        item['keyValueAndSetId'] = item.keyValue + "-" + item.setId;
      })
    }
  }


  setMapLimitMapForReview() {
    const keyValueSetIdList = this.extractAndAddUniqueEntitiesWithSedId(this.previewData);
    keyValueSetIdList.forEach((key: string) => {
      const [entityName, entityValue, setId] = this.extractEntityInfoFromKey(key);

      this.limitMappingForReviewMap.set(key, {
        entityName: entityName,
        entityValue: entityValue,
        setId: setId,
        currentCount: this.getCurrentCount(key),
        activeCount: this.getActiveCount(key),
        limitExceed: this.getLimitExceed(),
      }
      )
    })
    this.limitMappingForReviewArray = Array.from(this.limitMappingForReviewMap.values());
  }

  extractEntityInfoFromKey(key: string): [string, string, string] {
    const entity = key.split('@@');
    const entityName = entity[1];
    const [entityValue, setId] = entity[2].split('-');
    return [entityName, entityValue, setId];
  }

  getCurrentCount(key) {
    this.currentCount = this.previewData.filter((item) => item.keyValueAndSetId == key && item.isActive).length;
    return this.currentCount;
  }

  getActiveCount(key) {
    this.activeCount = this.activeMappedAssetList.filter((item) => item.keyValueAndSetId == key).length;
    return this.activeCount;
  }

  getLimitExceed() {
    return (this.currentCount + this.activeCount) > this.autoAssetService.MAX_MAP_LIMIT;
  }

  getRemainingLimitForKeyValueSetId(entry) {
    const diff = this.autoAssetService.MAX_MAP_LIMIT - entry.activeCount;
    if (diff > 0) return diff;
    return 0;
  }

  generateMessageForReviewMapLimit(entry) {
    let message = `Currently you have selected <strong>${entry.currentCount}</strong> assets 
    for ${entry.entityName.toLowerCase()} <strong>${entry.entityValue}</strong> for set id <strong>${entry.setId}</strong>.&nbsp;`

    if (this.getRemainingLimitForKeyValueSetId(entry) > 0) {
      message += `You can only map <strong>${this.getRemainingLimitForKeyValueSetId(entry)}</strong> new assets. `
    }
    else {
      message += `You can <strong>not</strong> map any new assets.`
    }

    return message;
  }

  isMapLimitExceedForReview(): boolean {
    return this.limitMappingForReviewArray.some(item => item.limitExceed == true);
  }


  setEntityValueMap() {
    this.entityValue.forEach((item) => {
      this.entityValueMap[item] = {
        currentCount: this.getCurrentCountForEntityValue(item),
        activeCount: this.getActiveAssetCountForEntityValue(item),
        limitExceed: this.isMapLimitExceedForEntityValue(item),
      }
    })
  }


  isExistingActiveLimitExceed(entityValue) {
    return this.entityValueMap[entityValue]?.activeCount > this.autoAssetService.MAX_MAP_LIMIT;
  }

  isSelectionLimitExceed(entityValue) {
    return !this.isExistingActiveLimitExceed(entityValue) && (this.entityValueMap[entityValue]?.activeCount + this.entityValueMap[entityValue].currentCount > this.autoAssetService.MAX_MAP_LIMIT);
  }

  getRemainingLimit(entityValue) {
    const diff = this.autoAssetService.MAX_MAP_LIMIT - this.entityValueMap[entityValue]?.activeCount;
    if (diff > 0) return diff;
    return 0;

  }

  getActiveAssetCountForEntityValue(entityValue) {
    return this.activeMappedAssetList.filter((item) => item.entityValue == entityValue).length;
  }


  getCurrentCountForEntityValue(entityValue) {
    return this.previewData.filter((item) => item['entity_value'] == entityValue).length;
  }

  isMapLimitExceedForEntityValue(entityValue) {
    return (this.getActiveAssetCountForEntityValue(entityValue) + this.getCurrentCountForEntityValue(entityValue)) > this.autoAssetService.MAX_MAP_LIMIT;
  }

  isMapLimitExceed(): boolean {
    const mapValues = Object.values(this.entityValueMap) as { isMapLimitExceed: boolean }[];
    return mapValues.some(item => item['limitExceed'] == true);
  }


  generateMessageForEntityValue(entityValue) {
    let message = `Currently you have selected <strong>${this.entityValueMap[entityValue]?.currentCount}</strong> assets 
    for ${this.entityName.toLowerCase()} <strong>${entityValue}</strong>.`

    if (this.getRemainingLimit(entityValue) > 0) {
      message += `You can only map <strong>${this.getRemainingLimit(entityValue)}</strong> new assets. `
    }
    else {
      message += `You can <strong>not</strong> map any new assets.`
    }

    return message;
  }

  getEntityValueKeys() {
    return Object.keys(this.entityValueMap);
  }

  removePreviewRow(rowData) {
    this.removeRowEvent.emit(rowData);
    this.handleEntityMap();
    this.source.load(this.previewData);
  }

  saveChanges() {
    if (!this.isSaveButtonDisable()) {
      this.saveButtonEvent.emit();
      this.activeModal.close()
    }
    else {
      this.toastingService.warning("Map Limit Exceed", "Please block some assets to continue")
    }
  }

  isSaveButtonDisable() {
    if (this.isMapping()) {
      return this.isMapLimitExceed() || this.isMapLimitExceedForReview();
    }
    return false;
  }

  isMapping() {
    return this.previewModalName == this.configConstantService.KBB_MAP_IMAGE_ASSET_PREVIEW_MODAL
      || this.previewModalName == this.configConstantService.SD_MAP_ASSET_PREVIEW_MODAL
      || this.previewModalName == this.configConstantService.REVIEW_IMAGE_ASSET_PREVIEW_MODAL;
  }

  isReviewModalOpen() {
    return this.previewModalName == this.configConstantService.REVIEW_IMAGE_ASSET_PREVIEW_MODAL;
  }

}
