import {
  AfterViewInit,
  ChangeDetectionStrategy,
  Component,
  ElementRef,
  Input,
  OnDestroy,
  OnInit,
  ViewChild,
} from '@angular/core';
import { FormControl } from '@angular/forms';

@Component({
  selector: 'app-input-file',
  templateUrl: './input-file.component.html',
  styleUrls: ['./input-file.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class InputFileComponent implements OnInit, AfterViewInit, OnDestroy {
  @Input() control!: FormControl;
  @Input() buttonText: string = 'Select files';
  @Input() multiple: boolean = false;

  @ViewChild('fileInput') fileInput!: ElementRef<HTMLInputElement>;

  files: File[] = [];

  ngOnInit(): void {}

  ngAfterViewInit(): void {
    this.files = this.control.value;
    this.patchInput();
  }

  change(e: Event): void {
    const target = e.target as HTMLInputElement;
    const filesArray = Array.from(target.files!);
    if (filesArray.length > 1) {
      this.control.patchValue(filesArray);
      this.files = filesArray;
    } else {
      this.control.patchValue([filesArray[0]]);
      this.files = [filesArray[0]];
    }
  }

  delete(index: number): void {
    this.files.splice(index, 1);
    this.control.patchValue(this.files);
    this.patchInput();
  }

  clear(): void {
    this.fileInput.nativeElement.files = new DataTransfer().files;
    this.files = [];
  }

  patchInput(): void {
    const dt = new DataTransfer();
    this.files.forEach((file: File) => dt.items.add(file));
    this.fileInput.nativeElement.files = dt.files;
  }

  ngOnDestroy(): void {
    this.clear();
  }
}
