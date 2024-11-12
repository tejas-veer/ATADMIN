import { Component, OnInit } from '@angular/core';
import { AutoAssetService } from '../../@core/data/auto_asset/auto-asset.service';
import { ToastingService } from '../../@core/utils/toaster.service';
import { AutoAssetConstantsService } from '../../@core/data/auto-asset-constants.service';
import { UtilService } from '../../@core/utils/util.service';
import { AutoAssetEntityDropdownService } from '../../@core/data/auto_asset/auto-asset-entity-dropdown.service';
import { LocalDataSource } from 'ng2-smart-table';


@Component({
  selector: 'config-tab',
  templateUrl: './config-tab.component.html',
  styleUrls: ['./config-tab.component.css', '../shared-style.css']
})
export class ConfigTabComponent implements OnInit {
  dimensionMap: Map<string, any> = new Map();
  entityNameMap = this.autoAssetService.EntityNameMapForConfig;
  entityName: any;
  entityValue: any;
  domain: any;
  dimensionInput: any;

  isNotesCollapsed: boolean = false;

  isChecked = false
  isEditable = false
  waitingMessage: string = '';
  showLoader: boolean = false;
  showFirstChangeMsg: boolean = true;

  configData = []
  originalConfigDataJson = {}
  configDataJson = {}
  parentsIds: any;
  showAddConfig = false;

  settings = {};
  source: LocalDataSource = new LocalDataSource();

  newConfigDetails: any = {};

  previewColumns = {
    entity_name: {
      title: 'Entity Name',
      filter: false,
    },
    entity_value: {
      title: 'Entity Value',
      filter: false,
    },
    site_name: {
      title: 'Site Name',
      filter: false,
    },
    property: {
      title: 'Property',
      filter: false,
    },
    value: {
      title: 'Value',
      filter: false,
    },
    is_active: {
      title: 'Is Active',
      filter: false,
    },
  }
  editedConfigs: any;
  upsertedConfigs: Set<any> = new Set();

  filteredPropertiesMap: Map<string, string[]> = new Map<string, string[]>();

