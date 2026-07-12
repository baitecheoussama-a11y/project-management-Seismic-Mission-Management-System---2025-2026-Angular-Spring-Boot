import { TestBed } from '@angular/core/testing';

import { AntecedentsMedicalService } from './antecedents-medical.service';

describe('AntecedentsMedicalService', () => {
  let service: AntecedentsMedicalService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AntecedentsMedicalService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
