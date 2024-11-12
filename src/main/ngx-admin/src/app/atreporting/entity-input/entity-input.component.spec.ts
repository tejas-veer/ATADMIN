import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EntityInputComponent } from './entity-input.component';

describe('EntityInputComponent', () => {
  let component: EntityInputComponent;
  let fixture: ComponentFixture<EntityInputComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EntityInputComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EntityInputComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
