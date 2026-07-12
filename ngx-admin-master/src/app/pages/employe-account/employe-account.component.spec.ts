import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EmployeAccountComponent } from './employe-account.component';

describe('EmployeAccountComponent', () => {
  let component: EmployeAccountComponent;
  let fixture: ComponentFixture<EmployeAccountComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EmployeAccountComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EmployeAccountComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
