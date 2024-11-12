import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot} from '@angular/router';
import {Observable} from 'rxjs';
import {ToastingService} from "../@core/utils/toaster.service";
import {UtilService} from '../@core/utils/util.service';
import {isNumeric} from 'rxjs/util/isNumeric';

@Injectable({
  providedIn: 'root'
})
export class EntityIdGuard implements CanActivate {
  canActivate(next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean> | Promise<boolean> | boolean {
    let supply = next.params.supply.toLowerCase();
    const supplyValue = next.params.supplyId.trim();
    const demand = next.params.demand.toLowerCase();
    const demandValue = next.params.demandId.trim();
    let valid = false;

    if (supplyValue === '' && demandValue.length > 0) {
      switch (demand) {
        case 'adgroup':
          let adgroupId = UtilService.extractId(demandValue);
          valid = isNumeric(adgroupId);
          break;
        case 'campaign':
          let campaignId = UtilService.extractId(demandValue);
          valid = isNumeric(campaignId);
          break;
        case 'addomain':
          valid = true;
          break;
        case 'advertiser':
          let advertiserId = UtilService.extractId(demandValue);
          valid = isNumeric(advertiserId);
          break;
      }
    }
    else {
      let entityId = supplyValue.substring(1);
      switch (supply) {
        case 'partner':
          valid = entityId.startsWith('PR');
          break;
        case 'customer':
        case 'global_customer':
          valid = entityId.startsWith('CU');
          break;
        case 'adtag':
        case 'global_adtag':
        case 'domain':
          valid = !(entityId.startsWith('PR') || entityId.startsWith('CU'));
          break;
        case 'entity':
        case 'itype':
          valid = true;
          break;
        case 'portfolio':
        case 'global_portfolio':
          valid = entityId.startsWith('PO');
          break;
      }
    }

    if (!valid) {
      this.showErrorToast(supply);
      this.router.navigate(['at/' + next.params.section]);
    }
    return true;

  }
  showErrorToast(supply: string): any {
      if (supply !== 'no_toast_msg')
        this.toastingService.error('Authentication Error', 'The Id you have provided is not in the right format');
  }

  constructor(private toastingService: ToastingService, private router: Router) {

  }
}
