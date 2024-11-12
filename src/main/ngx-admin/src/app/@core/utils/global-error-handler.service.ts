import {ErrorHandler, Injectable} from '@angular/core';
import {ToastingService} from './toaster.service';

@Injectable()
export class GlobalErrorHandlerService implements ErrorHandler {
    constructor(private toast: ToastingService) {
    }

    handleError(error: any): void {
        window['gtag']('event', 'exception', {
            'description': error.message,
        });
        console.log('error', error);
        // throw error;
    }


}
