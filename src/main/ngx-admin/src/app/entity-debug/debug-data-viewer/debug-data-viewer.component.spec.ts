import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DebugDataViewerComponent } from './debug-data-viewer.component';

describe('DebugDataViewerComponent', () => {
  let component: DebugDataViewerComponent;
  let fixture: ComponentFixture<DebugDataViewerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DebugDataViewerComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DebugDataViewerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
