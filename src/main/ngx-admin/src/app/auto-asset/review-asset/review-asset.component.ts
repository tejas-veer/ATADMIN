import {Component, OnInit, ViewChild} from '@angular/core';
import {AssetImageComponent} from '../shared-component/asset-image/asset-image.component';
import {LocalDataSource} from 'ng2-smart-table';
import {AutoAssetService} from '../../@core/data/auto_asset/auto-asset.service';
import {TriToggleComponent} from '../shared-component/tri-toggle/tri-toggle.component';
import {ToastingService} from '../../@core/utils/toaster.service';
import {PreviewModalComponent} from '../shared-component/preview-modal/preview-modal.component';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {AutoAssetConstantsService} from '../../@core/data/auto-asset-constants.service';
import {UtilService} from "../../@core/utils/util.service";
import { TitleAssetService } from '../../@core/data/auto_asset/title-asset.service';

@Component({
    selector: 'review-asset',
    templateUrl: './review-asset.component.html',
    styleUrls: ['./review-asset.component.css','../shared-style.css'],
})
export class ReviewAssetComponent implements OnInit {

    reviewAssetData: any = [];
    filteredData = [];
    reviewedData: Array<any> = [];
    showLoader = false;
    showPreviewButtonLoader = false;

    acceptAllIsChecked = false;
    rejectAllIsChecked = false;

    existingMappedAssetListOnKeyValue: any[];

    settings = {};
    source: LocalDataSource = new LocalDataSource();
    assetColumnConfig: any = {
        title: 'Asset',
        type: 'custom',
        filter: false,
        renderComponent: AssetImageComponent,
        valuePrepareFunction: (cell, row) => {
            return row.assetValue;
        },
    };
    columns = {
        checkBox: {
            title: 'Accept / Reject',
            type: 'custom',
            filter: false,
            renderComponent: TriToggleComponent,
            onComponentInitFunction: (instance) => {
                instance.selectEvent.subscribe((data) => {
                    this.handleReviewedRowData(data);
                });
            },
        },
        asset: this.assetColumnConfig,
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
                return row.hasOwnProperty('extAssetId') ? row.extAssetId : 'NULL';
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
        adminName: {
            title: 'Admin Name',
            filter: false,
        },
        adminId: {
            title: 'Admin Id',
            filter: false,
        },
        updationDate: {
            title: 'Updation Date',
            filter: false,
        },
    };
    actions: {
        add: false,
        edit: false,
        delete: false,
    };

    selectedEntityName: any = null;
    selectedEntityValue: any = null;
    selectedAssetType: string;
    selectedAssetId: any = null;
    selectedSetId: string = null;
    selectedSize: string = null;
    selectedAdminName: string = null;
    selectedAssetValue: string = null;
    entityNameMap  = Object.keys(this.titleAssetService.EntityNameMapForTitle);


    reviewAssetPreviewModalColumns = {
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
    };
    itemsPerPage: number = 10;
    cursorUpdationDate: string = null;
    cursorId: number = 0;

    constructor(private autoAssetService: AutoAssetService,
                private titleAssetService: TitleAssetService,
                private toastingService: ToastingService,
                private configConstantService: AutoAssetConstantsService,
                public modalService: NgbModal,
                public utilService: UtilService) {
        this.settings = {
            actions: false,
            noDataMessage: '',
        };
    }

    ngOnInit() {
    }

    resetFilters() {
        this.selectedEntityName = null;
        this.selectedEntityValue = null;
        this.selectedAssetId = null;
        this.selectedSetId = null;
        this.selectedSize = null;
        this.selectedAssetValue = null;
    }

    resetData() {
        this.acceptAllIsChecked = false;
        this.rejectAllIsChecked = false;
        this.reviewedData = [];
        this.filteredData = [];
        this.reviewAssetData = [];
        this.cursorId = 0;
        this.cursorUpdationDate = null;
    }


    async getReviewAsset() {
        this.showLoader = true;
        let entityNameForBackend = null;
        if (this.utilService.isSet(this.selectedEntityName))
            entityNameForBackend = this.titleAssetService.EntityNameMapForTitle[this.selectedEntityName].value;
        await this.autoAssetService.getAssetToReview(this.selectedAssetType, this.itemsPerPage, this.cursorUpdationDate, this.cursorId,
            entityNameForBackend, this.selectedEntityValue, this.selectedAssetId, this.selectedSetId, this.selectedAdminName, this.selectedAssetValue)
            .then((response) => {
                let numberOfAssetsFetched: number = 0;
                for (const asset of response['assetList']) {
                    this.reviewAssetData.push(asset);
                    numberOfAssetsFetched += 1;
                }
                // todo: disable next button if below are undefined
                this.cursorUpdationDate = response['updation_date'];
                this.cursorId = response['cursor_id'];
                this.reviewAssetData.map((item) => {
                    if (!this.utilService.isSet(item['isReviewed'])) {
                        item['isReviewed'] = false;
                    }
                });
                this.loadNg2Table(this.reviewAssetData);
                this.toastingService.success('Fetch Review Assets', `Successfully Fetched ${numberOfAssetsFetched} Assets For Review`);
                this.showLoader = false;
                setTimeout(() => {
                    this.source.setPaging(Math.floor(this.reviewAssetData.length / this.itemsPerPage), this.itemsPerPage, true);
                }, 100);
            })
            .catch((error) => {
                this.showLoader = false;
            });
    }

