import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DashbrdComponent } from './dashbrd.component';

describe('DashbrdComponent', () => {
  let component: DashbrdComponent;
  let fixture: ComponentFixture<DashbrdComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DashbrdComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DashbrdComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
