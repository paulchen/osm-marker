import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs';
import {MarkerData} from './marker-data';

const url = 'http://localhost:8080/osm/marker';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable()
export class MarkerService {
  constructor(private http: HttpClient) {
  }

  public getAllMarkers(): Observable<MarkerData> {
    return this.http.get<MarkerData>(url, httpOptions);
  }

  createMarker(name: string, files: Set<any>, latitude: number, longitude: number): Observable<any> {
    // TODO
    const data = {
      latitude: latitude,
      longitude: longitude,
      name: name,
      fileIds: Array.from(files)
    };
    return this.http.post<MarkerData>(url, data, httpOptions);
  }
}
