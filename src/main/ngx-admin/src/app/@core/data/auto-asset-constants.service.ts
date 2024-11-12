import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class AutoAssetConstantsService {
  public readonly DOUBLE_DOLLAR = '$$';

  public readonly KBB = 'KBB';
  public readonly STABLE_DIFFUSION = 'Stable Diffusion';
  public readonly TITLE_GENERATION = 'Title Generation';

  public readonly KEYWORD_ASSET_SIZE = [
    '1200x800', '800x445', '600x500', '600x314', '525x487', '382x200', '312x103',
    '310x95', '301x216', '300x194', '300x104', '300x95', '286x175', '275x205',
    '250x150', '245x90', '233x175', '230x191', '207x100', '195x150', '195x150',
    '195x91', '175x95', '173x137', '172x86', '170x185', '170x120', '150x100',
    '150x85', '140x140', '140x110', '137x127', '100x75'];

  public readonly REVIEW_IMAGE_ASSET_PREVIEW_MODAL = "Review Asset Modal";
  public readonly VIEW_ASSET_PREVIEW_MODAL = "View Asset Modal";
  public readonly KBB_MAP_IMAGE_ASSET_PREVIEW_MODAL = "KBB Map Asset Modal";
  public readonly KBB_MAP_TITLE_ASSET_PREVIEW_MODAL = "KBB Map Title Asset Modal";
  public readonly REVIEW_TITLE_ASSET_PREVIEW_MODAL = "KBB Review Title Asset Modal";

  public readonly SD_MAP_ASSET_PREVIEW_MODAL = 'SD Map Asset Modal';
  public readonly SD_VERSION_LIST = ['GPT_GENERATED_PROMPT', 'USER_PROMPT'];
  public readonly SD_DEFAULT_VERSION = 'GPT_GENERATED_PROMPT';
  public readonly SD_NEGATIVE_PROMPT = 'text, brand logos, distorted faces, ugly, tiling, poorly drawn hands, poorly drawn feet, poorly drawn face, out of frame, extra limbs, disfigured, deformed, body out of frame, bad anatomy, watermark, signature, cut off, low contrast, underexposed, overexposed, bad art, beginner, amateur, distorted, blurry, draft, grainy';
  public readonly SD_DEFAULT_PROMPT = 'Generate an image that creatively represents the essence of the following text: {{kwd}}. Ensure the image is visually appealing and conceptually aligned with the content. realistic, highly detailed, cinematic lighting, intricate, sharp focus, f/1.8, 85mm, (centered image composition), (professionally color graded), ((bright soft diffused light)), HDR 4K, 8K';
  public readonly SD_RESPONSE_STATUS_SUCCESS = 'success';
  public readonly SD_RESPONSE_STATUS_PROCESSING = 'processing';
  public readonly SD_RESPONSE_STATUS_ERROR = 'error';
  public readonly HOSTED_IMAGE_UPLOAD_MODAL = 'hostedUrlUploadModal';
  public readonly LOCAL_IMAGE_UPLOAD_MODAL = 'localImageUploadModal';
  public readonly KEY_VALUE_SEPARATOR = '@@';
  public readonly ASSET_TYPE_IMAGE = 'IMAGE';
  public readonly ASSET_TYPE_TITLE = 'TITLE';
  public readonly ASSET_TYPE_C2A = 'C2A';
  public readonly OZIL_GENERATED_TITLE_DEFAULT_PROMPT = 'Generate a list of 5 compelling ad titles for a digital ' +
      'advertisement with the following information: (Keep in mind that the user has shown intent of reading the ' +
      'publisher\'s page, so the ad titles should appeal to that intent) Advertiser Keyword: {{DEMAND_KEYWORD}}, Titles ' +
      'should be simple and effective. Limit ad title length to 60 characters.';

  public readonly MANUAL_TITLE_SET_ID = 4;
  public readonly AUTO_GENERATED_TITLE_SET_ID = 5;
  public readonly AA_GET_REQUEST_DETAILS_INTERVAL = 10000;

  public readonly ALL_DEMAND = 'ALL_DEMAND';

  public readonly AUTO_ASSET_TEST_PERCENTAGE = 'AUTO_ASSET_TEST_PERCENTAGE';
  public readonly IMAGE_TEST_PERCENTAGE = 'IMAGE_TEST_PERCENTAGE';
  public readonly TITLE_TEST_PERCENTAGE = 'TITLE_TEST_PERCENTAGE';
  public readonly C2A_TEST_PERCENTAGE = 'C2A_TEST_PERCENTAGE';
  public readonly ONLY_AA_C2A = 'ONLY_AA_C2A';
  public readonly ONLY_AA_IMAGE = 'ONLY_AA_IMAGE';
  public readonly ONLY_AA_TITLE = 'ONLY_AA_TITLE';

  public readonly IMAGE_CONFIGS = [this.AUTO_ASSET_TEST_PERCENTAGE, this.IMAGE_TEST_PERCENTAGE, this.ONLY_AA_IMAGE];
  public readonly C2A_CONFIGS = [this.AUTO_ASSET_TEST_PERCENTAGE, this.C2A_TEST_PERCENTAGE, this.ONLY_AA_C2A];
  public readonly TITLE_CONFIGS = [this.AUTO_ASSET_TEST_PERCENTAGE, this.TITLE_TEST_PERCENTAGE, this.ONLY_AA_TITLE];


  constructor() { }
}
