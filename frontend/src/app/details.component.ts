import {Component, Inject, OnInit, ViewChild} from '@angular/core';
import {MatDialogRef} from '@angular/material/dialog';
import {FormBuilder, FormControl, FormGroup} from '@angular/forms';
import {UploadService} from './upload.service';
import {forkJoin} from 'rxjs';
import {MAT_DIALOG_DATA} from '@angular/material/dialog';
import {Marker} from './marker';
import {Upload} from './upload';

@Component({
  selector: 'app-details',
  templateUrl: './details.component.html',
  styleUrls: ['./details.component.css']
})
export class DetailsComponent implements OnInit {
  @ViewChild('file', { static: false }) file;
  public files: Set<File> = new Set();
  public serverFileData: Set<number> = new Set();

  progress;
  canBeClosed = true;
  primaryButtonText = 'Upload';
  uploading = false;
  uploadSuccessful = false;

  title = '';
  link = '';

  error = '';

  formGroup: FormGroup;

  existingUploads: Upload[];

  constructor(
    private formBuilder: FormBuilder,
    private dialogRef: MatDialogRef<DetailsComponent>,
    private uploadService: UploadService,
    @Inject(MAT_DIALOG_DATA) public existingMarker: Marker
  ) {}

  ngOnInit() {
    this.formGroup = this.formBuilder.group({
      title: new FormControl()
    });

    if (this.existingMarker != null) {
      this.title = this.existingMarker.name;
      this.link = this.existingMarker.link;
    }

    if (this.existingMarker != null) {
      this.uploadService.loadExistingUploads(this.existingMarker.id).subscribe(uploads => this.existingUploads = uploads);
    }
  }

  // TODO use this?
  submit(form) {
    this.dialogRef.close(`${form.value.title}`);
  }

  addFiles() {
    this.file.nativeElement.click();
  }

  onFilesAdded() {
    const files: { [key: string]: File } = this.file.nativeElement.files;
    for (const key in files) {
      if (!isNaN(parseInt(key, 10))) {
        this.files.add(files[key]);
      }
    }
  }

  closeDialog() {
    console.log('1');
    // if everything was uploaded already, just close the dialog
    if (this.uploadSuccessful) {
      return this.dialogRef.close({
        action: 'SAVE',
        title: this.title,
        link: this.link,
        files: this.serverFileData,
        existingUploads: this.existingUploads
      });
    }

    // set the component state to "uploading"
    this.uploading = true;

    // start the upload and save the progress map
    const uploadData = this.uploadService.upload(this.files)
    this.progress = uploadData.files;

    // convert the progress map into an array
    const allProgressObservables = [];
    for (const key of Object.keys(this.progress)) {
      allProgressObservables.push(this.progress[key].progress);

      this.progress[key].filename.subscribe(file => this.serverFileData.add(file));
    }

    uploadData.errors.subscribe(s => this.error = s);

    // Adjust the state variables

    // The OK-button should have the text "Finish" now
    this.primaryButtonText = 'Finish';

    // The dialog should not be closed while uploading
    this.canBeClosed = false;
    this.dialogRef.disableClose = true;

    // When all progress-observables are completed...
    forkJoin(allProgressObservables).subscribe(end => {
      this.completeUpload();
    });

    if (this.files.size === 0) {
      this.completeUpload();
    }
  }

  completeUpload(): void {
    // ... the dialog can be closed again...
    this.canBeClosed = true;
    this.dialogRef.disableClose = false;

    // ... the upload was successful...
    this.uploadSuccessful = true;

    // ... and the component is no longer uploading
    this.uploading = false;
  }

  getDownloadLink(upload: Upload) {
    return UploadService.getDownloadLink(upload);
  }

  removeUpload(id: number) {
    this.existingUploads = this.existingUploads.filter(upload => upload.id !== id);
  }

  deleteMarker() {
    return this.dialogRef.close({action: 'DELETE', files: this.serverFileData});
  }
}