    loadNg2Table(data) {
        if (data && data.length > 0) {
            this.setNg2TableSettings();
            this.source.load(data);
        }
    }

    setNg2TableSettings(): void {
        if (this.selectedAssetType === this.configConstantService.ASSET_TYPE_TITLE) {
            delete this.columns.asset;
        } else if (this.selectedAssetType === this.configConstantService.ASSET_TYPE_IMAGE && !this.columns.asset) {
            this.columns = {
                asset: this.assetColumnConfig,
                ...this.columns,
            };
        }
        this.settings = {
            columns: this.columns,
            actions: this.actions,
            pager: {
                perPage: 1,
                display: true,
            },
        };
    }
    handleReviewedRowData(data) {
        const rowData = data.rowData;
        const state = data.state;
        if (rowData.isActive == state || state == '-1') {
            rowData.isActive = -1;
            rowData.isReviewed = false;
        } else {
            rowData.isActive = Number.parseInt(state);
            rowData.isReviewed = true;
        }
        this.reviewedData = this.reviewAssetData.filter((item) => item['isReviewed']);
        this.source.load(this.reviewAssetData);
    }

    acceptAll(event) {
        this.rejectAllIsChecked = false;
        const isChecked = event.target.checked;
        this.filteredData = this.reviewAssetData;
        this.filteredData.forEach((item) => {
            item.isActive = isChecked ? 1 : -1;
            item.isReviewed = isChecked;
            return item;
        });
        this.reviewedData = this.reviewAssetData.filter((item) => item['isReviewed']);
        this.source.load(this.filteredData);
    }

    rejectAll(event) {
        this.acceptAllIsChecked = false;
        const isChecked = event.target.checked;
        this.filteredData = this.reviewAssetData;
        this.filteredData.forEach((item) => {
            item.isActive = isChecked ? 0 : -1;
            item.isReviewed = isChecked;
            return item;
        });
        this.reviewedData = this.reviewAssetData.filter((item) => item['isReviewed']);
        this.source.load(this.filteredData);
    }

    async saveReviewedData() {
        this.showLoader = true;
        await this.autoAssetService.updateReviewAssetDetails(this.reviewedData)
            .then(() => {
                this.resetData();
            })
            .catch(() => {
                console.error('error in saving reviewed data');
            })
            .finally(() => {
                this.showLoader = false;
                this.resetData();
            });
    }

    extractEntities(data) {
        const keyValueSet = new Set();
        data.forEach(obj => {
            if (obj.hasOwnProperty('keyValue')) {
                keyValueSet.add(obj['keyValue']);
            }
        });
        return Array.from(keyValueSet);
    }

    async getMappedAssetListOnKeyValue() {
        this.showPreviewButtonLoader = true;

        const selectedRow = this.reviewAssetData.filter((item) => item['isReviewed']);
        const keyValueListJoined = this.extractEntities(selectedRow).join(',');
        await this.autoAssetService.getMappedAssetListOnKeyValue(keyValueListJoined)
            .then((response) => {
                this.existingMappedAssetListOnKeyValue = response;
            })
            .catch((error) => {
                this.existingMappedAssetListOnKeyValue = [];
            })
            .finally(() => {
                this.showPreviewButtonLoader = false;

            });
    }

    getActiveMappedList() {
        return this.existingMappedAssetListOnKeyValue.filter((item) => item.isActive);
    }

    async previewModal() {
        if (this.reviewedData.length <= 0) {
            this.toastingService.warning('Nothing to Preview', 'There\'s No Change In Assets');
        } else {
            await this.getMappedAssetListOnKeyValue();
            const previewModal = this.modalService.open(PreviewModalComponent, {
                size: 'lg',
                scrollable: true,
            });
            previewModal.componentInstance.previewData = this.reviewedData;
            if (this.selectedAssetType === this.configConstantService.ASSET_TYPE_IMAGE)
                previewModal.componentInstance.previewModalName = this.configConstantService.REVIEW_IMAGE_ASSET_PREVIEW_MODAL;
            else
                previewModal.componentInstance.previewModalName = this.configConstantService.REVIEW_TITLE_ASSET_PREVIEW_MODAL;
            previewModal.componentInstance.receivedColumns = this.reviewAssetPreviewModalColumns;
            previewModal.componentInstance.imageUrlKeyName = 'assetValue';
            previewModal.componentInstance.existingMappedAssetList = this.existingMappedAssetListOnKeyValue;
            previewModal.componentInstance.activeMappedAssetList = this.getActiveMappedList();

            const childInstance: PreviewModalComponent = previewModal.componentInstance as PreviewModalComponent;
            childInstance.removeRowEvent.subscribe((rowData) => {
                const data = {'rowData': rowData, 'state': '-1'};
                this.handleReviewedRowData(data);
                previewModal.componentInstance.previewData = this.reviewedData;
                if (this.reviewedData.length <= 0) {
                    previewModal.dismiss('close');
                }
            });
            childInstance.saveButtonEvent.subscribe(() => {
                this.saveReviewedData();
            });
        }
    }

    changeAssetTypeToReview(event) {
        this.resetData();
        this.resetFilters();
        this.selectedAssetType = event;
    }

    setPager() {
        this.source.setPaging(1, this.itemsPerPage, true);
    }
}
