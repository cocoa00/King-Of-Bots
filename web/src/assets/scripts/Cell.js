export class Cell {
    constructor (r, c) {
        this.c = c;
        this.r = r;
        this.x = c + 0.5;
        this.y = r + 0.5;
    }
}