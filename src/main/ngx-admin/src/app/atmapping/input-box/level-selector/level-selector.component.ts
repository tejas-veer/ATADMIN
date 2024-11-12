import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';

@Component({
    selector: 'ngx-level-selector',
    templateUrl: './level-selector.component.html',
    styleUrls: ['./level-selector.component.scss']
})
export class LevelSelectorComponent implements OnInit {

    @Input() levels: Array<string>;
    @Input() querySide: string;
    @Output() selectedLevelEvent = new EventEmitter<string>();
    @Input() selectedLevel: string;

    constructor() {
    }

    ngOnInit() {
    }

    toggleSelectedLevel() {
        this.selectedLevelEvent.emit(this.selectedLevel);
    }
}
