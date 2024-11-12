import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { AutoAssetService } from "../../../@core/data/auto_asset/auto-asset.service";
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { DomSanitizer } from '@angular/platform-browser';
import { CsvService } from '../../../@core/data/csv.service';
import { ConversionMapService } from '../../../@core/data/conversion-map.service';
import { AutoAssetConstantsService } from '../../../@core/data/auto-asset-constants.service';
import { ToastingService } from '../../../@core/utils/toaster.service';
import { ngxCsv } from 'ngx-csv';
import { ImageSourceEnum } from '../../../@core/data/image-source.enum';import { BulkUploadService } from '../../../@core/data/auto_asset/bulk-upload.service';
import { ImageAssetService } from '../../../@core/data/auto_asset/image-asset.service';
;

@Component({
  selector: 'image-upload-modal',
  templateUrl: './image-upload-modal.component.html',
  styleUrls: ['./image-upload-modal.component.css']
})
export class ImageUploadModalComponent implements OnInit {
  @Input() modalName: any;
  @Output() mapEvent = new EventEmitter<any>();
  @Output() bulkMapEvent = new EventEmitter<any>();

  HOST_IMAGE_MODAL;
  LOCAL_IMAGE_UPLOAD;
  MAX_CSV_SIZE = 1024 * 1024;
  MAX_CSV_ROW = 500;
  MAX_IMAGE_FILE_SIZE_MB = 1;
  MAX_IMAGE_FILE_SIZE_BYTE = this.MAX_IMAGE_FILE_SIZE_MB * 1024 * 1024;


  dimensionMap: Map<string, any> = new Map();
  filterInput: any;
  selectedFiles = [];
  imageUrl = null;
  csvFile = null;
  parsingMsg: string = '';
  activeTab = 'image_upload';
  parsedBulkData: any[] = [];

  entityNameMap: any;

  imageSource: any;
  entityName: any;
  entityValue: any;
  entityAssetSize: any;

  constructor(public autoAssetService: AutoAssetService,
    private imageAssetService: ImageAssetService,
    private bulkUploadService : BulkUploadService,
    public modal: NgbActiveModal,
    private sanitizer: DomSanitizer,
    private toastingService: ToastingService,
    private csvService: CsvService,
    private conversionMapService: ConversionMapService,
    private configConstantService: AutoAssetConstantsService,
  ) {
    this.HOST_IMAGE_MODAL = this.configConstantService.HOSTED_IMAGE_UPLOAD_MODAL;
    this.LOCAL_IMAGE_UPLOAD = this.configConstantService.LOCAL_IMAGE_UPLOAD_MODAL;

    const imageSourceOptions = Object.values(ImageSourceEnum);
    this.entityNameMap = this.imageAssetService.EntityNameMapForImage;
    const entityNameOptions = Object.keys(this.entityNameMap);

    this.dimensionMap.set("ImageSource", {
      options: imageSourceOptions,
      selectedValue: null,
      showLoader: false,
    });
    this.dimensionMap.set("EntityName", {
      options: entityNameOptions,
      selectedValue: entityNameOptions[0],
      showLoader: false,
    });
    this.dimensionMap.set("EntityValue", {
      options: [],
      selectedValue: [],
      showLoader: false,
    });
    this.dimensionMap.set("EntityAssetSize", {
      options: [],
      selectedValue: null,
      showLoader: false,
    });

    this.imageSource = this.dimensionMap.get('ImageSource');
    this.entityName = this.dimensionMap.get('EntityName');
    this.entityValue = this.dimensionMap.get('EntityValue');
    this.entityAssetSize = this.dimensionMap.get('EntityAssetSize');

  }

  ngOnInit(): void {
  }

  getSelectedEntityName() {
    return this.entityName.selectedValue;
  }

  getSelectedEntityNameValue() {
    return this.entityNameMap[this.entityName.selectedValue].value;
  }

  getSelectedEntityNamePlaceholderValue() {
    return this.entityNameMap[this.getSelectedEntityName()].placeholder;
  }

  getSelectedEntityNameDropdownValue() {
    return this.entityNameMap[this.getSelectedEntityName()].dropDownValue;
  }

