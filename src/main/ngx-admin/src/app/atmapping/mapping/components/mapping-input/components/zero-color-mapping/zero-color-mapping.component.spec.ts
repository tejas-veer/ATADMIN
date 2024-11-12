import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ZeroColorMappingComponent } from './zero-color-mapping.component';

describe('ZeroColorMappingComponent', () => {
  let component: ZeroColorMappingComponent;
  let fixture: ComponentFixture<ZeroColorMappingComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ZeroColorMappingComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ZeroColorMappingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
