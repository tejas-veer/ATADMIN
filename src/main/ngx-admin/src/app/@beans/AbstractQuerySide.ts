import {BaseUrlService} from "../@core/data/base-url.service";
import {EntityTypeMapping} from "./EntityTypeMapping";
import {CookieService} from "../@core/data/cookie.service";

export abstract class AbstractQuerySide {
    readonly querySide: string;
    private readonly levelsMap: Map<string, EntityTypeMapping>;
    private readonly levelLabels: Array<string>;
    private readonly levelsParamMap: Map<string, string>;
    selectedLevel: string;
    selectedValue: string;
    formatterForInputBoxes: string;

    protected constructor(queryString: string, levels: Array<EntityTypeMapping>,
                          protected baseUrlService: BaseUrlService, protected cookieService: CookieService) {
        this.querySide = queryString;
        this.levelsMap = new Map<string, EntityTypeMapping>();
        this.levelLabels = new Array<string>();
        this.levelsParamMap = new Map<string, string>();
        levels.forEach(item => {
            let label = item.getEntity().normalize();
            this.levelLabels.push(label);
            this.levelsMap.set(label, item);
            this.levelsParamMap.set(label.toUpperCase(), label);
        });
        this.selectedLevel = this.levelLabels.length > 0 ? this.levelLabels[0] : '';
        this.setFormatterForInputBoxes(this.selectedLevel);
    }

    setFormatterForInputBoxes(label: string): void {
        if (this.cookieService.getBUSelectedFromCookie() === 'MAX') {
            if (label === 'Customer' || label === 'Global_Customer') this.formatterForInputBoxes = 'Customer_HB';
            else if (label === 'Global_AdTag')  this.formatterForInputBoxes = 'AdTag';
            else if (label === 'Global_Portfolio')  this.formatterForInputBoxes = 'Portfolio';
            else if (label === 'Domain')  this.formatterForInputBoxes = 'Publisher_Domain';
            else if (label === 'IType') this.formatterForInputBoxes = 'Max_Integration_Type';
            else this.formatterForInputBoxes = label;
        } else this.formatterForInputBoxes = label;
    }

    toggleLevel(level: string): void {
        this.selectedLevel = level;
        this.setFormatterForInputBoxes(this.selectedLevel);
    }

    getSearchUrl(): string {
        this.setFormatterForInputBoxes(this.selectedLevel);
        return this.baseUrlService.getSearchUrlForDimension(this.formatterForInputBoxes);
    }

    getLevelsLabel(): Array<string> {
        return this.levelLabels;
    }

    getMappedInputValue(): string {
        if(this.selectedValue) {
            return this.levelsMap.get(this.selectedLevel).getValueUsingMapping(this.selectedValue);
        }
        else {
            return "";
        }
    }

    changedId(e): void {
        this.selectedValue = e[this.selectedLevel];
        this.setFormatterForInputBoxes(this.selectedLevel);
    }

    getLevelFromParam(param: string): string {
        if(this.levelsParamMap.has(param.toUpperCase())) {
            return this.levelsParamMap.get(param.toUpperCase());
        }
        return this.levelLabels[0];
    }
}