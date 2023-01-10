import { Component, Input } from '@angular/core';
import { FormControl } from '@angular/forms';
import { MatFormFieldAppearance } from '@angular/material/form-field';

@Component({
  selector: 'app-input',
  templateUrl: './input.component.html',
  styleUrls: ['./input.component.scss'],
})
export class InputComponent {
  @Input() label: string = '';
  @Input() placeholder: string = '';
  @Input() fieldAppearance: MatFormFieldAppearance = 'fill';
  @Input() control!: FormControl;
}
