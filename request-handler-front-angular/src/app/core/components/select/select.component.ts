import {
  ChangeDetectionStrategy,
  Component,
  Input,
  OnInit,
} from '@angular/core';
import { MatFormFieldAppearance } from '@angular/material/form-field';
import { FormControl } from '@angular/forms';

@Component({
  selector: 'app-select',
  templateUrl: './select.component.html',
  styleUrls: ['./select.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SelectComponent implements OnInit {
  @Input() label: string = '';
  @Input() placeholder: string = '';
  @Input() fieldAppearance: MatFormFieldAppearance = 'fill';
  @Input() control!: FormControl;
  @Input() options!: any;

  ngOnInit(): void {}
}
