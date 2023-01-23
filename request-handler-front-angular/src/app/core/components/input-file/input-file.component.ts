import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { FormControl } from '@angular/forms';

@Component({
  selector: 'app-input-file',
  templateUrl: './input-file.component.html',
  styleUrls: ['./input-file.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class InputFileComponent {
  @Input() control!: FormControl;

  filename: string = '';

  change(e: Event) {
    const target = e.target as HTMLInputElement;
    const file = target.files!.item(0);
    this.control.patchValue(file);
    this.filename = file!.name;
  }
}
