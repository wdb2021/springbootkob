import { LQGameObject } from "./LQGameObject";
import { Cell } from "./Cell";

export class Snake extends LQGameObject {
    constructor(info, gamemap) {
        super();

        this.id = info.id;
        this.color = info.color;
        this.gamemap = gamemap;

        //存放身体，cells[0]存放蛇头
        this.cells = [new Cell(info.r, info.c)];
        this.next_cell = null; //下一步的目标位置

        this.speed = 5;  //每秒走5个格子
        this.direction = -1; //-1表示没有指令， 0123上右下左 
        this.status = "idle";  //idle静止，move正在移动，die死亡

        this.dr = [-1, 0, 1, 0];  //4个方向行的偏移量
        this.dc = [0, 1, 0, -1];  //4个方向列的偏移量

        this.step = 0; //当前回合数
        this.eps = 1e-2;

        this.eye_direction = 0;
        if (this.id === 1) this.eye_direction = 2; //左下角蛇的方向初始向上

        this.eye_dx = [   //蛇眼睛不同方向偏移量x
            [-1, 1], //上
            [1, 1], //右
            [1, -1], //下
            [-1, -1], //左
        ];

        this.eye_dy = [   //蛇眼睛不同方向偏移量y
            [-1, -1],
            [-1, 1],
            [1, 1],
            [-1, 1],
        ]
    }

    start() {

    }

    set_direction(d) { //统一输入接口
        this.direction = d;
    }

    check_tail_increasing() { //检测当前回合长度是否增加
        if (this.step <= 10) return true;
        if (this.setp % 3 === 1) return true;
        return false;
    }

    next_step() {
        const d = this.direction;
        this.next_cell = new Cell(this.cells[0].r + this.dr[d], this.cells[0].c + this.dc[d]);
        this.direction = -1;  //清空操作
        this.eye_direction = d;
        this.status = "move";
        this.step++;

        const k = this.cells.length;
        for (let i = k; i > 0; i--) { //每一位向前，相当于有两个头节点
            this.cells[i] = JSON.parse(JSON.stringify(this.cells[i - 1]));
        }

        // if (!this.gamemap.check_valid(this.next_cell)) { //下一步撞了的话瞬间去世
        //     this.status = "die";
        // }
    }

    update_move() {
        const move_distance = this.speed * this.timedelta / 1000; //每两帧间走的距离
        const dx = this.next_cell.x - this.cells[0].x;
        const dy = this.next_cell.y - this.cells[0].y;
        const distance = Math.sqrt(dx * dx + dy * dy);

        if (distance < this.eps) { //走到目标点
            this.cells[0] = this.next_cell; //添加一个新头部
            this.next_cell = null;
            this.status = "idle";  //走完停下来
            if (!this.check_tail_increasing()) { //蛇不变长，pop掉最后一个
                this.cells.pop();
            }
        } else {
            this.cells[0].x += move_distance * dx / distance;
            this.cells[0].y += move_distance * dy / distance;

            if (!this.check_tail_increasing()) {
                const k = this.cells.length;
                const tail = this.cells[k - 1], tail_target = this.cells[k - 2];
                const tail_dx = tail_target.x - tail.x;
                const tail_dy = tail_target.y - tail.y;
                tail.x += move_distance * tail_dx / distance;
                tail.y += move_distance * tail_dy / distance;
            }
        }
    }

    update() {
        if (this.status === 'move') {
            this.update_move();
        }
        this.render();
    }

    render() {
        const L = this.gamemap.L;
        const ctx = this.gamemap.ctx;

        ctx.fillStyle = this.color;
        if (this.status === "die") {
            ctx.fillStyle = "white";
        }

        for (const cell of this.cells) //of 遍历值 in遍历下标
        {
            ctx.beginPath();
            //(端点1，端点2，圆的半径, 起始角度，终止角度)
            ctx.arc(cell.x * L, cell.y * L, L / 2 * 0.8, 0, Math.PI * 2);
            ctx.fill();
        }

        for (let i = 1; i < this.cells.length; i++) {
            const a = this.cells[i - 1], b = this.cells[i];
            if (Math.abs(a.x - b.x) < this.eps && Math.abs(a.y - b.y) < this.eps)
                continue;
            if (Math.abs(a.x - b.x) < this.eps) { //fillRect(x, y, 长, 宽) 
                ctx.fillRect((a.x - 0.4) * L, Math.min(a.y, b.y) * L, L * 0.8, Math.abs(a.y - b.y) * L);
            } else {
                ctx.fillRect(Math.min(a.x, b.x) * L, (a.y - 0.4) * L, Math.abs(a.x - b.x) * L, L * 0.8);
            }
        }

        ctx.fillStyle = "black";
        for (let i = 0; i < 2; i++) {
            const eye_x = (this.cells[0].x + this.eye_dx[this.eye_direction][i] * 0.2) * L;
            const eye_y = (this.cells[0].y + this.eye_dy[this.eye_direction][i] * 0.2) * L;
            ctx.beginPath();
            ctx.arc(eye_x, eye_y, L * 0.05, 0, Math.PI * 2);
            ctx.fill();
        }
    }

}