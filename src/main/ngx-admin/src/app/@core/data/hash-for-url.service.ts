import { Injectable } from '@angular/core';
import { v4 as uuidv4 } from 'uuid';
import * as CryptoJS from 'crypto-js';

@Injectable({
  providedIn: 'root',
})
export class HashForUrlService {

  constructor() { }

  generateHash(blob: string): string {
    const hash = CryptoJS.MD5(CryptoJS.enc.Latin1.parse(blob));
    return hash.toString(CryptoJS.enc.Hex);
  }
}
