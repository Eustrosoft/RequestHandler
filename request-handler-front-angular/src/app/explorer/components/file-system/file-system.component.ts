import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { map, Observable, of } from 'rxjs';
import { ExplorerService } from '../../services/explorer.service';
import { FileSystemObject } from '../../interfaces/file-system-object.interface';
import { FileSystemObjectTypes } from '../../constants/enums/file-system-object-types.enum';

@Component({
  selector: 'app-file-system',
  templateUrl: './file-system.component.html',
  styleUrls: ['./file-system.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FileSystemComponent implements OnInit {
  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private explorerService: ExplorerService
  ) {}
  fsObjTypes = FileSystemObjectTypes;
  folders!: Observable<FileSystemObject[]>;

  ngOnInit() {
    this.folders = this.explorerService.getFsObjects();
  }

  folderClick(folder: FileSystemObject) {
    console.log(folder);
    this.folders = of(folder.child);
  }
}
