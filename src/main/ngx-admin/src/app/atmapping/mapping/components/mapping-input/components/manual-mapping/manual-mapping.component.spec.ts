import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ManualMappingComponent } from './manual-mapping.component';

describe('ManualMappingComponent', () => {
  let component: ManualMappingComponent;
  let fixture: ComponentFixture<ManualMappingComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ManualMappingComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ManualMappingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
