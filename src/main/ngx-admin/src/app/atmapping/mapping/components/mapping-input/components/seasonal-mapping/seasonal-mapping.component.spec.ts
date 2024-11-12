import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SeasonalMappingComponent } from './seasonal-mapping.component';

describe('SeasonalMappingComponent', () => {
  let component: SeasonalMappingComponent;
  let fixture: ComponentFixture<SeasonalMappingComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SeasonalMappingComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SeasonalMappingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
