import { FormControl } from '@angular/forms';

export type RequestForm = {
  request: FormControl<string | null>;
  file: FormControl<File | null>;
  submit: FormControl<boolean | null>;
};
