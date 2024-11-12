import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CmReportingComponent } from './cm-reporting.component';

describe('CmReportingComponent', () => {
  let component: CmReportingComponent;
  let fixture: ComponentFixture<CmReportingComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CmReportingComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CmReportingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
