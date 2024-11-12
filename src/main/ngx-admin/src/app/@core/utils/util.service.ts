import {Injectable} from '@angular/core';
import {isNumeric} from 'rxjs-compat/util/isNumeric';

@Injectable({
    providedIn: 'root'
})
export class UtilService {
    public static ifObjectelse(obj: any, property: string, placeholder: any): any {
        if (obj) {
            return obj[property];
        } else {
            return placeholder;
        }
    }

    constructor() {
    }


    public static makeIterable(value: Array<any>): Array<any> {
        const sizeMap = UtilService.getSizeMap(value);
        let iterables = [];
        Object.keys(sizeMap).forEach(sizeKey => {
            let sizeObj = {};
            let templatesObj = sizeMap[sizeKey];
            sizeObj["size"] = sizeKey;
            sizeObj["templates"] = [];
            Object.keys(templatesObj).forEach(templateKey => {
                let templateObj = sizeMap[sizeKey][templateKey];
                templateObj["template"] = templateKey.trim();
                templateObj["size"] = sizeKey.trim();
                if (templateObj)
                    sizeObj["templates"].push(templateObj);
            });

            if (sizeObj["templates"].length) {
                iterables.push(sizeObj);
            }
        });
        return iterables;
    }

    public static getSizeMap(value: Array<any>) {
        let sizeMap = {};

        value.forEach(item => {

            let templates = item.Templates.split(",");

            let additional = UtilService.getAdditionalData(item);
            additional["status"] = true;

            item.Sizes.forEach(size => {
                // size = size.value;
                templates.forEach(template => {
                    if (!sizeMap[size]) {
                        sizeMap[size] = {};
                    }
                    let val = Object.assign({}, additional);
                    val.template = template;
                    val.size = size;
                    sizeMap[size][template] = val;

                });
            });
        });

        return sizeMap;
    }

    public static getAdditionalData(item: any): any {
        let nItem = Object.assign({}, item);
        delete nItem.Templates;
        delete nItem.Sizes;
        return nItem;
    }

    public static preProcessIterables(data: Array<any>, mappingType: string): Array<any> {
        let list = [];
        data.forEach(sizeObj => {
            sizeObj.templates.forEach(template => {
                if (template.status) {
                    let temp = {
                        templateId: template.template.trim(),
                        templateSize: template.size.trim(),
                    };

                    if (mappingType == 'SEASONAL') {
                        temp['startDate'] = template.startDate;
                        temp['endDate'] = template.endDate;
                    }
                    list.push(temp);
                }
            })
        });
        return list;
    }

    public static extractIdFromDruidFormat(value: string) {
        value = String(value);
        if (value.includes("[") && value.includes("]")) return value.substring(value.indexOf('[') + 1, value.indexOf(']'));
        else return value;
    }

    public static extractId(value: string): string {
        value = String(value);
        if (isNumeric(value))
            return value;
        value = String(value);
        return value.substring(value.indexOf('[') + 1, value.indexOf(']'));
    }

    public static extractCustomerId(value: string): string {
        value = String(value);
        if (value.substring(1).startsWith("CU")) return value;
        return value.substring(value.indexOf('[') + 1, value.indexOf(']'));
    }

    public static extractPartnerId(value: string): string {
        value = String(value);
        if (value.substring(1).startsWith("PR")) return value;
        return value.substring(value.indexOf('[') + 1, value.indexOf(']'));
    }

    isSet(entity: any): boolean {
        if (entity !== null && entity !== undefined) {
            if (typeof entity === 'object' && !Object.keys(entity).length) {
                return false;
            } else if (typeof entity === 'string' && entity.trim() === '') {
                return false;
            } else if (typeof entity === 'number' && isNaN(entity)) {
                return false;
            } else if (typeof entity === 'boolean') {
                return true;
            } else if (Array.isArray(entity) && entity.length === 0) {
                return false;
            }
            return true;
        }
        return false;
    }

    isStringSetAndNotNull(entity){
    return this.isSet(entity) && entity.trim() !== 'null';
    }

    removeEmptyValuesInDrilldown(payload: any): any {
        for (const key in payload) {
            if (payload.hasOwnProperty(key) && (!this.isSet(payload[key]))) {
                delete payload[key];
            }
        }
        return payload;
    }

    isNumber(value: string): boolean {
        return /^-?\d+(\.\d+)?$/.test(value);
    }
}
