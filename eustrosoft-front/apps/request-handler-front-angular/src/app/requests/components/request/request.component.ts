import {
  ChangeDetectionStrategy,
  Component,
  Input,
  ViewChild,
} from '@angular/core';
import { FormGroup } from '@angular/forms';
import { QueryTypes } from '../../constants/enums/query-types.enum';
import { SingleRequestForm } from '../../types/request.types';
import { InputFileComponent } from '../../../core/components/input-file/input-file.component';

@Component({
  selector: 'app-request',
  templateUrl: './request.component.html',
  styleUrls: ['./request.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RequestComponent {
  @Input() form!: FormGroup<SingleRequestForm>;
  @Input() formNumber!: number;
  @Input() queryTypeOptions: string[] = [...Object.values(QueryTypes)];

  @ViewChild(InputFileComponent) inputFileComponent!: InputFileComponent;
  QueryTypes = QueryTypes;

  deleteFile(index: number): void {
    const control = this.form.get('file')!;
    control.value!.splice(index, 1);
    control.patchValue(control.value);
    this.inputFileComponent.patchInput();
  }
}
