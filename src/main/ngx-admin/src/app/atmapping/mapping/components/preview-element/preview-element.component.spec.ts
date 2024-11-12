import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PreviewElementComponent } from './preview-element.component';

describe('PreviewElementComponent', () => {
  let component: PreviewElementComponent;
  let fixture: ComponentFixture<PreviewElementComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PreviewElementComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PreviewElementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
