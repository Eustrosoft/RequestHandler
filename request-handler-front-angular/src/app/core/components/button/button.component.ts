import {Component, EventEmitter, Input, Output} from '@angular/core';
import {ThemePalette} from '@angular/material/core';

@Component({
  selector: 'app-button',
  templateUrl: './button.component.html',
  styleUrls: ['./button.component.scss'],
})
export class ButtonComponent {
  @Input() color: ThemePalette = undefined;
  @Input() buttonType: 'button' | 'submit' = 'button';
  @Input() buttonStyleType?:
    | 'raised'
    | 'stroked'
    | 'flat'
    | 'icon'
    | 'fab'
    | 'mini-fab';
  @Input() buttonText?: string;
  @Input() disabled: boolean = false;
  @Input() iconName: string = '';

  @Output() buttonClicked = new EventEmitter<void>();

  click(): void {
    this.buttonClicked.emit();
  }
}
