import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MappingEntriesViewerComponent } from './mapping-entries-viewer.component';

describe('MappingEntriesViewerComponent', () => {
  let component: MappingEntriesViewerComponent;
  let fixture: ComponentFixture<MappingEntriesViewerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MappingEntriesViewerComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MappingEntriesViewerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
