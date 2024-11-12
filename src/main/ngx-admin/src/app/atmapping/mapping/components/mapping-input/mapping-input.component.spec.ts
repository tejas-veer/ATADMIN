import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MappingInputComponent } from './mapping-input.component';

describe('MappingInputComponent', () => {
  let component: MappingInputComponent;
  let fixture: ComponentFixture<MappingInputComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MappingInputComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MappingInputComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
