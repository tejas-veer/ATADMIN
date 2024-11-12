import { Injectable } from '@angular/core';
import {Subject} from 'rxjs/Subject';

@Injectable({
  providedIn: 'root'
})
export class EmitterService {

  eventBus$ = new Subject();
  event: any = this.eventBus$;

  next() {
    this.eventBus$.next();
  }
}
