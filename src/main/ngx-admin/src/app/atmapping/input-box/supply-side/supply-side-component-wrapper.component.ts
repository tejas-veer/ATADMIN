import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {SupplySideTemplateComponent} from "./supply-side-template/supply-side-template.component";

@Component({
  selector: 'supply-side',
  templateUrl: './supply-side-component-wrapper.component.html',
  styleUrls: ['./supply-side-component-wrapper.component.scss']
})
export class SupplySideComponentWrapper implements OnInit {

  @Input() onSameColumn: boolean = false;
  @ViewChild(SupplySideTemplateComponent) childComponent: SupplySideTemplateComponent;

  constructor() {

  }

  ngOnInit() {
  }

  get selectedLevel(): string {
      return this.childComponent.selectedLevel;
  }

  getMappedInputValue(): string {
      return this.childComponent.getMappedInputValue();
  }

}