  getSelectedEntityValue() {
    const entityValues = this.entityValue.selectedValue.map((item) => {
      const extractedEntityValue = this.autoAssetService.extractIdUsingRegex(item);
      if (this.imageAssetService.isSizeSelectVisible(this.getSelectedEntityNameValue())) {
        return extractedEntityValue + this.configConstantService.DOUBLE_DOLLAR + this.getSelectedEntityAssetSize();
      }
      return extractedEntityValue;
    })

    return entityValues;
  }

  getSelectedEntityAssetSize() {
    return this.entityAssetSize.selectedValue;
  }

  async getDropdownOptionSuggestion(event, selectedDimension) {
    const dimension = this.dimensionMap.get(selectedDimension);
    dimension.showLoader = true;
    this.filterInput = event.target.value;

    if (selectedDimension == "EntityAssetSize") {
      selectedDimension = "Template Size"
    }
    else {
      selectedDimension = this.getSelectedEntityNameDropdownValue();
    }

    await this.autoAssetService.getAutoSelectSuggestions(
      selectedDimension,
      this.filterInput
    )
      .then((data) => {
        const dimensionEnumName = this.conversionMapService.frontendToBackendEnumMap[selectedDimension]
          .toLowerCase();

        const convertKeysToLowerCase = (obj) => {
          const converted = {};
          for (const key in obj) {
            if (Object.prototype.hasOwnProperty.call(obj, key)) {
              converted[key.toLowerCase()] = obj[key];
            }
          }
          return converted;
        };
        dimension.options = Object.values(data).map((item) => {
          const itemLowerCaseKeys = convertKeysToLowerCase(item);
          if (itemLowerCaseKeys[dimensionEnumName] !== undefined && itemLowerCaseKeys[dimensionEnumName] !== null) {
            return itemLowerCaseKeys[dimensionEnumName];
          }
          return '';
        });

      })
      .finally(() => {
        dimension.showLoader = false;
      })
  }

  resetDimensionData(selectedDimension) {
    this.dimensionMap.get(selectedDimension).options = [];
    this.dimensionMap.get(selectedDimension).selectedValue = [];
    this.resetEntityAssetSize();
  }

  resetEntityAssetSize() {
    if (!this.imageAssetService.isSizeSelectVisible(this.getSelectedEntityNameValue())) {
      this.entityAssetSize.selectedValue = null;
    }
  }

  handleCsvUpload(event) {
    this.csvFile = event.target.files[0];
    this.checkCSV();
  }

  getCsvFileSizeMB() {
    if (this.csvFile) {
      return (this.csvFile.size / (1024 * 1024)).toFixed(2);
    }
  }

  async checkCSV(): Promise<void> {
    if (!this.csvFile) {
      this.parsingMsg = 'Please select a CSV file.';
      return;
    }

    try {
      this.parsedBulkData = await this.csvService.parseCSV(this.csvFile);
      if (this.parsedBulkData.length > this.MAX_CSV_ROW) {
        this.parsingMsg = `Number of rows exceeds the ${this.MAX_CSV_ROW} limit`;
        return;
      }
      const mandatoryFields = ['entity_name', 'entity_value', 'asset_value', 'set_id'];
      this.parsingMsg = this.csvService.checkMandatoryFields(
        this.parsedBulkData,
        mandatoryFields
      );
    } catch (error) {
      this.parsingMsg = 'Error parsing the CSV.';
    }
  }

  mapBulkUrlImage() {
    if (this.isMapBulkHostedImageButtonVisible()) {
      this.bulkMapEvent.emit(this.parsedBulkData);
      this.modal.close()
    }
  }

  mapUrlImage() {
    if (!this.entityName.selectedValue || this.entityValue.selectedValue.length == 0) {
      this.toastingService.warning("Incomplete Input", 'Entity name or value not selected.');
    } else if (!this.imageUrl || this.imageUrl.length <= 0) {
      this.toastingService.warning("Incomplete Input", 'Image URL not entered.');
    } else {
      const payload = this.generatePayloadForMapUrlImage();
      this.mapEvent.emit(payload);
      this.modal.close()
    }
  }

