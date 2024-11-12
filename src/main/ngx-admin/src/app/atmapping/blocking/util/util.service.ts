import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class UtilService {

  constructor() { }

  public static getCreativeType(row: any): string {
    let type = '';
    if (row.Framework)
      type = 'Framework';
    else
      type = 'Template';
    return type;
  }

  public static isNumeric(n): boolean {
    return !isNaN(parseFloat(n)) && isFinite(n);
  }
}
