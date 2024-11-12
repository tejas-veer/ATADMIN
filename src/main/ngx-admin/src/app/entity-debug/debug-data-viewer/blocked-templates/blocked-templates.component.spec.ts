import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BlockedTemplatesComponent } from './blocked-templates.component';

describe('BlockedTemplatesComponent', () => {
  let component: BlockedTemplatesComponent;
  let fixture: ComponentFixture<BlockedTemplatesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BlockedTemplatesComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BlockedTemplatesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
