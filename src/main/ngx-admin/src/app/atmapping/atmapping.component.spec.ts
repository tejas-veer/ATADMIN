import { ReactiveFormsModule } from '@angular/forms';
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AtmappingComponent } from './atmapping.component';

describe('AtmappingComponent', () => {
  let component: AtmappingComponent;
  let fixture: ComponentFixture<AtmappingComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AtmappingComponent ],
      imports: [ReactiveFormsModule]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AtmappingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
