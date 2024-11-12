import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'tri-toggle',
  templateUrl: './tri-toggle.component.html',
  styleUrls: ['./tri-toggle.component.css'],
})
export class TriToggleComponent implements OnInit {
  @Output() selectEvent = new EventEmitter<any>();
  @Input() rowData: any;
  selectedState = "-1";
  currentState = "-1";

  constructor() { }

  ngOnInit(): void {
    this.selectedState = this.rowData.isActive.toString();
  }

  handleSelect(state) {
    const data = { "rowData": this.rowData, "state": state }
    this.selectEvent.emit(data)
  }



}
