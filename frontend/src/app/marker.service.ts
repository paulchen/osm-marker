import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {MarkerData} from './marker-data';

const url = 'http://localhost:8080/osm/marker';

@Injectable()
export class MarkerService {
  constructor(private http: HttpClient) {
  }

  public getAllMarkers(): Observable<MarkerData> {
    return this.http.get<MarkerData>(url);
  }
}
