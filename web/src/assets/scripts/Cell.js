export class Cell {
    constructor(r, c) {
        this.r = r;
        this.c = c;
        this.x = c + 0.5;  //画布 横纵坐标相反，中间位置取0.5
        this.y = r + 0.5;
    }


}