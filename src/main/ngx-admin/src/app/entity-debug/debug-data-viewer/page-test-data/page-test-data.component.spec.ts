import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PageTestDataComponent } from './page-test-data.component';

describe('PageTestDataComponent', () => {
  let component: PageTestDataComponent;
  let fixture: ComponentFixture<PageTestDataComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PageTestDataComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PageTestDataComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
