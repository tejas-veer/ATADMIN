import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConfigInputBoxComponent } from './config-input-box.component';

describe('ConfigInputBoxComponent', () => {
  let component: ConfigInputBoxComponent;
  let fixture: ComponentFixture<ConfigInputBoxComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ConfigInputBoxComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ConfigInputBoxComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
