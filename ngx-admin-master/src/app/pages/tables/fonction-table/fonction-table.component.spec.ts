import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FonctionTableComponent } from './fonction-table.component';

describe('FonctionTableComponent', () => {
  let component: FonctionTableComponent;
  let fixture: ComponentFixture<FonctionTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ FonctionTableComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FonctionTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
