import {Injectable, NgModule} from '@angular/core';
import 'rxjs/add/observable/of';
import { Http, HttpModule } from '@angular/http';
import {BaseUrlService} from './base-url.service';
import {AnalyticsService} from "../utils/analytics.service";
import { HttpClientModule } from '@angular/common/http';


let counter = 0;

@Injectable({ providedIn: 'root' })
export class UserService {

  session: any;
  private static advanced_mode: boolean;

  constructor(private http: Http,
              private analyticsService:AnalyticsService,
              private baseUrlService: BaseUrlService) {
    this.getSessionData().then(data => {
        this.analyticsService.addUser(data.admin_name)
    });
  }



  getSessionData(): Promise<any> {
    let entitySessionAPI = this.baseUrlService.getBaseUrl() + '/session';
    if (this.session)
      return Promise.resolve(this.session);
    return this.http.get(entitySessionAPI).toPromise().then(response => {
      this.session = response.json();
      return response.json();
    })
      .catch(UserService.handleError);
  }

  getSessionProperty(pname) {
    if (this.session)
      return this.session[pname];
    else {
      this.getSessionData();
    }
    return false;
  }

  getUserName() {
    let name = this.getSessionProperty('admin_name');
    if (name.indexOf('.') > 0) {
      let vars = name.split('.');
      name = vars[0] + ' ' + vars[1];
    }
    return name;
  }

  isSuperAdmin() {
    return this.getSessionProperty('super_admin');
  }

  isSuperGroup() {
    return this.getSessionProperty('super_group');
  }

  static setAdvancedMode(mode: boolean) {

    this.advanced_mode = mode ? true : false;
  }

  static getAdvancedMode() {
    return this.advanced_mode;
  }

  private static handleError(error: any): Promise<any> {
    return Promise.reject(error.message || error);
  }
}
