import {
  AfterViewInit,
  ChangeDetectionStrategy,
  Component,
  ElementRef,
  Input,
  OnDestroy,
  ViewChild,
} from '@angular/core';
import { FormControl } from '@angular/forms';

@Component({
  selector: 'app-input-file',
  templateUrl: './input-file.component.html',
  styleUrls: ['./input-file.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class InputFileComponent implements OnDestroy {
  @Input() control!: FormControl;
  @ViewChild('fileInput') fileInput!: ElementRef;

  filename: string = '';

  change(e: Event): void {
    const target = e.target as HTMLInputElement;
    const file = target.files!.item(0);
    this.control.patchValue(file);
    this.filename = file!.name;
  }

  clear(): void {
    const el = this.fileInput.nativeElement as HTMLInputElement;
    el.files = new DataTransfer().files;
    this.filename = '';
    this.control.reset();
  }

  ngOnDestroy(): void {
    this.clear();
  }
}
