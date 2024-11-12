import {Component, Input, OnInit} from '@angular/core';


@Component({
  selector: 'loader-iframe',
  template: `
    <div style="position: relative;">
      <ngx-loader *ngIf="loader" style="position: absolute" [style.width.px]="width"></ngx-loader>
      <iframe (load)="loader=false" [src]="srUrl"  [height]="height" [width]="width" marginheight="0" marginwidth="0"
              frameborder="0" scrolling="NO" style="border: 1px solid teal"></iframe>  
      <br>
    </div>
  `,
})
export class IframeComponent implements OnInit {

  ngOnInit(): void {
    this.srUrl = this.frameurl;
    this.loader = true;
    let sizeAr = this.size.split('x');
    this.width = sizeAr[0];
    this.height = sizeAr[1];
  }

  @Input() frameurl;
  @Input() size;

  public loader: boolean = true;
  srUrl: any;
  height: any;
  width: any;
  style: any;

  constructor() {
    this.loader = true;
  }


}
