import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class BulkUploadService {


  hostedImageSampleCsvColumns = ["entity_name", "entity_value", "asset_value", "set_id"]
  hostedImageSampleCsvData = [
    {
      "entity_name": "DEMAND_BASIS",
      "entity_value": "demand_basis_test",
      "asset_value": "http://cdn.taboola.com/libtrc/static/thumbnails/STABLE_DIFFUSION/ESD/b6e7ccc5-e8c0-4df7-80fe-ff2e0dd37fbf__W9quRXkf.jpg",
      "set_id": "1"
    },
     {
      "entity_name": "AD_ID",
      "entity_value": "9876543210",
      "asset_value": "http://cdn.taboola.com/libtrc/static/thumbnails/STABLE_DIFFUSION/ESD/b6e7ccc5-e8c0-4df7-80fe-ff2e0dd37fbf__W9quRXkf.jpg",
      "set_id": "1"
    },
     {
      "entity_name": "ADGROUP_ID",
      "entity_value": "9876543210",
      "asset_value": "http://cdn.taboola.com/libtrc/static/thumbnails/STABLE_DIFFUSION/ESD/b6e7ccc5-e8c0-4df7-80fe-ff2e0dd37fbf__W9quRXkf.jpg",
      "set_id": "1"
    },
     {
      "entity_name": "CAMPAIGN_ID",
      "entity_value": "9876543210",
      "asset_value": "http://cdn.taboola.com/libtrc/static/thumbnails/STABLE_DIFFUSION/ESD/b6e7ccc5-e8c0-4df7-80fe-ff2e0dd37fbf__W9quRXkf.jpg",
      "set_id": "1"
    },
    {
      "entity_name": "ADVERTISER_ID",
      "entity_value": "9876543210",
      "asset_value": "http://cdn.taboola.com/libtrc/static/thumbnails/STABLE_DIFFUSION/ESD/b6e7ccc5-e8c0-4df7-80fe-ff2e0dd37fbf__W9quRXkf.jpg",
      "set_id": "1"
    },
    {
      "entity_name": "DEMAND_BASIS_IAD_SIZE",
      "entity_value": "demand_basis_test_iad$$300x250",
      "asset_value": "http://cdn.taboola.com/libtrc/static/thumbnails/STABLE_DIFFUSION/ESD/b6e7ccc5-e8c0-4df7-80fe-ff2e0dd37fbf__W9quRXkf.jpg",
      "set_id": "1"
    },
    {
      "entity_name": "AD_GROUP_ID_IAD_SIZE",
      "entity_value": "9876543210$$300x250",
      "asset_value": "http://cdn.taboola.com/libtrc/static/thumbnails/STABLE_DIFFUSION/ESD/b6e7ccc5-e8c0-4df7-80fe-ff2e0dd37fbf__W9quRXkf.jpg",
      "set_id": "1"
    },
    {
      "entity_name": "CAMPAIGN_ID_IAD_SIZE",
      "entity_value": "9876543210$$300x250",
      "asset_value": "http://cdn.taboola.com/libtrc/static/thumbnails/STABLE_DIFFUSION/ESD/b6e7ccc5-e8c0-4df7-80fe-ff2e0dd37fbf__W9quRXkf.jpg",
      "set_id": "1"
    },
    {
      "entity_name": "ADVERTISER_ID_IAD_SIZE",
      "entity_value": "9876543210$$300x250",
      "asset_value": "http://cdn.taboola.com/libtrc/static/thumbnails/STABLE_DIFFUSION/ESD/b6e7ccc5-e8c0-4df7-80fe-ff2e0dd37fbf__W9quRXkf.jpg",
      "set_id": "1"
    }
  ]


  validStableDiffusionAssetHeadersForDownload = ["keyword", "version", "asset_value", "prompt", "negative_prompt", "set_id"];
  stableDiffusionBulkUploadMandatoryColumns = ['keyword', 'prompt', 'negative_prompt', 'version']

  sdHeaderMapping = {
    keyword: 'keyword',
    version: 'version',
    imageUrl: 'asset_value',
    prompt: 'prompt',
    negativePrompt: 'negative_prompt',
    set_id: 'set_id'
  };
  stableDiffusionSampleCsvColumns = ["version", "keyword", "prompt", "negative_prompt"]
  stableDiffusionSampleCsvData = [
    {
      "version": "USER_PROMPT",
      "keyword": "gifts",
      "prompt": "Generate realsitic image for {{kwd}} that creatively represents the essence",
      "negative_prompt": "person,blur"
    },
    {
      "version": "USER_PROMPT",
      "keyword": "Black Friday deals,smart home devices",
      "prompt": "Create a stunning visual for {{kwd}} showcasing exclusive Black Friday discounts!",
      "negative_prompt": "empty,plain"
    },
    {
      "version": "GPT_GENERATED_PROMPT",
      "keyword": "travel accessories",
      "prompt": "",
      "negative_prompt": ""
    },
    {
      "version": "GPT_GENERATED_PROMPT",
      "keyword": "Dog foods",
      "prompt": "",
      "negative_prompt": ""
    }
  ]

  validTitleGenerationAssetHeadersForDownload = ["asset_value", "demand_keyword", "publisher_page_url", "publisher_page_title", "prompt"];
  titleGenerationBulkUploadColumns = ['demand_keyword', 'publisher_page_url', 'publisher_page_title', 'prompt']
  titleGenerationSampleCsvData = [
    {
      "demand_keyword": "ladies shoes clearance",
      "publisher_page_url": "shoes.com",
      "publisher_page_title": "best shoes",
      "prompt": "Generate a list of 5 compelling ad titles for a digital advertisement with the following information: (Keep in mind that the user has shown intent of reading the publisher's page, so the ad titles should appeal to that intent) Advertiser Keyword: {{DEMAND_KEYWORD}}, Titles should be simple and effective. Limit ad title length to 60 characters."
    },
    {
      "demand_keyword": "amazons overstock clearance outlet",
      "publisher_page_url": "amazonoutletsale.com",
      "publisher_page_title": "Best Amazon Outlet sale 2024",
      "prompt": ""
    },
    {
      "demand_keyword": "women clothing sale",
      "publisher_page_url": "",
      "publisher_page_title": "",
      "prompt": ""
    },
    {
      "demand_keyword": "upcoming nvidia stock split",
      "publisher_page_url": "gamer.com",
      "publisher_page_title": "",
      "prompt": "generate title for {{DEMAND_KEYWORD}}"
    }
  ]

  titleMappingBulkUploadColumns = ['entity_name', 'entity_value', 'asset_value', 'set_id']
  titleMappingSampleCsvData = [
    {
      "entity_name": "DEMAND_BASIS",
      "entity_value": "demand_basis_test",
      "asset_value": "Walk in Style: Top Senior Skechers Shoes",
      "set_id": "4"
    },
    {
      "entity_name": "CAMPAIGN_ID",
      "entity_value": "9876543210",
      "asset_value": "Don't Miss Out: Clearance Sale on Clothes!",
      "set_id": "4"
    },
    {
      "entity_name": "AD_GROUP_ID",
      "entity_value": "9876543210",
      "asset_value": "Shop Now: Unbeatable Deals on Fashion",
      "set_id": "4"
    },
    {
      "entity_name": "AD_ID",
      "entity_value": "9876543210",
      "asset_value": "Save Big: Clearance Sale on Trendy Apparel",
      "set_id": "4"
    },
    {
      "entity_name": "CAMPAIGN_ID$$DEMAND",
      "entity_value": "9876543210$$demand_test",
      "asset_value": "Save Big: Clearance Sale on Trendy Apparel",
      "set_id": "4"
    },
    {
      "entity_name": "AD_GROUP_ID$$DEMAND",
      "entity_value": "9876543210$$demand_test",
      "asset_value": "Save Big: Clearance Sale on Trendy Apparel",
      "set_id": "4"
    },
    {
      "entity_name": "AD_ID$$DEMAND",
      "entity_value": "demand_basis_test",
      "asset_value": "Save Big: Clearance Sale on Trendy Apparel",
      "set_id": "4"
    }
  ]

  titleAssetKeySetForBulkUpload = {
    demand_keyword: 'dmd_kwd',
    publisher_page_url: 'purl',
    publisher_page_title: 'ptitle',
    prompt: 'pmt'
  }

  sdAssetKeySetForBulkUpload = {
    keyword: 'kwd',
    prompt: 'pmt',
    negative_prompt: 'npmt',
    version: 'ver'
  }


  c2aMappingBulkUploadColumns = this.titleMappingBulkUploadColumns;
  c2aMappingSampleCsvData = [
    {
      "entity_name": "DOMAIN",
      "entity_value": "domain.test",
      "asset_value": "Find Out",
      "set_id": "1"
    },
    {
      "entity_name": "DOMAIN$$CAMPAIGN_ID",
      "entity_value": "domain.test$$9876543210",
      "asset_value": "Get It",
      "set_id": "1"
    },
    {
      "entity_name": "DOMAIN$$ADVERTISER_ID",
      "entity_value": "domain.test$$9876543210",
      "asset_value": "Next >",
      "set_id": "1"
    },
    {
      "entity_name": "AD_GROUP_ID",
      "entity_value": "9876543210",
      "asset_value": "Let's Go",
      "set_id": "1"
    },
    {
      "entity_name": "CAMPAIGN_ID",
      "entity_value": "9876543210",
      "asset_value": "Let's Go",
      "set_id": "1"
    },
    {
      "entity_name": "ADVERTISER_ID",
      "entity_value": "9876543210",
      "asset_value": "Let's Go",
      "set_id": "1"
    }
  ]

  constructor() { }
}
