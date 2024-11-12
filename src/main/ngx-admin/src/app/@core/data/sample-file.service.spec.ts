import { TestBed } from '@angular/core/testing';

import { SampleFileService } from './sample-file.service';

describe('SampleFileService', () => {
  let service: SampleFileService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SampleFileService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
