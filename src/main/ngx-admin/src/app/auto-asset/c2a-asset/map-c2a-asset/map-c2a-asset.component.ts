import { Component, OnInit } from '@angular/core';
import { AutoAssetService } from '../../../@core/data/auto_asset/auto-asset.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ToastingService } from '../../../@core/utils/toaster.service';
import { AutoAssetConstantsService } from '../../../@core/data/auto-asset-constants.service';
import { BulkUploadModalComponent } from '../../shared-component/bulk-upload-modal/bulk-upload-modal.component';
import { LocalDataSource } from 'ng2-smart-table';
import { RemovePreviewRowComponent } from '../../shared-component/preview-modal/remove-preview-row/remove-preview-row.component';
import { PreviewModalComponent } from '../../shared-component/preview-modal/preview-modal.component';
import { AutoAssetEntityDropdownService } from '../../../@core/data/auto_asset/auto-asset-entity-dropdown.service';
import { BulkUploadService } from '../../../@core/data/auto_asset/bulk-upload.service';
import { C2aAssetService } from '../../../@core/data/auto_asset/c2a-asset.service';
import { UtilService } from '../../../@core/utils/util.service';

@Component({
  selector: 'map-c2a-asset',
  templateUrl: './map-c2a-asset.component.html',
  styleUrls: ['./map-c2a-asset.component.css','../../shared-style.css'],
})
export class MapC2aAssetComponent implements OnInit {

  source: LocalDataSource = new LocalDataSource();

  dimensionMap: Map<string, any> = new Map();
  entityNameMap = this.c2aAssetService.EntityNameMapForC2A;
  entityName: any;
  entityValue: any;
  domain: any;
  ctaValue: any;

  dimensionInput: any = '';
  c2aInput: any = '';
  addedAssetData = [];

  mapLoader: boolean;
  waitingMessage = ''
  settings: any;
  isCollapsed: boolean = false;


  columns = {
    entity_name: {
      title: 'Entity Name',
      filter: false,
    },
    entity_value: {
      title: 'Entity Value',
      filter: false,
    },
    asset_value: {
      title: 'Asset Value',
      filter: false,
    },
    set_id: {
      title: 'Set Id',
      filter: false,
    },
    is_active: {
      title: 'Is Active',
      filter: false,
    },
    remove: {
      title: "Remove",
      filter: false,
      type: "custom",
      renderComponent: RemovePreviewRowComponent,
      onComponentInitFunction: (instance) => {
        instance.removeRow.subscribe((rowData) => {
          this.removeRow(rowData);
        });
      }
    }
  };
  showLoader: boolean;

  constructor(public autoAssetService: AutoAssetService,
    private c2aAssetService: C2aAssetService,
    public modalService: NgbModal,
    private bulkUploadService: BulkUploadService,
    private toastingService: ToastingService,
    private configConstantService: AutoAssetConstantsService,
    public dropDownService: AutoAssetEntityDropdownService,
    public utilService : UtilService,
  ) { }

  ngOnInit(): void {
    this.dropDownService.setEntityNameMap(this.entityNameMap);
    const entityNameOptions = Object.keys(this.entityNameMap).filter((item) => item != 'Global');

    this.settings = {
      actions: false,
      noDataMessage: ''
    }

    this.dimensionMap.set('EntityName', {
      options: entityNameOptions,
      selectedValue: entityNameOptions[0],
      showLoader: false,
    });
    this.dimensionMap.set('EntityValue', {
      options: [],
      selectedValue: null,
      showLoader: false,
    });
    this.dimensionMap.set('Domain', {
      options: [],
      selectedValue: null,
      showLoader: false,
    });
    this.dimensionMap.set('ManualC2A', {
      options: [],
      selectedValue: null,
      showLoader: false,
    });

    this.entityName = this.dimensionMap.get('EntityName');
    this.entityValue = this.dimensionMap.get('EntityValue');
    this.domain = this.dimensionMap.get('Domain');
    this.ctaValue = this.dimensionMap.get('ManualC2A');
  }

  toggleCollapse() {
    this.isCollapsed = !this.isCollapsed;
  }

