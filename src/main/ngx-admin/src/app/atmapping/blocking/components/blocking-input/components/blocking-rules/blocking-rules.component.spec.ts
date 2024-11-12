import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BlockingRulesComponent } from './blocking-rules.component';

describe('BlockingRulesComponent', () => {
  let component: BlockingRulesComponent;
  let fixture: ComponentFixture<BlockingRulesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BlockingRulesComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BlockingRulesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
