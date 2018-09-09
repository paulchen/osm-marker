import {Component, OnInit} from '@angular/core';
import {Feature, Map, View, Overlay as MapOverlay} from 'ol';
import {Tile, Vector} from 'ol/layer';
import * as source from 'ol/source';
import {OSM} from 'ol/source';
import {Point} from 'ol/geom';
import {fromLonLat, transform} from 'ol/proj';
import {Circle as CircleStyle, Fill, Stroke, Style} from 'ol/style';
import {Overlay} from '@angular/cdk/overlay';
import {DetailsComponent} from './details.component';
import {MatDialog} from '@angular/material';
import {MarkerService} from './marker.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  foo: AppComponent;
  vectorSource: Vector;
  map: Map;

  constructor(
      private overlay: Overlay,
      private dialog: MatDialog,
      private markerService: MarkerService) {
    this.foo = this;
  }

  openOverlay(coordinates): void {
    const fileNameDialogRef = this.dialog.open(DetailsComponent, { width: '50%', data: null });

    fileNameDialogRef
      .afterClosed()
      .subscribe(data => {
        if (data === 'null' || !data) {
          return;
        }

        console.log(data);
        const transformedCoordinates = transform(coordinates, 'EPSG:3857', 'EPSG:4326');
        this.markerService.createMarker(data.title, data.files, transformedCoordinates[1], transformedCoordinates[0]).subscribe(() => {
          const newFeature = new Feature({geometry: new Point(coordinates)});
          this.vectorSource.addFeature(newFeature);
        });

      });
    // const overlayRef = this.overlay.create({
    //   width: '400px',
    //   height: '600px'
    // });
    // const overlayPortal = new ComponentPortal(DetailsComponent);
    // overlayRef.attach(overlayPortal);
  }

  editMarker(marker): void {
    const fileNameDialogRef = this.dialog.open(DetailsComponent, { width: '50%', data: marker });

    fileNameDialogRef
      .afterClosed()
      .subscribe(data => {
        if (data === 'null' || !data) {
          return;
        }

        console.log(data);
        // TODO update marker

      });
  }

  ngOnInit(): void {
    this.map = new Map({
      target: 'map',
      layers: [
        new Tile({
          source: new OSM()
        })
      ],
      view: new View({
        center: fromLonLat([16.37, 48.21]),
        zoom: 12
      })
    });

    // map.on('click', function (event) {
    //   alert(transform(event.coordinate, 'EPSG:3857', 'EPSG:4326'));
    // });
    //
    // const feature = new Feature({
    //   geometry: new Point(fromLonLat([16.37, 48.21]))
    // });
    this.vectorSource = new source.Vector();
    const vector_layer = new Vector({
      source: this.vectorSource,
      style: new Style({
        image: new CircleStyle({
          radius: 4,
          fill: new Fill({
            color: '#ff0000'
          })
        })
      })
    });
    this.map.addLayer(vector_layer);

    // this.vectorSource.addFeature(feature);

    // map.addInteraction(new Draw({
    //   type: GeometryType.POINT,
    //   source: vectorSource
    // }));
    //
    this.map.on('singleclick', evt => {
      const feature = this.map.forEachFeatureAtPixel(evt.pixel, featureAtPixel => featureAtPixel);

      if (!feature) {
        this.openOverlay(evt.coordinate);
      } else {
        this.editMarker(feature.values_.data);
      }
    });

    this.markerService.getAllMarkers().subscribe(markerData => {
      markerData.markers.forEach(marker => {
        const feature = new Feature({
          geometry: new Point(fromLonLat([marker.longitude, marker.latitude])),
          data: marker
        });
        this.vectorSource.addFeature(feature);
      });
    });

    const tooltip = new MapOverlay({
      element: document.getElementById('info'),
      positioning: 'bottom-left'
    });
    tooltip.setMap(this.map);

    this.map.on('pointermove', evt => {
      const feature = this.map.forEachFeatureAtPixel(evt.pixel, featureAtPixel => {
        tooltip.setPosition(evt.coordinate);
        tooltip.getElement().innerHTML = featureAtPixel.values_.data.name;
        return featureAtPixel;
      });
      tooltip.getElement().style.display = feature ? '' : 'none';
      document.body.style.cursor = feature ? 'pointer' : '';
    });
  }
}
