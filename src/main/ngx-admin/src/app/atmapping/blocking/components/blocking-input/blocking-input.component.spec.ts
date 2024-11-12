import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BlockingInputComponent } from './blocking-input.component';

describe('BlockingInputComponent', () => {
  let component: BlockingInputComponent;
  let fixture: ComponentFixture<BlockingInputComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BlockingInputComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BlockingInputComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
