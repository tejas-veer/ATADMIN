import { Injectable } from '@angular/core';
import {
  BodyOutputType, ToasterConfig, IToasterConfig, ToasterService, Toast, ToastType, DefaultTypeClasses, DefaultIconClasses
} from 'angular2-toaster';


import 'style-loader!angular2-toaster/toaster.css';


export const ToastConfig: ToasterConfig = new ToasterConfig({
  positionClass: 'toast-top-right',
  timeout: 5000,
  newestOnTop: true,
  tapToDismiss: true,
  preventDuplicates: false,
  animation: 'flyRight',
  limit: 2,
});

@Injectable({
  providedIn: 'root'
})
export class ToastingService {
    config: ToasterConfig;

  constructor(private toasterService: ToasterService) {
    this.config = ToastConfig;
  }

  getToastConfig() {
    return this.config;
  }

  setToasterService(ts) {
    this.toasterService = ts;
  }

  showToast(type: string, title: string, body: string) {
    const toast: Toast = {
      type: type as ToastType,
      title: title,
      body: body,
      timeout: 8000,
      showCloseButton: true,
      bodyOutputType: BodyOutputType.TrustedHtml,
    };
    this.toasterService.popAsync(toast);
  }

  error(title: string, body: string) {
    const toast: Toast = {
      type: 'error' as ToastType ,
      title: title,
      body: body,
      timeout: -1,
      showCloseButton: true,
      bodyOutputType: BodyOutputType.TrustedHtml,
    };
    this.toasterService.popAsync(toast);
  }

  clearToaster(){
    this.toasterService.clear();
  }

  def(title: string, body: string) {
    const toast: Toast = {
      type: 'default' as ToastType,
      title: title,
      body: body,
      timeout: 8000,
      showCloseButton: true,
      bodyOutputType: BodyOutputType.TrustedHtml,
    };
    this.toasterService.popAsync(toast);
  }

  info(title: string, body: string) {
    const toast: Toast = {
      type: 'info' as ToastType,
      title: title,
      body: body,
      timeout: 8000,
      showCloseButton: true,
      bodyOutputType: BodyOutputType.TrustedHtml,
    };
    this.toasterService.popAsync(toast);
  }

  warning(title: string, body: string) {
    const toast: Toast = {
      type: 'warning' as ToastType,
      title: title,
      body: body,
      timeout: 8000,
      showCloseButton: true,
      bodyOutputType: BodyOutputType.TrustedHtml,
    };
    this.toasterService.popAsync(toast);
  }

  success(title: string, body: string) {
    const toast: Toast = {
      type: 'success' as ToastType,
      title: title,
      body: body,
      timeout: -1,
      showCloseButton: true,
      bodyOutputType: BodyOutputType.TrustedHtml,
    };
    this.toasterService.popAsync(toast);
  }
}
