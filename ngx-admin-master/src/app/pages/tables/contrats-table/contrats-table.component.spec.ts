import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ContratsTableComponent } from './contrats-table.component';

describe('ContratsTableComponent', () => {
  let component: ContratsTableComponent;
  let fixture: ComponentFixture<ContratsTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ContratsTableComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ContratsTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
