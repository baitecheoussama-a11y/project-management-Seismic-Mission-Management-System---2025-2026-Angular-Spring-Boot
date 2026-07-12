import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'filter' })
export class FilterPipe implements PipeTransform {
  transform(items: any[], searchText: string, property: string): any[] {
    if (!items) return [];
    if (!searchText || !searchText.trim()) return items;
    searchText = searchText.toLowerCase();
    return items.filter(item => item[property]?.toLowerCase().includes(searchText));
  }
}