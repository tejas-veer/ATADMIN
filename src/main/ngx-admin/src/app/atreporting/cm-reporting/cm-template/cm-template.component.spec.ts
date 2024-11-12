import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CmTemplateComponent } from './cm-template.component';

describe('CmTemplateComponent', () => {
  let component: CmTemplateComponent;
  let fixture: ComponentFixture<CmTemplateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CmTemplateComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CmTemplateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
