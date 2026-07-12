import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DashboardyComponent } from './dashboardy.component';

describe('DashboardyComponent', () => {
  let component: DashboardyComponent;
  let fixture: ComponentFixture<DashboardyComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DashboardyComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DashboardyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
