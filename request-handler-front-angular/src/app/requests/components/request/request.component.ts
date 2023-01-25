import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { QueryTypes } from '../../constants/enums/query-types.enum';
import { SingleRequestForm } from '../../types/request.types';

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
  QueryTypes = QueryTypes;
}
