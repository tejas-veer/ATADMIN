import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FormatMetricsComponent } from './format-metrics.component';

describe('FormatMetricsComponent', () => {
  let component: FormatMetricsComponent;
  let fixture: ComponentFixture<FormatMetricsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ FormatMetricsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FormatMetricsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
