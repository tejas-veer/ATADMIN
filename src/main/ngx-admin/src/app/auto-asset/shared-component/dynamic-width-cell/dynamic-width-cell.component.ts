import { Component, Input, OnInit } from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';

@Component({
  selector: 'dynamic-width-cell',
  templateUrl: './dynamic-width-cell.component.html',
  styleUrls: ['./dynamic-width-cell.component.css']
})
export class DynamicWidthCellComponent implements OnInit {
  @Input() value: any;
  @Input() rowData: any;
  
  width: string;
  fullContent: string = '';
  truncatedContent: string = '';
  isExpanded: boolean = false;
  maxLength: number = 400;
  sanitizedContent: SafeHtml = '';

  constructor(private sanitizer: DomSanitizer) { }

  ngOnInit(): void {
    this.width = this.value['width'] || '300px';
    this.fullContent = this.value['value'] || '';
    this.truncatedContent = this.fullContent.substring(0, this.maxLength);
    this.sanitizedContent = this.sanitizer.bypassSecurityTrustHtml(this.truncatedContent);
  }

  toggleContent() {
    this.isExpanded = !this.isExpanded;
    this.sanitizedContent = this.sanitizer.bypassSecurityTrustHtml(
      this.isExpanded ? this.fullContent : this.truncatedContent
    );
  }
}
