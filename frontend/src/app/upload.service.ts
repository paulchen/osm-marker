import { Injectable } from '@angular/core';
import { HttpClient, HttpRequest, HttpEventType, HttpResponse } from '@angular/common/http';
import { Subject, Observable } from 'rxjs';
import {Upload} from './upload';
import {environment} from '../environments/environment';

const url = 'uploadFile';
const downloadUrl = 'files/{id}';
const uploadUrl = 'marker/{id}/uploads';

@Injectable()
export class UploadService {
  constructor(private http: HttpClient) {}

  static getDownloadLink(upload: Upload): string {
    return environment.backendUrl + downloadUrl.replace('{id}', String(upload.id));
  }

  public upload(files: Set<File>): {[key: string]: Observable<number>} {
    // this will be the our resulting map
    const status = {};

    files.forEach(file => {
      // create a new multipart-form for every file
      const formData: FormData = new FormData();
      formData.append('file', file, file.name);

      // create a http-post request and pass the form
      // tell it to report the upload progress
      const req = new HttpRequest('POST', environment.backendUrl + url, formData, {
        reportProgress: true
      });

      // create a new progress-subject for every file
      const progress = new Subject<number>();

      const filename = new Subject<string>();

      // send the http-request and subscribe for progress-updates
      this.http.request(req).subscribe(event => {
        if (event.type === HttpEventType.UploadProgress) {

          // calculate the progress percentage
          const percentDone = Math.round(100 * event.loaded / event.total);

          // pass the percentage into the progress-stream
          progress.next(percentDone);
        } else if (event instanceof HttpResponse) {
          // TODO check for "ok" status
          console.log(event);
          const wtf = <any>{};
          wtf.abc = event.body;
          filename.next(wtf.abc.fileData.id);
          filename.complete();

          // Close the progress-stream if we get an answer form the API
          // The upload is complete
          progress.complete();
        }
      });

      // Save every progress-observable in a map of all observables
      status[file.name] = {
        progress: progress.asObservable()
        ,
        filename: filename.asObservable()
      };
    });

    // return the map of progress.observables
    return status;
  }

  loadExistingUploads(markerId: number): Observable<Upload[]> {
    const requestUrl = environment.backendUrl +  uploadUrl.replace('{id}', String(markerId));
    return this.http.get<Upload[]>(requestUrl);
  }

  removeUpload(file: number): Observable<any> {
    const requestUrl = environment.backendUrl + downloadUrl.replace('{id}', String(file));
    return this.http.delete<any>(requestUrl);
  }
}
