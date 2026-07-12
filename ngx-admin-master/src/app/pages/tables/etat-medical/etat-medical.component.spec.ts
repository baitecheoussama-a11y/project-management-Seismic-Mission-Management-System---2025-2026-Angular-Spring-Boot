import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EtatMedicalComponent } from './etat-medical.component';

describe('EtatMedicalComponent', () => {
  let component: EtatMedicalComponent;
  let fixture: ComponentFixture<EtatMedicalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EtatMedicalComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EtatMedicalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
