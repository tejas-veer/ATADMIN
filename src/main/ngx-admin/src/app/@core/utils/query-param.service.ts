import {Injectable} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';

@Injectable({
    providedIn: 'root',
})
export class QueryParamService {
    constructor(private router: Router, private route: ActivatedRoute) {
    }

    getQueryParamValue(key: string): string | null {
        return this.route.snapshot.queryParamMap.get(key);
    }

    setQueryParam(key: string, value: any): void {
        this.setMultipleQueryParams({ [key]: value });
    }

    setMultipleQueryParams(params: { [key: string]: any }): void {
        const updatedParams = {...this.route.snapshot.queryParams};

        for (const [key, value] of Object.entries(params)) {
            if (!value) {
                delete updatedParams[key];
            } else {
                updatedParams[key] = value;
            }
        }

        this.router.navigate([], {
            queryParams: updatedParams,
            replaceUrl: true,
        });
    }
}