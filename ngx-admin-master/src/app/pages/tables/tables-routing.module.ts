import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { TablesComponent } from './tables.component';
import { SmartTableComponent } from './smart-table/smart-table.component';
import { TreeGridComponent } from './tree-grid/tree-grid.component';
import { ComptesTableComponent } from './comptes-table/comptes-table.component';
import { AuthGuard} from '../../guards/auth.guard';
import { RoleGuard} from '../../guards/role.guard';
import { EtatMedicalComponent } from './etat-medical/etat-medical.component';
import { AntecedentsMedicalComponent } from './etat-medical/antecedents-medical/antecedents-medical.component';

import { AffectationRolesTableComponent } from './affectation-roles-table/affectation-roles-table.component';
import { MissionsComponent } from './missions/missions.component';
import { IncidentComponent } from './incident/incident.component';
import { FonctionTableComponent } from './fonction-table/fonction-table.component';
import { AttendanceComponent } from './attendance/attendance.component';

const routes: Routes = [{
  path: '',
  component: TablesComponent,
  children: [
    {
      path: 'smart-table',
      component: SmartTableComponent,
        canActivate: [AuthGuard, RoleGuard],
  data: { roles: ['ADMIN'] }
    },
  
   {
      path: 'fonction-table',
      component: FonctionTableComponent,
        canActivate: [AuthGuard, RoleGuard],
  data: { roles: ['ADMIN'] }
    },
  

  {
      path: 'etat-medical',
      component: EtatMedicalComponent,
      canActivate: [RoleGuard],
      data: { roles: ['ADMIN'] }
    },
    {
      path: 'etat-medical/:id',
      component: EtatMedicalComponent,
      canActivate: [RoleGuard],
      data: { roles: ['ADMIN'] }
    },
    {
      path: 'etat-medical/:id/antecedents',
      component: AntecedentsMedicalComponent,
      canActivate: [RoleGuard],
      data: { roles: ['ADMIN'] }
    },
{
  path: 'accounts',
  component:ComptesTableComponent ,
  canActivate: [AuthGuard, RoleGuard],
  data: { roles: ['ADMIN'] }
},
{
  path: 'accounts/:id',
  component: ComptesTableComponent ,
  canActivate: [AuthGuard, RoleGuard],
  data: { roles: ['ADMIN'] }
},
{
  path: 'affectation-roles/:id',
  component: AffectationRolesTableComponent    ,
  canActivate: [AuthGuard, RoleGuard],
  data: { roles: ['ADMIN'] }
},
{
  path: 'missions',
  component:  MissionsComponent,
  canActivate: [AuthGuard],
 
},
{
  path: 'incidents',
  
      component: IncidentComponent,

}
,
{
  path: 'pointage',
  
      component: AttendanceComponent,

}
,
    {
      path: 'tree-grid',
      component: TreeGridComponent,
    },
  ],
}];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class TablesRoutingModule { }

export const routedComponents = [
  TablesComponent,
  SmartTableComponent,
  TreeGridComponent,
];
