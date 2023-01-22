import {Component, Input} from '@angular/core';
import {MatFormFieldAppearance} from '@angular/material/form-field';
import {FormControl} from '@angular/forms';

@Component({
  selector: 'app-textarea',
  templateUrl: './textarea.component.html',
  styleUrls: ['./textarea.component.scss'],
})
export class TextareaComponent {
  @Input() label: string = '';
  @Input() placeholder: string = '';
  @Input() fieldAppearance: MatFormFieldAppearance = 'fill';
  @Input() cols: number = 5;
  @Input() rows: number = 5;
  @Input() control!: FormControl;
  @Input() disabled: boolean = false;
}
