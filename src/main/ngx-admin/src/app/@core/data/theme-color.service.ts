import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ThemeColorService {
  maxBU: string = 'MAX';
  cmBU: string = 'CM';
  maxThemeColor: string = '#17B3A3';
  cmThemeColor: string = '#8A7FFF';

  getThemeColor(cname: string): string {
      if(cname == this.maxBU)
          return this.maxThemeColor;
      if(cname == this.cmBU)
          return this.cmThemeColor;
  }
  constructor() { }
}
