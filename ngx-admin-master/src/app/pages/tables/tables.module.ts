import { NgModule } from '@angular/core';
import { NbCardModule, NbIconModule, NbInputModule, NbTreeGridModule } from '@nebular/theme';
import { Ng2SmartTableModule } from 'ng2-smart-table';

import { ThemeModule } from '../../@theme/theme.module';
import { TablesRoutingModule, routedComponents } from './tables-routing.module';
import { FsIconComponent } from './tree-grid/tree-grid.component';
import { ContratsTableComponent } from './contrats-table/contrats-table.component';
import { ComptesTableComponent } from './comptes-table/comptes-table.component';
import { ActionsDropdownComponent } from './smart-table/actions-dropdown/actions-dropdown.component';
import { NbButtonModule, NbContextMenuModule,  NbMenuModule, NbToastrModule } from '@nebular/theme';
import { AffectationRolesTableComponent } from './affectation-roles-table/affectation-roles-table.component';
import { MissionsComponent } from './missions/missions.component';
import { ViewButtonComponent } from './missions/view-button/view-button.component';
import { EtatMedicalComponent } from './etat-medical/etat-medical.component';
import { AntecedentsMedicalComponent } from './etat-medical/antecedents-medical/antecedents-medical.component';
import { MedicalHistoryButtonComponent } from './etat-medical/medical-history-button/medical-history-button.component';
import { IncidentComponent } from './incident/incident.component';
import { FonctionTableComponent } from './fonction-table/fonction-table.component';
import { ProjectsComponent } from './projects/projects.component';
import { ProjectViewButtonComponent } from './projects/project-view-button/project-view-button.component';
import { MissionFilterComponent } from './projects/mission-filter/mission-filter.component';
import { FormsModule } from '@angular/forms';
import { AttendanceComponent } from './attendance/attendance.component';
@NgModule({
  imports: [
    NbCardModule,
    NbTreeGridModule,
    NbIconModule,
    NbInputModule,
    ThemeModule,
    TablesRoutingModule,
    Ng2SmartTableModule,
      NbContextMenuModule,  // ✅ Required for nbContextMenu
          
    NbMenuModule,         // ✅ Required for menu items
    NbToastrModule,     
    FormsModule
  ],
  declarations: [
    ...routedComponents,
    FsIconComponent,
    ContratsTableComponent,
    ComptesTableComponent,
    ActionsDropdownComponent,
    AffectationRolesTableComponent,
    MissionsComponent,
    ViewButtonComponent,
    EtatMedicalComponent,
    AntecedentsMedicalComponent,
    MedicalHistoryButtonComponent,
    IncidentComponent,
    FonctionTableComponent,
    ProjectsComponent,
    ProjectViewButtonComponent,
    MissionFilterComponent,
    AttendanceComponent,
  
  
  ],
    exports: [ActionsDropdownComponent]

})
export class TablesModule { }
