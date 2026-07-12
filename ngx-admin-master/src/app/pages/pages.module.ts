import { NgModule } from '@angular/core';
import { NbMenuModule } from '@nebular/theme';

import { ThemeModule } from '../@theme/theme.module';
import { PagesComponent } from './pages.component';
import { DashboardModule } from './dashboard/dashboard.module';
import { ECommerceModule } from './e-commerce/e-commerce.module';
import { PagesRoutingModule } from './pages-routing.module';
import { MiscellaneousModule } from './miscellaneous/miscellaneous.module';
import { LoginComponent } from './login/login.component';
import { FormsModule } from '@angular/forms';
import { EmployeAccountComponent } from './employe-account/employe-account.component';
import { NbIconModule } from '@nebular/theme';
import { FilterPipe } from './materiel/filter.pipe';

import { CommonModule } from '@angular/common';
import { NbDialogModule } from '@nebular/theme';
import { 
  NbCardModule,
  NbButtonModule,

  NbSpinnerModule
} from '@nebular/theme';
import { DashbrdComponent } from './dashbrd/dashbrd.component';
import { MaterielComponent } from './materiel/materiel.component';
import { ReactiveFormsModule } from '@angular/forms';
import { MaterielDetailModalComponent } from './materiel/materiel-detail-modal/materiel-detail-modal.component';
import { AssignMissionModalComponent } from './materiel/assign-mission-modal/assign-mission-modal.component';
import { RessourceComponent } from './ressource/ressource.component';
import { MissionOverviewComponent } from './mission-overview/mission-overview.component';
import { ProjectOverviewComponent } from './project-overview/project-overview.component';
import { EquipmentTypeDetailComponent } from './mission-overview/equipment-type-detail/equipment-type-detail.component';
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
import { AssignActivityModalComponent } from './materiel/assign-activity-modal/assign-activity-modal.component';
import { DdashboardComponent } from './ddashboard/ddashboard.component';
import { ProductionStatsComponent } from './production-stats/production-stats.component';
import { DashboardyComponent } from './analytics/dashboardy/dashboardy.component';
import { PivotTableComponent } from './pivot-table/pivot-table.component';

@NgModule({
  imports: [
     NbCardModule,
    NbButtonModule,
   
    NbSpinnerModule,
     NbIconModule,
    PagesRoutingModule,
    ThemeModule,
    NbMenuModule,
    DashboardModule,
    ECommerceModule,
    MiscellaneousModule,
     FormsModule,
     CommonModule,
    NbDialogModule.forChild(), 
        ReactiveFormsModule,
               
  ],
  declarations: [
    PagesComponent,
    LoginComponent,
    EmployeAccountComponent,
    DashbrdComponent,
    MaterielComponent,
        FilterPipe,
    
        MaterielDetailModalComponent,
             AssignMissionModalComponent,
             RessourceComponent,
             MissionOverviewComponent,
             ProjectOverviewComponent,
             EquipmentTypeDetailComponent,
             EquipeDetailComponent,
             MissionDashboardComponent,
             ProjectDashboardComponent,
           
             MembersComponent,
                   
                         ActivitiesComponent,
                                             RapportsComponent,
                                             EventsComponent,
                                             TypesComponent,
                                             IncidentsComponent,
                                             ReportsViewerComponent,
                                             PerformanceComponent,
                                             AssignActivityModalComponent,
                                             DdashboardComponent,
                                             ProductionStatsComponent,
                                             DashboardyComponent,
                                             PivotTableComponent,
                                    
          
        
     

  ],
})
export class PagesModule {
}





