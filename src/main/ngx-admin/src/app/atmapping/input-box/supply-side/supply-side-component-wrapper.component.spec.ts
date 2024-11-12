import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SupplySideComponentWrapper } from './supply-side-component-wrapper.component';

describe('SupplySideComponentWrapper', () => {
  let component: SupplySideComponentWrapper;
  let fixture: ComponentFixture<SupplySideComponentWrapper>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SupplySideComponentWrapper ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SupplySideComponentWrapper);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
