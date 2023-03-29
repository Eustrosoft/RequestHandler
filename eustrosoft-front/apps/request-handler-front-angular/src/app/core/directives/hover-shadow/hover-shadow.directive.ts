import { Directive, ElementRef, HostListener } from '@angular/core';

@Directive({
  selector: '[hoverShadow]',
})
export class HoverShadowDirective {
  constructor(private elementRef: ElementRef) {}

  private classes = this.elementRef.nativeElement.classList as DOMTokenList;
  private shadowClasses = ['shadow-lg', 'p-3', 'bg-body', 'rounded'];

  @HostListener('mouseenter') onMouseEnter() {
    this.applyShadow();
  }

  @HostListener('mouseleave') onMouseLeave() {
    this.removeShadow();
  }

  private applyShadow(): void {
    this.classes.add(...this.shadowClasses);
  }

  private removeShadow(): void {
    this.classes.remove(...this.shadowClasses);
  }
}