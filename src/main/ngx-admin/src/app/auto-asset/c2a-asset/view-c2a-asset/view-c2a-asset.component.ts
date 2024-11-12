import { Component, OnInit, Output } from '@angular/core';
import { AutoAssetService } from '../../../@core/data/auto_asset/auto-asset.service';
import { AutoAssetConstantsService } from '../../../@core/data/auto-asset-constants.service';
import { ToastingService } from '../../../@core/utils/toaster.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { PreviewModalComponent } from '../../shared-component/preview-modal/preview-modal.component';
import { LocalDataSource } from 'ng2-smart-table';
import { AssetBlockUnblockComponent } from '../../shared-component/asset-block-unblock/asset-block-unblock.component';
import { AutoAssetEntityDropdownService } from '../../../@core/data/auto_asset/auto-asset-entity-dropdown.service';
import { C2aAssetService } from '../../../@core/data/auto_asset/c2a-asset.service';
import { UtilService } from '../../../@core/utils/util.service';

@Component({
  selector: 'view-c2a-asset',
  templateUrl: './view-c2a-asset.component.html',
  styleUrls: ['./view-c2a-asset.component.css', '../../shared-style.css'],
})
export class ViewC2aAssetComponent implements OnInit {

  isCollapsed: boolean = false;

  dimensionMap: Map<string, any> = new Map();
  entityNameMap = this.c2aAssetService.EntityNameMapForC2A;
  entityName: any;
  entityValue: any;
  domain: any;

  toggledRows: any[] = [];
  filteredData: any[] = [];
  dimensionInput: any;
  assetData: any[] = [];

  showLoader = false;
  waitingMessage = ''

  source: LocalDataSource = new LocalDataSource();

  previewColumns = {
    entityName: {
      title: 'Entity Name',
      filter: false,
    },
    entityValue: {
      title: 'Entity Value',
      filter: false,
    },
    keyValue: {
      title: 'Key Value',
      filter: false,
    },
    assetType: {
      title: 'Asset Type',
      filter: false,
    },
    assetId: {
      title: 'Asset Id',
      filter: false,
    },
    extAssetId: {
      title: 'External Asset Id',
      filter: false,
      valuePrepareFunction: (cell, row) => {
        return row.hasOwnProperty('extAssetId') ? row.extAssetId : "NULL";
      },
    },
    assetValue: {
      title: 'Asset Value',
      filter: false,
    },
    basis: {
      title: 'Basis',
      filter: false,
    },
    setId: {
      title: 'Set Id',
      filter: false,
    },
    size: {
      title: 'Size',
      filter: false,
    },
    isActive: {
      title: 'Is Active',
      filter: false,
    },
    adminId: {
      title: 'Admin Id',
      filter: false,
    },
  }

  columns = {
    blockUnblock: {
      title: 'Map/Unmap',
      type: 'custom',
      filter: false,
      renderComponent: AssetBlockUnblockComponent,
      onComponentInitFunction: (instance) => {
        instance.toggledEvent.subscribe((data) => {
          this.toggledRowData(data);
        });
      },
    },
    ...this.previewColumns
  };
  actions: {
    add: false,
    edit: false,
    delete: false,
  };

  settings: any;

  constructor(public autoAssetService: AutoAssetService,
    private c2aAssetService: C2aAssetService,
    private toastingService: ToastingService,
    private configConstantService: AutoAssetConstantsService,
    private modalService: NgbModal,
    public dropDownService: AutoAssetEntityDropdownService,
    public utilService: UtilService,
  ) { }

