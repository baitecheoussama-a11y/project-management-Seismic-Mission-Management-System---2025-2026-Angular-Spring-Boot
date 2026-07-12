import { Component, AfterViewInit } from '@angular/core';

declare const google: any;

@Component({
  selector: 'ngx-gmaps',
  styleUrls: ['./gmaps.component.scss'],
  templateUrl: './gmaps.component.html',
})
export class GmapsComponent implements AfterViewInit {
  // Algeria center coordinates
  readonly position = { lat: 28.0, lng: 2.0 };
  private map: any;

  ngAfterViewInit() {
    this.initMap();
  }

  initMap() {
    const mapOptions = {
      center: new google.maps.LatLng(this.position.lat, this.position.lng),
      zoom: 6,
      minZoom: 5,
      maxZoom: 10,
      mapTypeId: google.maps.MapTypeId.ROADMAP,
      restriction: {
        latLngBounds: {
          north: 38.0,
          south: 18.0,
          east: 12.0,
          west: -8.7
        },
        strictBounds: true
      }
    };

    this.map = new google.maps.Map(document.getElementById('algeria-map'), mapOptions);

    // Optional: Add Algeria boundary
    this.addAlgeriaBoundary();
  }

  addAlgeriaBoundary() {
    // Simple rectangle to show Algeria bounds
    const bounds = {
      north: 38.0,
      south: 18.0,
      east: 12.0,
      west: -8.7
    };

    const algeriaBounds = new google.maps.LatLngBounds(
      new google.maps.LatLng(bounds.south, bounds.west),
      new google.maps.LatLng(bounds.north, bounds.east)
    );

    const boundary = new google.maps.Rectangle({
      bounds: algeriaBounds,
      strokeColor: "#ff7800",
      strokeWeight: 2,
      fillColor: "#ff7800",
      fillOpacity: 0.1,
      map: this.map
    });
  }
}