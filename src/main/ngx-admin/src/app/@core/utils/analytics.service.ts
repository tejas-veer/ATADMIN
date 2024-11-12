import { Injectable } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { Location } from '@angular/common';
import { filter } from 'rxjs/operators/filter';
import {CookieService} from '../data/cookie.service';
import {GoogleAnalyticsConfig} from "../data/ga-configs";

declare const ga: any;

@Injectable()
export class AnalyticsService {
    gtag: any;
    TRACKING_ID = 'G-HVK59FY0J5';
    BUTTON: string = 'button';
    user: any;
    reverseLookup: Record<string, string> = {};

    constructor(private location: Location, private router: Router, private cookieService: CookieService) {
        this.gtag = window['gtag'];
        Object.keys(GoogleAnalyticsConfig).forEach(key => {
            this.reverseLookup[GoogleAnalyticsConfig[key]] = key;
        });
    }

    addUser(user: string) {
        this.user = user;
        this.gtag('set', {'user_id': user}); // Set the user ID using signed-in user_id.
    }

    trackPageViews() {
        this.router.events
            .pipe(
                filter(event => event instanceof NavigationEnd)
            )
            .subscribe((event: NavigationEnd) => {
                this.gtag('config', this.TRACKING_ID, {
                    'page_path': event.urlAfterRedirects,
                });
            });
    }

    trackEventOnGA(eventName: string) {
        const enumName = this.reverseLookup[eventName];
        this.gtag('event', enumName + '_' + this.cookieService.getBUSelectedFromCookie(), {
            'event_category': this.BUTTON,
            'event_label': enumName,
            'user': this.user,
        });
    }
}