import { Component, OnInit } from '@angular/core';
import {CookieService} from '../@core/data/cookie.service';
import { NbMediaBreakpoint, NbMediaBreakpointsService, NbThemeService } from '@nebular/theme';

@Component({
  selector: 'ngx-bu-selection',
  templateUrl: './bu-selection.component.html',
  styleUrls: ['./bu-selection.component.scss'],
})
export class BuSelectionComponent implements OnInit {
  buSelected: null;

  constructor(private cookieService: CookieService) {
  }

  ngOnInit() {
  }


  initialiseBU(bu) {
    this.buSelected = bu;
    this.cookieService.setBUSelectedCookie(bu);
  }
}
