import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'ngx-error-stack-viewer',
  templateUrl: './error-stack-viewer.component.html',
  styleUrls: ['./error-stack-viewer.component.scss']
})
export class ErrorStackViewerComponent implements OnInit {

  constructor() { }

  errorStack: Array<StackError> = [];

    ngOnInit() {
    }

    public pushError(error: StackError) {
        error.type = error.type === 'error' ? 'danger' : 'warning';
        this.errorStack.push(error);
    }

    public closeAlert(alert: StackError) {
        const index: number = this.errorStack.indexOf(alert);
        this.errorStack.splice(index, 1);
    }

    public clear() {
        this.errorStack = [];
    }
}

export interface StackError {
  title: string;
  message: string;
  type: string;
  /**
   * type expects two values error or warning can be
   */
}