  ngOnInit(): void {
    this.dropDownService.setEntityNameMap(this.entityNameMap);
    const entityNameOptions = Object.keys(this.entityNameMap);
    this.settings = {
      actions: false,
      noDataMessage: ''
    }

    this.dimensionMap.set('EntityName', {
      options: entityNameOptions,
      selectedValue: entityNameOptions[1],
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

    this.entityName = this.dimensionMap.get('EntityName');
    this.entityValue = this.dimensionMap.get('EntityValue');
    this.domain = this.dimensionMap.get('Domain');
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

  toggleUnmapAll(event) {
    this.assetData.forEach((item) => {
      if (event.target.checked === true) {
        if (item.isToggled && item.isActive) {
          item.isToggled = false;
          item.isActive = false;
        } else if (item.isActive === true) {
          item.isToggled = true;
          item.isActive = false;
        }
      } else {
        if (item.isToggled && !item.isActive) {
          item.isToggled = false;
          item.isActive = !item.isActive;
        }
      }
    });
    this.toggledRows = this.assetData.filter((item) => item.isToggled);
    this.source.load(this.assetData);
  }


  resetDimensionData(selectedDimension) {
    this.toggledRows = [];
    this.dimensionMap.get(selectedDimension).options = [];
    this.dimensionMap.get(selectedDimension).selectedValue = null;
  }

  async fetchAsset() {
    if (!this.isFetchButtonEnabled()) {
      this.toastingService.warning(`Incomplete Input`, `Enter entity details to fetch assets`);
    }
    else {
      this.resetAssetData();
      const selectedEntityName = this.dropDownService.getSelectedEntityNameValue(this.entityName);
      const selectedEntityValueJoined = this.getEntityValue();
      this.waitingMessage = "Fetching..."
      this.showLoader = true;
      await this.autoAssetService.getMappedAssetListOnKey(selectedEntityName, selectedEntityValueJoined,
        this.configConstantService.ASSET_TYPE_C2A)
        .then((response) => {
          this.assetData = response;
          this.updateNg2Table();
        })
        .catch((error) => {
          this.showLoader = false;
        })
        .finally(() => {
          this.waitingMessage = ""
        });
    }
  }

  getEntityValue() {
    if (this.c2aAssetService.isAdditionAlInputRequired(this.dropDownService.getSelectedEntityNameValue(this.entityName))) {
      return this.domain.selectedValue + '$$' + this.dropDownService.getCommaSeparatedEntityValue(this.entityValue);
    }
    return this.dropDownService.getCommaSeparatedEntityValue(this.entityValue);
  }

  updateNg2Table(): void {
    this.assetData.map((item) => {
      item['isToggled'] = false;
    });
    this.toggledRows = [];
    this.loadNg2Table(this.assetData);
    this.showLoader = false;
    this.toastingService.success('Fetch Assets', `Successfully Fetched ${this.assetData.length} Assets`);
  }

  loadNg2Table(data) {
    if (data && data.length > 0) {
      this.settings = {
        columns: this.columns,
        actions: this.actions,
        pager: {
          perPage: 10,
          display: true,
        }
      }
      this.source.load(data)
    }
  }

  toggledRowData(rowData: any) {
    rowData.isToggled = !rowData.isToggled;
    rowData.isActive = !rowData.isActive;
    const index = this.toggledRows.findIndex(item => item.id === rowData.id);
    if (index !== -1) {
      this.toggledRows.splice(index, 1);
    } else {
      this.toggledRows.push(rowData);
    }
    this.source.load(this.assetData);
  }

  updateAsset() {
    this.resetAssetData();
    this.waitingMessage = "Updating..."
    this.showLoader = true;
    this.autoAssetService.blockUnblockAssets(this.toggledRows)
      .then(() => {
        this.resetAfterUpdate();
        this.fetchAsset();
      })
      .catch(() => {
        this.showLoader = false;
        this.resetWaitingMsg()
      })
  }

  resetAssetData() {
    this.assetData = []
  }

  resetAfterUpdate() {
    this.toggledRows = [];
  }

  resetWaitingMsg() {
    this.waitingMessage = ''
  }

  isEntityValueDisabled() {
    return this.dropDownService.getSelectedEntityNameValue(this.entityName) == 'GLOBAL';
  }

  isFetchButtonEnabled(): boolean {
    const isAdditionalInputRequired = this.c2aAssetService.isAdditionAlInputRequired(this.dropDownService.getSelectedEntityNameValue(this.entityName));
    const isEntityValueSelected = this.utilService.isSet(this.entityName.selectedValue) && this.utilService.isSet(this.entityValue.selectedValue);
    const isDomainSelected = this.utilService.isSet(this.domain.selectedValue);

    return this.isEntityValueDisabled() || (isEntityValueSelected && (!isAdditionalInputRequired || isDomainSelected));
  }



  previewModal() {
    if (this.toggledRows.length <= 0) {
      this.toastingService.warning("Nothing to Preview", "There's No Change In Assets")
    } else {
      const previewModal = this.modalService.open(PreviewModalComponent, {
        size: "lg",
        scrollable: true,
      });
      previewModal.componentInstance.previewData = this.toggledRows;
      previewModal.componentInstance.previewModalName = this.configConstantService.KBB_MAP_TITLE_ASSET_PREVIEW_MODAL;
      previewModal.componentInstance.receivedColumns = this.previewColumns;
      const childInstance: PreviewModalComponent = previewModal.componentInstance as PreviewModalComponent;
      childInstance.removeRowEvent.subscribe((removedPreviewAssetRow) => {
        this.toggledRowData(removedPreviewAssetRow);
        if (this.toggledRows.length <= 0) {
          previewModal.dismiss();
        }
      });
      childInstance.saveButtonEvent.subscribe(() => {
        this.updateAsset()
      });
    }

  }


}
