import { Component, OnInit,Input } from '@angular/core';

@Component({
  selector: 'ngx-loader',
  templateUrl: './loader.component.html',
  styleUrls: ['./loader.component.scss']
})
export class LoaderComponent implements OnInit {
  @Input() type: string = 'material';

  constructor() {
   }

  ngOnInit(): void {
  }

}
