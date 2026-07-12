import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AssignActivityModalComponent } from './assign-activity-modal.component';

describe('AssignActivityModalComponent', () => {
  let component: AssignActivityModalComponent;
  let fixture: ComponentFixture<AssignActivityModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AssignActivityModalComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AssignActivityModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
