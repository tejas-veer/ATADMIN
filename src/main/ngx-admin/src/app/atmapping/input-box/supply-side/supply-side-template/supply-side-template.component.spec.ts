import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SupplySideTemplateComponent } from './supply-side-template.component';

describe('SupplySideTemplateComponent', () => {
  let component: SupplySideTemplateComponent;
  let fixture: ComponentFixture<SupplySideTemplateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SupplySideTemplateComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SupplySideTemplateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
