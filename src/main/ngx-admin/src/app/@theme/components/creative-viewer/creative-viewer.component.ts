import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {DomSanitizer} from '@angular/platform-browser';
import {CookieService} from "../../../@core/data/cookie.service";

@Component({
    selector: 'creative-viewer',
    templateUrl: './creative-viewer.component.html',
    styleUrls: ['./creative-viewer.component.scss']
})
export class CreativeViewerComponent implements OnInit, OnChanges {
    @Input() size: any;
    @Input() creative: any;
    @Input() customization: any;
    @Input() type: string = 'TEMPLATE';
    frameUrl: any;

    constructor(private domSan: DomSanitizer,
                public cookieService: CookieService) {
    }

    ngOnInit() {
    }

    ngOnChanges(changes: SimpleChanges): void {
        if (this.size == 'ALL' || !this.size)
            this.size = '300x250';
        this.type = this.type.toUpperCase();
        switch (this.type) {
            case "FRAMEWORK":
                this.frameUrl = this.domSan.bypassSecurityTrustResourceUrl('//cm.internal.reports.mn/template/framework/preview.php?frm_id=' + this.creative + '&pagename=keywords-only-' + this.size + '.html&isiframe=1');
                break;
            default:
                let url = '//cm.internal.reports.mn/common/preview.php?TID=' + this.creative + '&Page=keywords-only-' + this.size + '&isiframe=1';
                if (this.customization) {
                    url += "&jsonData=" + encodeURIComponent(this.customization);
                } else {
                    let width = this.size.split('x')[0];
                    let height = this.size.split('x')[1];
                    let apiEndPoint = this.cookieService.getPreviewEndPointBasedOnBU();
                    url = apiEndPoint + '?tid=' + this.creative + '&width=' + width + '&height=' + height;
                }
                this.frameUrl = this.domSan.bypassSecurityTrustResourceUrl(url);
        }
    }
}