  generatePayloadForMapUrlImage() {
    const entityValues = this.getSelectedEntityValue();

    const payload = entityValues.map(entityValue => ({
      entity_name: this.getSelectedEntityNameValue(),
      entity_value: entityValue,
      asset_size: this.getSelectedEntityAssetSize(),
      asset_value: this.imageUrl,
      set_id: this.imageAssetService.ImageSourceMap[this.imageSource.selectedValue].setId,
    }));

    return payload;
  }

  mapLocalImage() {
    if (!this.entityName.selectedValue || this.entityValue.selectedValue.length == 0) {
      this.toastingService.warning("Incomplete Input", 'Entity name or value not selected.');
    } else if (this.selectedFiles.length <= 0) {
      this.toastingService.warning("Incomplete Input", 'No image file selected.');
    } else {
      this.dimensionMap;
      let payload = {
        entity_name: this.getSelectedEntityNameValue(),
        entity_value: this.getSelectedEntityValue().join(","),
        asset_size: this.getSelectedEntityAssetSize(),
        asset_list: this.selectedFiles,
        set_id: this.imageAssetService.ImageSourceMap[this.imageSource.selectedValue].setId,
      }
      this.mapEvent.emit(payload);
      this.modal.close()
    }
  }

  handleFileInput(event: any) {
    const files = event.target.files;

    let totalFileSize = this.selectedFiles.reduce((totalSize, selectedFile) => totalSize + selectedFile.value.size, 0);
    for (let i = 0; i < files.length; i++) {
      totalFileSize += files[i].size;
    }

    if (totalFileSize >= this.MAX_IMAGE_FILE_SIZE_BYTE) {
      this.toastingService.warning("File Size Exceeds", `The total size of selected files should be less than ${this.MAX_IMAGE_FILE_SIZE_MB} MB.`);
    }
    else if (this.selectedFiles.length + event.target.files.length > this.autoAssetService.MAX_LOCAL_FILE_LIMIT) {
      this.toastingService.warning("File Limit Exceed", `Whoops! You can select only up to ${this.autoAssetService.MAX_LOCAL_FILE_LIMIT} files.`);
    }
    else {
      for (let i = 0; i < files.length; i++) {
        const file = files[i];
        if (!this.isDuplicate(file)) {
          const data = {
            value: file,
            url: this.sanitizer.bypassSecurityTrustUrl(URL.createObjectURL(file))
          }
          this.selectedFiles.unshift(data);
        }
      }
    }
  }

  deleteImage(deletedItem) {
    this.selectedFiles = this.selectedFiles.filter((item) => item.value.name != deletedItem.value.name);
  }

  isDuplicate(file) {
    return this.selectedFiles.some((item) => item.value.name == file.name);
  }

  isMapHostedImageButtonVisible() {
    const isEntitySelected = this.entityName.selectedValue && this.entityValue.selectedValue.length != 0 && this.imageSource.selectedValue;
    const imageUrlNotEntered = this.imageUrl == null || this.imageUrl.length <= 0;
    return isEntitySelected && !imageUrlNotEntered;
  }

  isMapBulkHostedImageButtonVisible() {
    const fileSelected = this.csvFile != null;
    const isFileNotExceedSize = fileSelected && this.csvFile.size <= this.MAX_CSV_SIZE;
    const isRowsInLimit = fileSelected && this.parsedBulkData.length < this.MAX_CSV_ROW;

    return fileSelected && isFileNotExceedSize && isRowsInLimit;
  }

  isMapLocalImageButtonVisible() {
    const isEntitySelected = this.entityName.selectedValue && this.entityValue.selectedValue.length != 0 && this.imageSource.selectedValue;
    const imageFileSelected = this.selectedFiles.length > 0;
    return isEntitySelected && imageFileSelected;
  }

  downloadHostedImageUploadDataCsv() {
    const csvName = 'AA_Image_Upload_Sample';
    const data = this.bulkUploadService.hostedImageSampleCsvData;
    const headers = this.bulkUploadService.hostedImageSampleCsvColumns;
    new ngxCsv(data, csvName, { headers: headers });
  }
}
