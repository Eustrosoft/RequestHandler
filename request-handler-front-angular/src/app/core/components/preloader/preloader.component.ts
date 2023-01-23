import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { ThemePalette } from '@angular/material/core';
import { ProgressSpinnerMode } from '@angular/material/progress-spinner';

@Component({
  selector: 'app-preloader',
  templateUrl: './preloader.component.html',
  styleUrls: ['./preloader.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PreloaderComponent {
  @Input() color: ThemePalette = 'primary';
  @Input() diameter: number = 50;
  @Input() strokeWidth: number = 5;
  @Input() mode: ProgressSpinnerMode = 'indeterminate';
  @Input() additionalClasses: string = '';
}