  async getDropdownOptionSuggestion(event, selectedDimension, analyticDimensionName) {
    const dimension = this.dimensionMap.get(selectedDimension);
    dimension.showLoader = true;
    this.dimensionInput = event.target.value;
    try {
      const options = await this.dropDownService.getDropdownOptionsForInput(analyticDimensionName, this.dimensionInput);
      dimension.options = options;
    } finally {
      dimension.showLoader = false;
    }
  }

  handleCommaSeparatedQueryValue(event: any, dimension) {
    const inputArray = this.dimensionInput.split(',').map((value) => value.trim());
    const uniqueValues = new Set([...dimension.selectedValue, ...inputArray]);
    dimension.selectedValue = Array.from(uniqueValues);
    if (inputArray.length > 1 && dimension.selectedValue.length > 0) {
      dimension.selectedValue = dimension.selectedValue.filter(element => element !== this.dimensionInput)
    }
    this.resetDimensionInput();
  }

  addValueIfNotAlreadyPresent(data) {
    const existingKeys = new Set(this.addedAssetData.map(obj => `${obj.entity_name}-${obj.entity_value}-${obj.asset_value}`));
    data.forEach(newObj => {
      const key = `${newObj.entity_name}-${newObj.entity_value}-${newObj.asset_value}`;
      if (!existingKeys.has(key)) {
        this.addedAssetData.push(newObj);
        existingKeys.add(key);
      }
    });
    return this.addedAssetData;
  }

  handleInput(event) {
    this.dimensionInput = event.target.value;
  }

  handleC2aInput(event) {
    this.c2aInput = event.target.value;
  }

  formatToNg2List(list) {
    return list.flatMap((item) => {
      return this.dropDownService.getSelectedEntityNameValue(this.entityName) != "GLOBAL"
        ? this.dropDownService.getEntityIdsFromSelectedEntityValues(this.entityValue).map((entityValue) => ({
          entity_name: this.dropDownService.getSelectedEntityNameValue(this.entityName),
          entity_value: this.getEntityValueForPreview(),
          asset_value: item,
          set_id: 1,
          is_active: true,
        }))
        : [
          {
            entity_name: this.dropDownService.getSelectedEntityNameValue(this.entityName),
            entity_value: null,
            asset_value: item,
            set_id: 1,
            is_active: true,
          },
        ];
    });
  }

  getEntityValueForPreview() {
    const selectedEntityValues = this.dropDownService.getEntityIdsFromSelectedEntityValues(this.entityValue);
    if (this.c2aAssetService.isAdditionAlInputRequired(this.dropDownService.getSelectedEntityNameValue(this.entityName))) {
      return this.domain.selectedValue + "$$" + selectedEntityValues;
    }
    return selectedEntityValues;
  }

  loadAssetsToTable(data) {
    if (data && data.length > 0) {
      this.settings = {
        columns: this.columns,
        actions: false,
        noDataMessage: '',
        pager: {
          perPage: 15,
          display: true,
        }
      }
      this.source.load(data)
    }

  }

  async mapAsset() {
    this.showLoader = true;
    this.waitingMessage = "Mapping...";
    const mappedRow = this.addedAssetData;
    const assetType = this.configConstantService.ASSET_TYPE_C2A;
    await this.autoAssetService.mapAsset(mappedRow, assetType)
      .then(() => {
        this.resetAfterMap();
      })
      .catch(() => {
      })
      .finally(() => {
        this.showLoader = false;
        this.resetWaitingMsg()
      })
  }

  async bulkMapping(payload) {
    this.showLoader = true;
    this.waitingMessage = 'Mapping...';
    const assetType = this.configConstantService.ASSET_TYPE_C2A;
    const response = await this.autoAssetService.mapAsset(payload, assetType)
      .then((response) => {
      })
      .catch((error) => {
        this.toastingService.error('Mapping Failed', "Unable to map title");
      })
      .finally(() => {
        this.showLoader = false;
        this.resetWaitingMsg();
      });
  }

