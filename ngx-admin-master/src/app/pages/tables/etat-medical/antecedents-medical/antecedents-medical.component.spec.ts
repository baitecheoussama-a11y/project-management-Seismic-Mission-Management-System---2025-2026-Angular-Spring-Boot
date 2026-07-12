import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AntecedentsMedicalComponent } from './antecedents-medical.component';

describe('AntecedentsMedicalComponent', () => {
  let component: AntecedentsMedicalComponent;
  let fixture: ComponentFixture<AntecedentsMedicalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AntecedentsMedicalComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AntecedentsMedicalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
