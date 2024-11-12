import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DemandSideComponent } from './demand-side.component';

describe('DemandSideComponent', () => {
  let component: DemandSideComponent;
  let fixture: ComponentFixture<DemandSideComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DemandSideComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DemandSideComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
