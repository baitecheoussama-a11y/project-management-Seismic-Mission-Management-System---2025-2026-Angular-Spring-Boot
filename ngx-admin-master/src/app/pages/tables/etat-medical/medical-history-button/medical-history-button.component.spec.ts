import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MedicalHistoryButtonComponent } from './medical-history-button.component';

describe('MedicalHistoryButtonComponent', () => {
  let component: MedicalHistoryButtonComponent;
  let fixture: ComponentFixture<MedicalHistoryButtonComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MedicalHistoryButtonComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MedicalHistoryButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
