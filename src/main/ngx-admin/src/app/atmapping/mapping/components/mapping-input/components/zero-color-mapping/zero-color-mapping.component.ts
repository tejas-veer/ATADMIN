import { ErrorStackViewerComponent } from './../../../../../../@theme/components/error-stack-viewer/error-stack-viewer.component';
import {Component, Input, OnChanges, OnInit, SimpleChanges, ViewChild} from '@angular/core';
import {ConfigFetchService} from "../../../../../../@core/data/configfetch.service";
import {ActivatedRoute} from "@angular/router";
import {DefaultConfigComponent} from "../../../../../at-config/default-config/default-config.component";
import {GeneratorService} from "../../../../../../@core/data/generator.service";
import {MappingEntriesViewerComponent} from "../../../mapping-entries-viewer/mapping-entries-viewer.component";


@Component({
  selector: 'ngx-zero-color-mapping',
  templateUrl: './zero-color-mapping.component.html',
  styleUrls: ['./zero-color-mapping.component.scss']
})
export class ZeroColorMappingComponent implements OnInit, OnChanges {


  ngOnChanges(changes: SimpleChanges): void {
    if (changes.entityId || changes.level) {
        if (this.entityId && this.level) {
            this.generatedTemplates = [];
            this.configLoader = true;
            this.makeFrameworkStatusMap();
            this.showInput(true);
            this.showPreview(false);
            this.configService.getEntityConfigs(this.entityId, this.level).then(data => {
                let meta = data.meta;
                let config = data.data;
                this.configs = DefaultConfigComponent.process(meta, config);
                this.configLoader = false;
            });
        }
    }
}

@ViewChild('errorViewer') errorStack: ErrorStackViewerComponent;
@Input() level: string;
@Input() sizeList;
@Input() entityId: string;
@Input() entriesViewer: MappingEntriesViewerComponent;

configs: any;
configLoader: boolean = false;
generatedTemplates = [];
frameworkList = [];
frameworkStatus = {};
insertionStatus = {};
selectedSizes: Array<any> = [];
templateLoader: boolean;
inputDisplay: string = 'block';
previewDisplay: string = 'none';
baseResponse: any;
previewIterable: any;

constructor(private activatedRoute: ActivatedRoute, private configService: ConfigFetchService, private generatorService: GeneratorService) {

    this.generatorService.getZeroColorFrameworks().then(data => {
        console.log(data);
        this.frameworkList = data;
        this.makeFrameworkStatusMap();
    });
}

getTemplates(): void {
    this.generatedTemplates = [];
    if (this.validate()) {
        let frameworks = this.getSelectedFrameworks();
        let sizes = this.selectedSizes.map(item => item.value);
        this.templateLoader = true;
        this.showInput(false);
        this.templateLoader = true;
        this.generatorService.getZeroColor(this.entityId, this.level, frameworks, sizes).then(data => {
            this.baseResponse = data;
            this.previewIterable = this.getIterable(this.baseResponse);
            console.info(this.previewIterable);
            this.showPreview(true);
            this.templateLoader = false;
        }).catch(err => {
            console.error("Uncaught Error", err);
        }).then(() => {
            this.templateLoader = false;
        });
    }
}

makeFrameworkStatusMap() {
    this.frameworkStatus = {};
    this.frameworkList.forEach(framework => this.frameworkStatus[framework] = true);
    console.log(this.frameworkStatus);
}

getSelectedFrameworks(): Array<string> {
    return this.frameworkList.filter(framework => this.frameworkStatus[framework]);
}

ngOnInit() {
}

private validate(): boolean {
    this.errorStack.clear();

    if (this.getSelectedFrameworks().length == 0) {
        this.errorStack.pushError({
            title: "No Frameworks Selected",
            message: "Please Select Frameworks by clicking on their Id",
            type: "error"
        });
        return false;
    }

    if (this.selectedSizes.length == 0) {
        this.errorStack.pushError({
            title: "No Sizes Selected",
            message: "Please Select a size to generate",
            type: "error"
        });
        return false;
    }

    return true;
}


showInput(show: boolean) {

    this.inputDisplay = show ? 'block' : 'none';
}

showPreview(show: boolean) {

    this.previewDisplay = show ? 'block' : 'none';
}

private getIterable(baseResponse: any) {
    let sizeMap = {};
    this.insertionStatus = {};
    baseResponse.forEach(frameworkObj => {
        this.insertionStatus[frameworkObj.key] = {};
        frameworkObj.value.forEach(templateObj => {
            this.insertionStatus[frameworkObj.key][templateObj.uniqueTemplateHash] = {};
            templateObj.templateSizes.forEach(size => {
                this.insertionStatus[frameworkObj.key][templateObj.uniqueTemplateHash][size] = true;
                if (!sizeMap[size])
                    sizeMap[size] = [];
                sizeMap[size].push({
                    framework: frameworkObj.key,
                    hash: templateObj.uniqueTemplateHash,
                    templateId: templateObj.defaultTemplateId,
                    size: size,
                    customization: templateObj.templateCustomizationJson
                });
            })
        });
    });

    return Object.keys(sizeMap).map(key => {
        return {sizeName: key, templates: sizeMap[key]};
    });
}

insertTemplates(): void {
    let baseCopy = Object.assign([], this.baseResponse);
    let filteredObject = [];
    baseCopy.forEach(frameworkObj => {
        let filteredTemplates = [];
        frameworkObj.value.forEach(templateObj => {
            let selectedSizes = [];
            templateObj.templateSizes.forEach(size => {
                if (this.insertionStatus[frameworkObj.key][templateObj.uniqueTemplateHash][size]) {
                    selectedSizes.push(size);
                }
            });
            templateObj.templateSizes = selectedSizes;
            if (templateObj.templateSizes.length > 0) {
                filteredTemplates.push(templateObj);
            }
        });
        frameworkObj.value = filteredTemplates;
        if (filteredTemplates.length > 0) {
            filteredObject.push(filteredTemplates);
        }
    });
    this.generatorService.insertZeroColor(this.entityId, this.level, filteredObject).then(data => {
        console.log(data);
    }).catch(err => {
        console.error(err);
    });
}

}
