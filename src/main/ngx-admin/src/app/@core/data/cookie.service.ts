import {Injectable} from '@angular/core';

@Injectable()
export class CookieService {
    exDays: number = 30;

    getCookie(cname: string): string {
        var name = cname + '=';
        var decodedCookie = decodeURIComponent(document.cookie);
        var ca = decodedCookie.split(';');
        for (var i = 0; i < ca.length; i++) {
            var c = ca[i];
            while (c.charAt(0) == ' ') {
                c = c.substring(1);
            }
            if (c.indexOf(name) == 0) {
                return c.substring(name.length, c.length);
            }
        }
        return '';
    }

    setCookie(cname: string, cvalue: string) {
        const d = new Date();
        d.setTime(d.getTime() + (this.exDays * 24 * 60 * 60 * 1000));
        let expires = "expires=" + d.toUTCString();
        document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
    }

    getBUSelectedFromCookie() {
        return this.getCookie('cookie-bu-selected');
    }

    setBUSelectedCookie(cvalue: string) {
        this.setCookie('cookie-bu-selected', cvalue);
    }

    getMaxMetricsSettingCookie() {
        return localStorage.getItem('max-metrics-settings');
    }

    setMaxMetricSettingsCookie(metricsSetting: string) {
        localStorage.setItem('max-metrics-settings', metricsSetting);
    }

    getCmMetricsSettingsCookie() {
        return localStorage.getItem('cm-metrics-settings');
    }

    setCmMetricSettingsCookie(metricsSetting: string) {
        localStorage.setItem('cm-metrics-settings', metricsSetting);
    }

    getCurrentApiPrefix(): string {
        let url = window.location.href;
        let apiPrefix = `http://atinterface.internal.media.net/ATAdmin/`;
        try {
            if (url.includes("localhost")) {
                apiPrefix = `http://localhost:8082/ATAdmin/`;
            } else if (url.includes("access.mn")) {
                apiPrefix = `https://atinterface.access.mn/ATAdmin/`;
            } else if (url.includes("atinterface-stg")) {
                apiPrefix = `http://atinterface-stg.internal.media.net/ATAdmin/`;
            }
        } catch (e) {
        }
        return apiPrefix;
    }

    getPreviewEndPointBasedOnBU(): string {
        let buSelectedFromCookie = this.getBUSelectedFromCookie();
        let apiEndPoint = 'template.jsp';
        if (buSelectedFromCookie == 'MAX') {
            apiEndPoint = 'template_test.jsp';
        }
        return this.getCurrentApiPrefix() + apiEndPoint;
    }

    getBulkPreviewEndPointBasedOnBU(): string {
        return this.getCurrentApiPrefix() + "bulk.jsp";
    }

    getMapLocalImageEndPoint() {
        let url = window.location.href;
        if (url.includes("localhost")) {
            return "http://localhost:8082/ATAdmin/mapLocalImage";
        }
        return this.getCurrentApiPrefix() + "mapLocalImage";
    }
}
