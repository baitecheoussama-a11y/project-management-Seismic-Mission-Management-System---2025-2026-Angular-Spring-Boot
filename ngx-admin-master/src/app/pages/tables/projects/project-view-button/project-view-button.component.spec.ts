import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectViewButtonComponent } from './project-view-button.component';

describe('ProjectViewButtonComponent', () => {
  let component: ProjectViewButtonComponent;
  let fixture: ComponentFixture<ProjectViewButtonComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ProjectViewButtonComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProjectViewButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
