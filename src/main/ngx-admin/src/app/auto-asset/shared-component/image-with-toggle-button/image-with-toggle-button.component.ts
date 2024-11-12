import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';


@Component({
  selector: 'image-with-toggle-button',
  templateUrl: './image-with-toggle-button.component.html',
  styleUrls: ['./image-with-toggle-button.component.css']
})
export class ImageWithToggleButtonComponent implements OnInit {
  @Input() imageSrc: string;
  @Input() value: string;
  @Input() rowData: any;
  @Output() toggledEvent = new EventEmitter<any>();

  constructor() { }

  ngOnInit(): void {
    this.imageSrc = this.value;
  }

  onToggle(event: any) {
    this.toggledEvent.emit(event);
  }
}