import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MaterielDetailModalComponent } from './materiel-detail-modal.component';

describe('MaterielDetailModalComponent', () => {
  let component: MaterielDetailModalComponent;
  let fixture: ComponentFixture<MaterielDetailModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MaterielDetailModalComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MaterielDetailModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
