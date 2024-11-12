import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BlockingtableComponent } from './blockingtable.component';

describe('BlockingtableComponent', () => {
  let component: BlockingtableComponent;
  let fixture: ComponentFixture<BlockingtableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BlockingtableComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BlockingtableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
