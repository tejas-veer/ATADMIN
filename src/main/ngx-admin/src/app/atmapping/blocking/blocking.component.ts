import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { BlockingtableComponent } from './components/blockingtable/blockingtable.component';
import { ActivatedRoute } from '@angular/router';
import { ConfirmModalComponent } from './components/confirm-modal/confirm-modal.component';
import { ToastingService } from '../../@core/utils/toaster.service';
import { BlockingInputComponent } from './components/blocking-input/blocking-input.component';

@Component({
  selector: 'at-blocking',
  templateUrl: './blocking.component.html',
  styleUrls: ['./blocking.component.scss']
})
export class BlockingComponent implements OnInit, AfterViewInit {

  @ViewChild('templateBlocking') templateBlocking: BlockingtableComponent;
  @ViewChild('frameworkBlocking') frameworkBlocking: BlockingtableComponent;
  @ViewChild('blockingInput') blockingInput: BlockingInputComponent;
  @ViewChild('deleteModal') confirmModal: ConfirmModalComponent;

  supply: string;
  supplyId: string;
  demand: string;
  demandId: string;

  constructor(private route: ActivatedRoute, private toastService: ToastingService) {
    this.route.params.subscribe(params => {
      this.supply = params.supply ? params.supply.toUpperCase() : "";
      this.supplyId = params.supplyId;
      this.demand = params.demand ? params.demand.toUpperCase() : "";
      this.demandId = params.demandId;
    });
  }

  ngAfterViewInit(): void {
    this.blockingInput.ruleInserts.subscribe(data => {
      if (data.updates.filter(item => item.type === 'TEMPLATE').length) {
        this.templateBlocking.refresh();
      }

      if (data.updates.filter(item => item.type === 'FRAMEWORK').length) {
        this.frameworkBlocking.refresh();
      }

    })
  }

  ngOnInit(): void {

  }

  confirm(): void {
    let changedTemplates = true, changedFrameworks = true;
    Promise.all([this.templateBlocking.getChangedData(), this.frameworkBlocking.getChangedData()]).then(data => {
      let typeCreativeMap = {};

      changedTemplates = data[0].length > 0;
      changedFrameworks = data[1].length > 0;

      data = data[0].concat(data[1]);

      if (!data.length) {
        this.toastService.warning('Nothing to Delete', 'You have not selected any entries to Delete');
        return;
      }

      data.forEach(item => {
        const size = item['Template Size'];
        const creative = item.creative;

        if (!typeCreativeMap[size]) {
          typeCreativeMap[size] = {};
        }

        if (!typeCreativeMap[size][creative]) {
          typeCreativeMap[size][creative] = {};
        }

        typeCreativeMap[size][creative][item.type] = true;
      });

      this.confirmModal.supplyId = this.supplyId;
      this.confirmModal.supply = this.supply;
      this.confirmModal.demand = this.demand;
      this.confirmModal.demandId = this.demandId;
      this.confirmModal.displayList = typeCreativeMap;
      this.confirmModal.status = 'W';
      this.confirmModal.open().result.then(message => {
        console.log(message);

        if (changedTemplates)
          this.templateBlocking.refresh();

        if (changedFrameworks)
          this.frameworkBlocking.refresh();

      });

    });

  }
}
