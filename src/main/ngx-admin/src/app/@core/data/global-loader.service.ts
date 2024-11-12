import {Injectable} from '@angular/core';
import {SampleLayoutComponent} from "../../@theme/layouts";

@Injectable()
export class GlobalLoaderService {

    private _loaderObject;

    constructor() {

    }

    enable() {
        this.loaderObject.enabled= true;
    }

    disable() {
        this.loaderObject.enabled = false;
    }


    get loaderObject() {
        return this._loaderObject;
    }

    set loaderObject(value) {
        this._loaderObject = value;
    }

    disable_inside() {
        this.loaderObject.enabled = false;
    }
}

