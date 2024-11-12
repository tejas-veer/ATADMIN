import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {CookieService} from "../@core/data/cookie.service";

@Component({
    selector: 'app-atmapping',
    template: `        
        <app-input-box></app-input-box>
        <at-blocking *ngIf="section == 'blocking'"></at-blocking>
        <at-mapping *ngIf="section == 'mapping'"></at-mapping>
    `,
    styleUrls: ['./atmapping.component.scss']
})
export class AtmappingComponent implements OnInit {
    section: string;

    constructor(private activatedRoute: ActivatedRoute,
                private cookieService: CookieService) {
        this.activatedRoute.params.subscribe(params => {
            this.section = params.section;
        });

    }

    ngOnInit(): void {
    }
}

