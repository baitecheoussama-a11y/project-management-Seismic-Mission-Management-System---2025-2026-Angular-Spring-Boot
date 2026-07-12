import { TestBed } from '@angular/core/testing';

import { AffectationRoleService } from './affectation-role.service';

describe('AffectationRoleService', () => {
  let service: AffectationRoleService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AffectationRoleService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
