import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';

import { PagesComponent } from './pages.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { ECommerceComponent } from './e-commerce/e-commerce.component';
import { NotFoundComponent } from './miscellaneous/not-found/not-found.component';

import { EmployeAccountComponent } from './employe-account/employe-account.component';
import { DashbrdComponent } from './dashbrd/dashbrd.component';
import { MaterielComponent } from './materiel/materiel.component';
import { RessourceComponent } from './ressource/ressource.component';

import { MissionOverviewComponent } from './mission-overview/mission-overview.component';
import { ProjectOverviewComponent } from './project-overview/project-overview.component';

import { EquipeDetailComponent } from './mission-overview/equipe-detail/equipe-detail.component';
import { MissionDashboardComponent } from './mission-dashboard/mission-dashboard.component';
import { ProjectDashboardComponent } from './project-dashboard/project-dashboard.component';
import { MembersComponent } from './my-team/members/members.component';
import { ActivitiesComponent } from './my-team/activities/activities.component';
import { RapportsComponent } from './my-team/rapports/rapports.component';
import { EventsComponent } from './safety/events/events.component';
import { TypesComponent } from './safety/events/types/types.component';
import { IncidentsComponent } from './safety/incidents/incidents.component';
import { ReportsViewerComponent } from './reports-viewer/reports-viewer.component';
import { PerformanceComponent } from './my-team/performance/performance.component';
import { ProjectsComponent } from './tables/projects/projects.component';
import { DdashboardComponent } from './ddashboard/ddashboard.component';
import { ProductionStatsComponent } from './production-stats/production-stats.component';
import { DashboardyComponent } from './analytics/dashboardy/dashboardy.component';
import { PivotTableComponent } from './pivot-table/pivot-table.component';

const routes: Routes = [{
  path: '',
  
  component: PagesComponent,
   
  children: [
    {
     
      path: 'dashboard',
      component: ECommerceComponent,
    },
 {
     
      path: 'analytics/dashboard',
      component: DashboardyComponent,
    },
     {
     
      path: 'analytics/PivotTable',
      component: PivotTableComponent,
    },
    
      {
    path: 'ddashboard',
    component: DdashboardComponent,
    data: { title: 'Dashboard' }
  },

  {
    path: 'stats/production',
    component: ProductionStatsComponent,
    data: { title: 'Production Statistics' }
  },
      {
     
      path: 'dashboard2',
      component:  DashbrdComponent ,
    },
    {
  path: 'employe-account',
  component: EmployeAccountComponent,
  data: { title: 'Employee Account Details' }
},
    {
      path: 'iot-dashboard',
      component: DashboardComponent,
    },
    {
      path: 'layout',
      loadChildren: () => import('./layout/layout.module')
        .then(m => m.LayoutModule),
    },
    {
      path: 'forms',
      loadChildren: () => import('./forms/forms.module')
        .then(m => m.FormsModule),
    },
    {
      path: 'ui-features',
      loadChildren: () => import('./ui-features/ui-features.module')
        .then(m => m.UiFeaturesModule),
    },
    {
      path: 'modal-overlays',
      loadChildren: () => import('./modal-overlays/modal-overlays.module')
        .then(m => m.ModalOverlaysModule),
    },
    {
      path: 'extra-components',
      loadChildren: () => import('./extra-components/extra-components.module')
        .then(m => m.ExtraComponentsModule),
    },
    {
      path: 'maps',
      loadChildren: () => import('./maps/maps.module')
        .then(m => m.MapsModule),
    },

    {
      path: 'charts',
      loadChildren: () => import('./charts/charts.module')
        .then(m => m.ChartsModule),
    },
    {
      path: 'editors',
      loadChildren: () => import('./editors/editors.module')
        .then(m => m.EditorsModule),
    },
    {
      path: 'tables',
      loadChildren: () => import('./tables/tables.module')
        .then(m => m.TablesModule),
    },
    {
    path: 'safety',
    children: [
      { path: 'events', component: EventsComponent },
      { path: 'events/types', component:TypesComponent}
    ]
  }
,
      {
    path: 'materiel',
    component: MaterielComponent,
  },
      {
    path: 'ressource',
    component: RessourceComponent,
  },
        {
    path: 'mission/overview',
    component: MissionOverviewComponent,
  },
{
  path: 'mission/overview/:id',
  component: MissionOverviewComponent,
},
      {
        path: 'mission-dashboard',
        component: MissionDashboardComponent,
      },
  {
        path: 'project-dashboard',
        component: ProjectDashboardComponent,
      },
        {
    path: 'teams/members',
    component: MembersComponent,
  },
   {
    path: 'teams/activities',
    component: ActivitiesComponent,
  },
     {
    path: 'teams/Performance',
    component: PerformanceComponent,
  },
    {
    path: 'safety/rapports',
    component: RapportsComponent,
  },
  {
  path: 'safety/incidents',
  component: IncidentsComponent
}
,
        {
    path: 'project/overview',
    component:       ProjectsComponent       ,
  },
  {
  path: 'tables/reports',
  component: ReportsViewerComponent
},
  {
  path: 'project-overview/:id',
  component: ProjectOverviewComponent,
},

    { path: 'equipe-detail/:equipeId', component: EquipeDetailComponent },

    {
      path: 'miscellaneous',
      loadChildren: () => import('./miscellaneous/miscellaneous.module')
        .then(m => m.MiscellaneousModule),
    },
    {
      path: '',
      redirectTo: 'dashboard',
      pathMatch: 'full',
    },
    {
      path: '**',
      component: NotFoundComponent,
    },
  ],
}];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class PagesRoutingModule {
}




