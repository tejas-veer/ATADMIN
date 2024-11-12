import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BuSelectionComponent } from './bu-selection.component';

describe('BuSelectionComponent', () => {
  let component: BuSelectionComponent;
  let fixture: ComponentFixture<BuSelectionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BuSelectionComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BuSelectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
