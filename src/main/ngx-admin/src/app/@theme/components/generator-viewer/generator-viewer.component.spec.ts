import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GeneratorViewerComponent } from './generator-viewer.component';

describe('GeneratorViewerComponent', () => {
  let component: GeneratorViewerComponent;
  let fixture: ComponentFixture<GeneratorViewerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GeneratorViewerComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(GeneratorViewerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
