import {
  AfterViewInit,
  ChangeDetectionStrategy,
  Component,
  ElementRef,
  Input,
  OnDestroy,
  OnInit,
  TemplateRef,
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
  @Input() buttonText: string = 'Select files';
  @Input() multiple: boolean = false;

  @ViewChild('fileInput') fileInput!: ElementRef<HTMLInputElement>;

  change(e: Event): void {
    const target = e.target as HTMLInputElement;
    if (target!.files!.length === 0) {
      this.control.patchValue([]);
      return;
    }
    const filesArray = Array.from(target!.files!);
    if (filesArray.length > 1) {
      this.control.patchValue(filesArray);
    } else {
      this.control.patchValue([filesArray[0]]);
    }
  }

  clear(): void {
    this.fileInput.nativeElement.files = new DataTransfer().files;
  }

  patchInput(): void {
    const dt = new DataTransfer();
    this.control.value.forEach((file: File) => dt.items.add(file));
    this.fileInput.nativeElement.files = dt.files;
  }

  ngOnDestroy(): void {
    this.clear();
  }
}
