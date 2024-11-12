import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ErrorStackViewerComponent } from './error-stack-viewer.component';

describe('ErrorStackViewerComponent', () => {
  let component: ErrorStackViewerComponent;
  let fixture: ComponentFixture<ErrorStackViewerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ErrorStackViewerComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ErrorStackViewerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
