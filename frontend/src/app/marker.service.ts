import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs';
import {MarkerData} from './marker-data';
import {environment} from '../environments/environment';

const url = 'marker';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable()
export class MarkerService {
  constructor(private http: HttpClient) {
  }

  public getAllMarkers(): Observable<MarkerData> {
    return this.http.get<MarkerData>(environment.backendUrl + url, httpOptions);
  }

  createMarker(name: string, link: string, files: Set<any>, latitude: number, longitude: number): Observable<any> {
    // TODO
    const data = {
      latitude: latitude,
      longitude: longitude,
      name: name,
      link: link,
      fileIds: Array.from(files)
    };
    return this.http.post<MarkerData>(environment.backendUrl + url, data, httpOptions);
  }

  updateMarker(id: number, name: string, link: string, files: number[]) {
    // TODO
    const data = {
      name: name,
      link: link,
      fileIds: files
    };
    const postUrl = environment.backendUrl + url + '/' + id;
    return this.http.post<MarkerData>(postUrl, data, httpOptions);
  }

  deleteMarker(id: number) {
    const deleteUrl = environment.backendUrl + url + '/' + id;
    return this.http.delete<any>(deleteUrl, httpOptions);
  }
}
