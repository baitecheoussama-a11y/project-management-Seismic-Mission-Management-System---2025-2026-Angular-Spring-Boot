import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MissionOverviewComponent } from './mission-overview.component';

describe('MissionOverviewComponent', () => {
  let component: MissionOverviewComponent;
  let fixture: ComponentFixture<MissionOverviewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MissionOverviewComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MissionOverviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
