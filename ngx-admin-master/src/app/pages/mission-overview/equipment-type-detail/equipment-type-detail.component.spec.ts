import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EquipmentTypeDetailComponent } from './equipment-type-detail.component';

describe('EquipmentTypeDetailComponent', () => {
  let component: EquipmentTypeDetailComponent;
  let fixture: ComponentFixture<EquipmentTypeDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EquipmentTypeDetailComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EquipmentTypeDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