  showC2aToastIfDisabled() {
    if (!this.isAddC2aEnabled()) {
      this.toastingService.warning(`Incomplete Input`, `Enter entity details to enter C2A`);
    }
  }

  addManualAsset() {
    const newData = this.formatToNg2List(this.c2aInput.split(','));
    this.addedAssetData = this.addValueIfNotAlreadyPresent(newData);
    this.loadAssetsToTable(this.addedAssetData);
    this.ctaValue.selectedValue = [];
    this.c2aInput = '';
  }

  isEntityValueDisabled() {
    return this.dropDownService.getSelectedEntityNameValue(this.entityName) == 'GLOBAL';
  }

  isAddC2aEnabled(): boolean {
    const isAdditionalInputRequired = this.c2aAssetService.isAdditionAlInputRequired(this.dropDownService.getSelectedEntityNameValue(this.entityName));
    const isEntityValueSelected = this.utilService.isSet(this.entityName.selectedValue) && this.utilService.isSet(this.entityValue.selectedValue);
    const isDomainSelected = this.utilService.isSet(this.domain.selectedValue);

    return !this.isEntityValueDisabled() && isEntityValueSelected && (!isAdditionalInputRequired || isDomainSelected);
}

  resetAfterMap() {
    this.addedAssetData = []
    this.ctaValue.selectValue = []
  }

  removeRow(rowData) {
    const index = this.addedAssetData.findIndex(item => item.assetValue == rowData.assetValue && item.entityName == rowData.entityName && item.entityValue == rowData.entityValue);
    if (index !== -1) {
      this.addedAssetData.splice(index, 1);
    } 
    this.source.load(this.addedAssetData);
  }


  resetWaitingMsg() {
    this.waitingMessage = '';
  }

  resetDimensionInput() {
    this.dimensionInput = '';
  }

  resetCtaValues() {
    this.ctaValue.selectedValue = []
  }

  resetDimensionData(dimension) {
    this.dimensionMap.get(dimension).options = [];
    this.dimensionMap.get(dimension).selectedValue = null;
  }

  openBulkC2AMappingModal() {
    const bulkModal = this.modalService.open(BulkUploadModalComponent, {
      size: "md",
      scrollable: true,
    });

    bulkModal.componentInstance.parentComponentName = this.configConstantService.TITLE_GENERATION;
    bulkModal.componentInstance.modalHeader = "Bulk C2A Mapping";
    bulkModal.componentInstance.modalSaveButtonName = "Map";
    bulkModal.componentInstance.sampleCsvData = this.bulkUploadService.c2aMappingSampleCsvData;
    bulkModal.componentInstance.sampleCsvColumns = this.bulkUploadService.c2aMappingBulkUploadColumns;
    bulkModal.componentInstance.csvColumns = this.bulkUploadService.c2aMappingBulkUploadColumns;
    const childInstance: BulkUploadModalComponent = bulkModal.componentInstance as BulkUploadModalComponent;
    childInstance.generateEvent.subscribe((bulkData) => {
      this.bulkMapping(bulkData);
    });
  }

  previewModal() {
    if (this.addedAssetData.length <= 0) {
      this.toastingService.warning("Nothing to Preview", "There's No Change In Assets")
    } else {
      const previewModal = this.modalService.open(PreviewModalComponent, {
        size: "lg",
        scrollable: true,
      });
      previewModal.componentInstance.previewData = this.addedAssetData;
      previewModal.componentInstance.previewModalName = this.configConstantService.KBB_MAP_TITLE_ASSET_PREVIEW_MODAL;
      previewModal.componentInstance.receivedColumns = this.columns;
      const childInstance: PreviewModalComponent = previewModal.componentInstance as PreviewModalComponent;
      childInstance.removeRowEvent.subscribe((removedPreviewAssetRow) => {
        this.removeRow(removedPreviewAssetRow);
        previewModal.componentInstance.previewData = this.addedAssetData;
        if (this.addedAssetData.length <= 0) {
          previewModal.dismiss();
        }
      });
      childInstance.saveButtonEvent.subscribe(() => {
        this.mapAsset()
      });
    }

  }

}
