import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MissionMapFilterComponent } from './mission-map-filter.component';

describe('MissionMapFilterComponent', () => {
  let component: MissionMapFilterComponent;
  let fixture: ComponentFixture<MissionMapFilterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MissionMapFilterComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MissionMapFilterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