  constructor(public autoAssetService: AutoAssetService,
    private toastingService: ToastingService,
    public autoAssetConstantService: AutoAssetConstantsService,
    public utilService: UtilService,
    public dropDownService: AutoAssetEntityDropdownService,
  ) {
    const entityNameOptions = Object.keys(this.entityNameMap);

    this.dimensionMap.set('EntityName', {
      options: entityNameOptions,
      selectedValue: entityNameOptions[5],
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

    this.settings = {
      actions: false,
      noDataMessage: ''
    }
    this.initializeUpsertedPropertyDetails()
  }

  ngOnInit(): void {
    this.dropDownService.setEntityNameMap(this.entityNameMap);
  }

  toggleCollapse(config) {
    config.isCollapsed = !config.isCollapsed;
  }

  toggleNotesCollapse() {
    this.isNotesCollapsed = !this.isNotesCollapsed;
  }

  initializeUpsertedPropertyDetails() {
    this.newConfigDetails = {
      property: null,
      entity_name: null,
      entity_name_value: null,
      entity_value: null,
      domain: null,
      entity_name_options: this.filterEntityNameForSelectedEntityName(this.entityName.options),
      selected_entity_name: null,
      selected_entity_value: null,
      selected_domain: null,
      final_entity_value: null,
      valueOptions: [],
      value: null,
      is_active: true
    }
  }

  isEntityNameIsMulti(entityName) {
    if(this.utilService.isSet(entityName)) {
      return this.entityNameMap[entityName].isMulti;
    }
    return false;
  }

  filterEntityNameForSelectedEntityName(entityNameOptions) {
    const allDemandIndex = entityNameOptions.indexOf('Domain + All Demand');
    const selectedIndex = entityNameOptions.indexOf(this.entityName.selectedValue);

    if (selectedIndex === -1) {
      throw new Error('Selected entity name not found in the list.');
    }

    const domainPlusIds = entityNameOptions.slice(0, allDemandIndex + 1);
    const onlyIds = entityNameOptions.slice(allDemandIndex + 1);
    const filteredDomainPlusIds = domainPlusIds.slice(selectedIndex);
    const filteredOnlyIds = onlyIds.slice(selectedIndex - domainPlusIds.length);
    const filteredEntities = [...filteredDomainPlusIds, ...filteredOnlyIds];
    return filteredEntities.filter((item) => item != 'Global');
  }

  showPreviewInfoMessage() {
    if (this.showFirstChangeMsg) {
      this.toastingService.info("Don't Forget to Save", "Click on 'Save' to save changes.");
    }
    this.showFirstChangeMsg = false;
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

  async fetchConfig() {
    if (this.isFetchConfigEnabled()) {
      this.resetData();
      this.resetUpsertedConfigs();
      this.isNotesCollapsed = true;
      this.waitingMessage = "Fetching..."
      this.showLoader = true;
      this.showAddConfig = false;
      const entityName = this.dropDownService.getSelectedEntityNameValue(this.entityName);
      const entityValue = this.getEntityValue();
      this.newConfigDetails.selected_entity_name = entityName;
      this.newConfigDetails.selected_entity_value = entityValue;
      this.newConfigDetails.selected_domain = this.domain.selectedValue;
      await this.autoAssetService.getAutoAssetConfig(entityName, entityValue)
        .then((response) => {
          const configData = response['config'];
          this.parentsIds = response['entity_ids'];
          this.originalConfigDataJson = this.formatConfigDataJson(configData);
          this.configDataJson = JSON.parse(JSON.stringify(this.originalConfigDataJson));
          this.configData = this.objectToList(this.configDataJson);
          this.setNewConfigDetails()
        })
        .catch((error) => {
          if (error?.error?.response.reason == 'Entity not found') {
            this.toastingService.warning('No Config Found', 'Please enter valid entity details');
          }
          else {
            this.toastingService.error("Fetching Failed", "Unable to fetch Config")
          }
        })
        .finally(() => {
          this.showAddConfig = true;
          this.showLoader = false;
          this.waitingMessage = ""
        });
    }
  }

  getEntityValue() {
    const entityNameValue = this.dropDownService.getSelectedEntityNameValue(this.entityName);
    const entityValue = this.dropDownService.getEntityIdsFromSelectedEntityValues(this.entityValue);
    if (entityNameValue == "DOMAIN$$ALL_DEMAND") {
      return this.entityValue.selectedValue + this.autoAssetConstantService.DOUBLE_DOLLAR + this.autoAssetConstantService.ALL_DEMAND;
    }
    else if (this.isEntityNameIsMulti(this.entityName.selectedValue)) {
      return this.domain.selectedValue + this.autoAssetConstantService.DOUBLE_DOLLAR + entityValue;
    }
    return entityValue;
  }

  formatConfigDataJson(data) {
    const groupedData = data.reduce((acc, item) => {
      const property = `${item.property}`;

      if (!acc[property]) {
        acc[property] = {
          properties: [],
          property: item.property,
          isCollapsed: true,
        };
      }

      acc[property].properties.push({
        entity_name: item.entity_name,
        entity_value: item.entity_value,
        property: property,
        site_name: item.site_name,
        value: item.value,
        is_active: item.is_active,
      });
      return acc;
    }, {});

    return groupedData;
  }


  objectToList(data) {
    const configList = Object.values(data);
    return configList;
  }

  setPropertyValueOptions() {
    if (this.utilService.isSet(this.newConfigDetails.property)) {
      if (this.autoAssetService.configOnlyAssetProperties.includes(this.newConfigDetails.property)) {
        this.newConfigDetails.valueOptions = [true, false];
      }
      else if (this.autoAssetService.configAutoAssetTestProperties.includes(this.newConfigDetails.property)) {
        this.newConfigDetails.valueOptions = this.autoAssetService.autoAssetConfigPercentages;
      }
    }
  }

  setNewConfigDetails(): void {
    if (this.isAllDemand()) {
      this.newConfigDetails.entity_name = "Domain + All Demand";
      this.newConfigDetails.entity_name_value = "DOMAIN$$ALL_DEMAND";
      this.newConfigDetails.entity_value = "ALL_DEMAND";
      this.newConfigDetails.final_entity_value = this.entityValue.selectedValue + "$$" + "ALL_DEMAND";
      this.newConfigDetails.domain = this.entityValue.selectedValue;
    } else {
      this.newConfigDetails.domain = this.domain.selectedValue;
    }
  }

  setNewConfigEntityDetails(entityName: string): void {
    if (!entityName) return;

    const entityNameValue = this.dropDownService.getSelectedEntityNameValueFromPlaceholder(entityName);
    let entityValue = '';
    let finalEntityValue = '';
    let domain = this.newConfigDetails.selected_domain || this.newConfigDetails.domain;

    if (this.isEntityNameIsMulti(entityName)) {
      const entityNameSplit = entityNameValue.split('$$');
      const id = this.parentsIds[entityNameSplit[1]];
      entityValue = id;
      finalEntityValue = domain + "$$" + entityValue;
    } else {
      if (entityNameValue == "DOMAIN$$ALL_DEMAND") {
        entityValue = 'ALL_DEMAND';
        finalEntityValue = domain + "$$" + entityValue;
      }
      else {
        entityValue = this.parentsIds[entityNameValue];
        finalEntityValue = entityValue;
      }
    }
    
    this.newConfigDetails.entity_name_value = entityNameValue;
    this.newConfigDetails.entity_value = entityValue;
    this.newConfigDetails.final_entity_value = finalEntityValue;
  }
  
  addToUpsertedConfigs(item): void {
    const addedConfig = {
      entity_name: item.entity_name_value,
      entity_value: item.final_entity_value,
      property: item.property,
      site_name: `AUTO_ASSET_CONFIG$${item.final_entity_value}`,
      value: item.value,
      is_active: item.is_active ? 1 : 0,
    };
    this.upsertData(addedConfig);
  }

  upsertData(addedConfig) {
    this.showPreviewInfoMessage()
    let propertyLevel = this.configData.find(item => item.property === addedConfig.property);

    if (!propertyLevel) {
      propertyLevel = {
        property: addedConfig.property,
        isCollapsed: true,
        properties: []
      };
      this.configData.push(propertyLevel);
    }

    const existingConfig = propertyLevel.properties.find(item => item.entity_name === addedConfig.entity_name);
    const existsInUpsertedConfigs = Array.from(this.upsertedConfigs).some(config =>
      config.property === addedConfig.property && config.entity_name === addedConfig.entity_name && config.entity_value === addedConfig.entity_value
    );

    if (existingConfig) {
      if (this.isDataValueChanged(addedConfig)) {
        existingConfig.value = addedConfig.value;
        existingConfig.is_active = addedConfig.is_active;
        existingConfig.type = existingConfig.type == "ADDED" ? "ADDED" : "UPDATED";
        this.upsertedConfigs.add(existingConfig);
      }
      else if (!this.isDataValueChanged(addedConfig)) {
        if (existsInUpsertedConfigs) {
          this.removeItemFromUpsertedConfigs(addedConfig);
        }
        else {
          this.toastingService.warning("Configuration Exists", "Config is already present");
        }
      }
    } else if (!this.utilService.isSet(existingConfig)) {
      addedConfig['type'] = "ADDED";
      propertyLevel.properties.push(addedConfig);
      this.upsertedConfigs.add(addedConfig);
    }
  }

  isDataValueChanged(config) {
    const originalConfig = this.getOriginalConfig(config);
    if (originalConfig) {
      config.is_active = config.is_active ? 1 : 0;
      if (originalConfig.value != config.value || originalConfig.is_active != config.is_active) {
        return true;
      }
    }
    else {
      return true;
    }
    return false;
  }

  getOriginalConfig(config) {
    const properties = this.originalConfigDataJson?.[config['property']]?.properties;

    if (!Array.isArray(properties)) {
        return undefined;
    }

    return properties.find(item => item.entity_name === config.entity_name && item.entity_value === config.entity_value);
  }

  removeItemFromUpsertedConfigs(item) {
    const toRemoveItem = Array.from(this.upsertedConfigs).find(config =>
      config.entity_name == item.entity_name && config.entity_value == item.entity_value
    );

    if (toRemoveItem) {
      this.upsertedConfigs.delete(toRemoveItem);
      if (item.type == "ADDED") {
        this.removeConfig(item);
      }
      else {
        this.resetConfig(item);
      }
    }
  }
  
  removeConfig(config) {
    let propertyLevel = this.configData.find(item => item.property === config.property);
    propertyLevel.properties.splice(propertyLevel.properties.indexOf(item => item.entity_name === config.entity_name && item.entity_value === config.entity_value), 1);
  }

  resetConfig(config) {
    const originalConfig = this.getOriginalConfig(config);
    const toResetConfig = this.configData.find(item => item.property === config.property).properties.find(item => item.entity_name === config.entity_name && item.entity_value === config.entity_value);
    toResetConfig.value = originalConfig.value;
    toResetConfig.is_active = originalConfig.is_active;
    toResetConfig.type = null;
  }

  updateConfig() {
    this.resetData();
    this.waitingMessage = "Updating";
    this.showLoader = true;
    this.autoAssetService.updateAutoAssetConfig(Array.from(this.upsertedConfigs))
      .then(() => {
        this.toastingService.success("Config Updated", "Configs are successfully updated")
      })
      .catch((error) => {
        this.toastingService.error("Updating Failed", "Configs update failed")
      })
      .finally(() => {
        this.waitingMessage = '';
        this.showLoader = false;
        this.fetchConfig();
      })
  }

  isEntityValueDisabled() {
    return this.dropDownService.getSelectedEntityNameValue(this.entityName) == 'GLOBAL';
  }

  isFetchConfigEnabled(): boolean {
    const entityNameNotEmpty = this.utilService.isSet(this.dropDownService.getSelectedEntityName(this.entityName));
    const entityValueNotEmpty = this.utilService.isSet(this.dropDownService.getEntityIdsFromSelectedEntityValues(this.entityValue));
    const entityValueDisabled = this.isEntityValueDisabled();
    return entityNameNotEmpty && (entityValueDisabled || entityValueNotEmpty);
  }

  showDomainInput() {
    return this.isEntityNameIsMulti(this.newConfigDetails.entity_name);
  }

  isGlobal() {
    return this.newConfigDetails.selected_entity_name == "GLOBAL";
  }

  isAllDemand() {
    return this.newConfigDetails.selected_entity_name == "DOMAIN$$ALL_DEMAND";
  }

  isDomain() {
    return this.newConfigDetails.selected_entity_name.includes("DOMAIN");
  }

  isGlobalOrAllDemand() {
    return this.isGlobal() || this.isAllDemand();
  }

  isNewConfigsPropertyNameDisabled(): boolean {
    return this.isGlobal();
  }

  isNewConfigsEntityNameDisabled(): boolean {
    return this.isGlobalOrAllDemand();
  }

  isNewConfigsDomainDisabled(): boolean {
    return this.isGlobalOrAllDemand() || this.isDomain();
  }

  isNewConfigsEntityValueDisabled(): boolean {
    return true;
  }

  isNewConfigsPropertyValueDisabled(): boolean {
    return this.isGlobal();
  }

  configRowIsActiveDisabled(row) {
    return row.entity_name == "GLOBAL"
  }


  isUpsertConfigButtonEnabled(): boolean {
    const entityNameNotEmpty = this.utilService.isSet(this.newConfigDetails.entity_name);
    const domainIsRequiredAndNotEmpty = !this.isEntityNameIsMulti(this.newConfigDetails.entity_name) || this.utilService.isSet(this.newConfigDetails.domain);
    const propertyNotEmpty = this.utilService.isSet(this.newConfigDetails.property);
    const valueNotEmpty = this.utilService.isSet(this.newConfigDetails.value);
    return entityNameNotEmpty && domainIsRequiredAndNotEmpty && propertyNotEmpty && valueNotEmpty;
  }

  resetData() {
    this.configData = []
    this.initializeUpsertedPropertyDetails();
  }

  resetUpsertedConfigs() {
    this.upsertedConfigs = new Set();
  }

  resetDomainData() {
    this.domain.options = [];
    this.domain.selectedValue = null;
  }

  resetDimensionData() {
    this.entityValue.options = [];
    this.entityValue.selectedValue = null;
    this.resetDomainData();
  }
}
