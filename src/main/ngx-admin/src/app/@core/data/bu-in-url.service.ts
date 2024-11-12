import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';
import {CookieService} from './cookie.service';

@Injectable({
  providedIn: 'root',
})
export class BuInUrlService {
  private redirSubject = new BehaviorSubject<void>(null);
  private configRedirSubject = new BehaviorSubject<void>(null);
  routeValue: string;
  buValueFromUrl: string;
  redir$ = this.redirSubject.asObservable();
  configRedir$ = this.configRedirSubject.asObservable();

  triggerRedir() {
    this.redirSubject.next();
    this.triggerConfigRedir();
  }

  triggerConfigRedir() {
    this.configRedirSubject.next();
  }

  constructor(private route: ActivatedRoute,
              private router: Router,
              private cookieService: CookieService) {
    setTimeout(() => {
      this.fetchBuFromUrlAndSetCookie();
    }, 1000);
  }

  public fetchBuFromUrlAndSetCookie() {
    this.routeValue = this.router.routerState.snapshot.url;
    const buSelectedRegex = /bu=([^;&]+)/;
    const match = this.routeValue.match(buSelectedRegex);
    this.buValueFromUrl = match ? match[1] : '';
    if (this.buValueFromUrl !== null && this.buValueFromUrl !== '') {
      this.cookieService.setBUSelectedCookie(this.buValueFromUrl);
    }
    if (!localStorage.getItem('isReload')) {
      localStorage.setItem('isReload', 'no reload');
      location.reload();
    } else {
      localStorage.removeItem('isReload');
    }

  }
}

