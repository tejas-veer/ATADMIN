import {Component, OnInit} from '@angular/core';
import {CookieService} from "../@core/data/cookie.service";

@Component({
    selector: 'ngx-atreporting',
    template: `
        <cm-reporting *ngIf="isBusinessUnitCM"></cm-reporting>
        <ngx-autocomplete-entity-input *ngIf="!isBusinessUnitCM"></ngx-autocomplete-entity-input>
    `,
    styleUrls: []
})
export class ATReportingComponent implements OnInit {
    isBusinessUnitCM: boolean = false;

    constructor(private cookieService: CookieService,) {

    }

    ngOnInit(): void {
        if (this.cookieService.getBUSelectedFromCookie() === 'CM')
            this.isBusinessUnitCM = true;
    }

}
