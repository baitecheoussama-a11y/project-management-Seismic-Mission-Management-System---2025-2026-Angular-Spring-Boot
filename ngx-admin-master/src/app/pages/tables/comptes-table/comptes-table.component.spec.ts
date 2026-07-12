import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ComptesTableComponent } from './comptes-table.component';

describe('ComptesTableComponent', () => {
  let component: ComptesTableComponent;
  let fixture: ComponentFixture<ComptesTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ComptesTableComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ComptesTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
