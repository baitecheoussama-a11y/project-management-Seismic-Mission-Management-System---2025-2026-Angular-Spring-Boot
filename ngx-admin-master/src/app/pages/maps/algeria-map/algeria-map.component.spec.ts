import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AlgeriaMapComponent } from './algeria-map.component';

describe('AlgeriaMapComponent', () => {
  let component: AlgeriaMapComponent;
  let fixture: ComponentFixture<AlgeriaMapComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AlgeriaMapComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AlgeriaMapComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
