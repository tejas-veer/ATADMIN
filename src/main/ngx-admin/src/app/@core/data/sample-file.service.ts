import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class SampleFileService {

  mappingSample: any = [
    {
      supply_hierarchy_level: 'Domain',
      supply_entity_value: 'cnn.com',
      demand_hierarchy_level: '',
      demand_entity_value: '',
      template_id: '808067712',
      mapping_type: 'MANUAL',
      template_size: '300x250',
      system_page_type: 'cm-native',
    },
    {
      supply_hierarchy_level: '',
      supply_entity_value: '',
      demand_hierarchy_level: 'AdGroup',
      demand_entity_value: '602340',
      template_id: '808067803',
      mapping_type: 'ADWISER',
      template_size: '300x250',
      system_page_type: 'keywords-only',
    },
    {
      supply_hierarchy_level: 'Domain',
      supply_entity_value: 'cnn.com',
      demand_hierarchy_level: 'Adgroup',
      demand_entity_value: '602340',
      template_id: '808367902',
      mapping_type: 'ADWISER',
      template_size: 'ALL',
      system_page_type: 'cm-native',
    },
    {
      supply_hierarchy_level: 'Domain',
      supply_entity_value: 'cnn.com',
      demand_hierarchy_level: '',
      demand_entity_value: '',
      template_id: '808067712|808132516',
      mapping_type: 'MANUAL',
      template_size: '300x250|728x90',
      system_page_type: 'cm-native',
    },

    {
      supply_hierarchy_level: 'Entity',
      supply_entity_value: 'cnn.com|665473506',
      demand_hierarchy_level: '',
      demand_entity_value: '',
      template_id: '808067712',
      mapping_type: 'MANUAL',
      template_size: '728x90',
      system_page_type: 'cm-native',
    },
  ];

  blockingSample: any = [
    {
      supply_hierarchy_level: 'Domain',
      supply_entity_value: 'cnn.com',
      demand_hierarchy_level: '',
      demand_entity_value: '',
      id: '808067712',
      creative_type: 'TEMPLATE',
      size: '300x250',
      system_page_type: 'cm-native',
    },
    {
      supply_hierarchy_level: '',
      supply_entity_value: '',
      demand_hierarchy_level: 'AdGroup',
      demand_entity_value: '602340',
      id: '808067803',
      creative_type: 'FRAMEWORK',
      size: '300x250',
      system_page_type: 'keywords-only',
    },
    {
      supply_hierarchy_level: 'Domain',
      supply_entity_value: 'cnn.com',
      demand_hierarchy_level: 'AdGroup',
      demand_entity_value: '602340',
      id: '808367902',
      creative_type: 'FRAMEWORK',
      size: 'ALL',
      system_page_type: 'cm-native',
    },
    {
      supply_hierarchy_level: 'Domain',
      supply_entity_value: 'cnn.com',
      demand_hierarchy_level: '',
      demand_entity_value: '',
      id: '808067712|808132516',
      creative_type: 'TEMPLATE',
      size: '300x250|728x90',
      system_page_type: 'cm-native',
    },

    {
      supply_hierarchy_level: 'Entity',
      supply_entity_value: 'cnn.com|665473506',
      demand_hierarchy_level: '',
      demand_entity_value: '',
      id: '808067712',
      creative_type: 'TEMPLATE',
      size: '728x90',
      system_page_type: 'cm-native',
    },
  ];

  constructor() { }


  getSampleMappingCsv(): any {
    return this.mappingSample;
  }

  getSampleBlockingCsv(): any {
    return this.blockingSample;
  }
}
