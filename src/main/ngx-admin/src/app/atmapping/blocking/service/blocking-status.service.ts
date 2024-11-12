import { Injectable } from '@angular/core';
import {Subject} from 'rxjs/Subject';

@Injectable({
  providedIn: 'root'
})
export class BlockingStatusService {

  public event: Subject<any>;

  constructor() {
    this.event = new Subject();
  }

  public put(type, status, tuple) {
    this.event.next({type: type, status: status, tuple: tuple});
  }}
