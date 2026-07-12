import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProductionStatsComponent } from './production-stats.component';

describe('ProductionStatsComponent', () => {
  let component: ProductionStatsComponent;
  let fixture: ComponentFixture<ProductionStatsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ProductionStatsComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProductionStatsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
