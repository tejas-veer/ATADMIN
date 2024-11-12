import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WhitelistingModalComponent } from './whitelisting-modal.component';

describe('WhitelistingModalComponent', () => {
  let component: WhitelistingModalComponent;
  let fixture: ComponentFixture<WhitelistingModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ WhitelistingModalComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(WhitelistingModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
