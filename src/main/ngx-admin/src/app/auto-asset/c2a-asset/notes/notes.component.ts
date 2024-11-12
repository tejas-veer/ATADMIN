import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'notes',
  templateUrl: './notes.component.html',
  styleUrls: ['./notes.component.css', '../../shared-style.css']
})
export class NotesComponent implements OnInit {
  isNotesCollapsed: boolean;

  constructor() { }

  ngOnInit(): void {
  }

  toggleNotesCollapse() {
    this.isNotesCollapsed = !this.isNotesCollapsed;
  }

}
