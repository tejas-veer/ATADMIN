import { Component, OnInit, Input } from '@angular/core';
import { ViewCell } from 'ng2-smart-table';

@Component({
  selector: 'asset-image',
  template: '<img src="{{imageSrc}}" alt="Image">',
  styles: ['img{ width : 20rem }']
})
export class AssetImageComponent implements ViewCell, OnInit {
  imageSrc: String;

  @Input() value: string;
  @Input() rowData: any;

  constructor() { }

  ngOnInit(): void {
    this.imageSrc = this.value;
  }

}
