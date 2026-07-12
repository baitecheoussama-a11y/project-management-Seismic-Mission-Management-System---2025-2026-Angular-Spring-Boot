import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AffectationRolesTableComponent } from './affectation-roles-table.component';

describe('AffectationRolesTableComponent', () => {
  let component: AffectationRolesTableComponent;
  let fixture: ComponentFixture<AffectationRolesTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AffectationRolesTableComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AffectationRolesTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
