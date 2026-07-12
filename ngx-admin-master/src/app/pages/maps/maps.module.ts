import { NgModule } from '@angular/core';
import { GoogleMapsModule } from '@angular/google-maps';
import { LeafletModule } from '@asymmetrik/ngx-leaflet';
import { NgxEchartsModule } from 'ngx-echarts';
import { NbCardModule } from '@nebular/theme';

import { ThemeModule } from '../../@theme/theme.module';
import { MapsRoutingModule, routedComponents } from './maps-routing.module';
import { AlgeriaMapComponent } from './algeria-map/algeria-map.component';
import { MissionFilterComponent } from './algeria-map/mission-filter/mission-filter.component';
import { MissionMapFilterComponent } from './algeria-map/mission-map-filter/mission-map-filter.component';

@NgModule({
  imports: [
    ThemeModule,
    GoogleMapsModule,
    LeafletModule.forRoot(),
    MapsRoutingModule,
    NgxEchartsModule,
    NbCardModule,
  ],
  exports: [],
  declarations: [
    ...routedComponents,
    AlgeriaMapComponent,
    MissionFilterComponent,
    MissionMapFilterComponent,
  ],
})
export class MapsModule { }
