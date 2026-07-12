import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AssignMissionModalComponent } from './assign-mission-modal.component';

describe('AssignMissionModalComponent', () => {
  let component: AssignMissionModalComponent;
  let fixture: ComponentFixture<AssignMissionModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AssignMissionModalComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AssignMissionModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
