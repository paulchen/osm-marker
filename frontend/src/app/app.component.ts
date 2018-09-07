import {Component, OnInit} from '@angular/core';
import {Feature, Map, View} from 'ol';
import {Tile, Vector} from 'ol/layer';
import * as source from 'ol/source';
import {OSM} from 'ol/source';
import {Point} from 'ol/geom';
import {fromLonLat, transform} from 'ol/proj';
import {Fill, Style, Stroke, Circle as CircleStyle} from 'ol/style';
import {Overlay} from '@angular/cdk/overlay';
import {DetailsComponent} from './details.component';
import {MatDialog} from '@angular/material';
import {filter} from 'rxjs/operators';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  foo: AppComponent;
  vectorSource: Vector;

  constructor(private overlay: Overlay, private dialog: MatDialog) {
    this.foo = this;
  }

  openOverlay(coordinates): void {
    const fileNameDialogRef = this.dialog.open(DetailsComponent, { width: '50%' });

    fileNameDialogRef
      .afterClosed()
      .subscribe(name => {
        if (name === 'null' || !name) {
          return;
        }

        console.log(name);
        const newFeature = new Feature({geometry: new Point(coordinates)});
        this.vectorSource.addFeature(newFeature);
      });
    // const overlayRef = this.overlay.create({
    //   width: '400px',
    //   height: '600px'
    // });
    // const overlayPortal = new ComponentPortal(DetailsComponent);
    // overlayRef.attach(overlayPortal);
  }

  ngOnInit(): void {
    const map = new Map({
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
    const feature = new Feature({
      geometry: new Point(fromLonLat([16.37, 48.21]))
    });
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
    map.addLayer(vector_layer);

    this.vectorSource.addFeature(feature);

    // map.addInteraction(new Draw({
    //   type: GeometryType.POINT,
    //   source: vectorSource
    // }));
    //
    map.on('singleclick', (evt) => {
      const coordinates = evt.coordinate;

      this.openOverlay(coordinates);
    });
  }
}
