import { TestBed } from '@angular/core/testing';

import { EtatMedicalService } from './etat-medical.service';

describe('EtatMedicalService', () => {
  let service: EtatMedicalService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(EtatMedicalService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
