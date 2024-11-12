import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GeneratorBetaViewerComponent } from './generator-beta-viewer.component';

describe('GeneratorBetaViewerComponent', () => {
  let component: GeneratorBetaViewerComponent;
  let fixture: ComponentFixture<GeneratorBetaViewerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GeneratorBetaViewerComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(GeneratorBetaViewerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
