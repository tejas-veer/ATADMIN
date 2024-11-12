import { Component, OnInit,Input } from '@angular/core';

@Component({
  selector: 'floating-button',
  templateUrl: './floating-button.component.html',
  styleUrls: ['./floating-button.component.scss']
})
export class FloatingButtonComponent implements OnInit {

  @Input() color;
  @Input() icon;
  @Input() message;

  constructor() {
  }

  ngOnInit() {
  }

}
