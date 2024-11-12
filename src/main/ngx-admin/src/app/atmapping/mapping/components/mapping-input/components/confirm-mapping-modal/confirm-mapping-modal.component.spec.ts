import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConfirmMappingModalComponent } from './confirm-mapping-modal.component';

describe('ConfirmMappingModalComponent', () => {
  let component: ConfirmMappingModalComponent;
  let fixture: ComponentFixture<ConfirmMappingModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ConfirmMappingModalComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ConfirmMappingModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
