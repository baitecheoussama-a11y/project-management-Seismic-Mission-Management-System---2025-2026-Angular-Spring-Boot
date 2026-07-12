import { TestBed } from '@angular/core/testing';

import { EtatAvancementService } from './etat-avancement.service';

describe('EtatAvancementService', () => {
  let service: EtatAvancementService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(EtatAvancementService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
