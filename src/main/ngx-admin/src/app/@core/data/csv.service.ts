import { Injectable } from '@angular/core';
import { Papa } from 'ngx-papaparse';

@Injectable({
  providedIn: 'root'
})
export class CsvService {

  constructor(private papa: Papa) { }


  parseCSV(file: File): Promise<any[]> {
    const promise = new Promise<any[]>((resolve, reject) => {
      this.papa.parse(file, {
        header: true, 
        skipEmptyLines: true,
        complete: (result) => {
          if (result.errors.length === 0) {
            resolve(result.data);
          } else {
            reject(result.errors);
          }
        },
      });
    });
    return promise;
  }

  checkMandatoryFields(data: any[], mandatoryFields: string[]): string {
    const headerFields = Object.keys(data[0]);
    const missingFields = mandatoryFields.filter(
      (field) => !headerFields.includes(field)
    );

    if (missingFields.length > 0) {
      return `Missing mandatory fields: <strong> ${missingFields.join(', ')} </strong>`;
    } 
  }


  isFileSizeValid(file: File, maxSize: number): boolean {
    const sizeMb = maxSize * 1024 * 1024;
    return file.size <= sizeMb;
  }



}
