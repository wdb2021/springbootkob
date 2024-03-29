import { LQGameObject } from "./LQGameObject";
import { Snake } from "./Snake";
import { Wall } from "./Wall";

export class GameMap extends LQGameObject {
    constructor(ctx, parent, store) {
        super();

        this.ctx = ctx;
        this.parent = parent;
        this.store = store;
        this.L = 0;

        this.rows = 17;
        this.cols = 18;

        this.inner_walls_count = 200;
        this.walls = [];

        this.snakes = [
            new Snake({ id: 0, color: "#4876EC", r: this.rows - 2, c: 1 }, this),
            new Snake({ id: 1, color: "#F94848", r: 1, c: this.cols - 2 }, this),
        ];

    }

    // check_connectivity(g, sx, sy, tx, ty) {
    //     if (sx == tx && sy == ty) return true;
    //     g[sx][sy] = true;

    //     let dx = [-1, 0, 1, 0], dy = [0, 1, 0, -1];
    //     for (let i = 0; i < 4; i++) {
    //         let x = sx + dx[i], y = sy + dy[i];
    //         if (!g[x][y] && this.check_connectivity(g, x, y, tx, ty))
    //             return true;
    //     }
    //     return false;
    // }

    create_walls() {
        // const g = [];
        // for (let r = 0; r < this.rows; r++) {
        //     g[r] = [];
        //     for (let c = 0; c < this.cols; c++)
        //         g[r][c] = false;
        // }

        // //四周加墙
        // for (let i = 0; i < this.rows; i++) g[i][0] = g[i][this.cols - 1] = true;
        // for (let i = 0; i < this.cols; i++) g[0][i] = g[this.rows - 1][i] = true;

        // //创建随机障碍物
        // for (let i = 0; i < 100; i++) {
        //     let t = parseInt(Math.random() * 10);
        //     if (t && t < 3) {
        //         this.inner_walls_count /= t;
        //         break;
        //     }
        // }
        // for (let i = 0; i < this.inner_walls_count; i++) {
        //     for (let j = 0; j < 1000; j++) {
        //         let r = parseInt(Math.random() * this.rows);
        //         let c = parseInt(Math.random() * this.cols);
        //         if (g[r][c] || g[this.rows - 1 - r][this.cols - 1 - c]) continue;
        //         if (r == this.rows - 2 && c == 1 || r == 1 && c == this.cols - 2) continue;

        //         g[r][c] = g[this.rows - 1 - r][this.cols - 1 - c] = true;
        //         break;
        //     }
        // }

        // //深拷贝,复制再生成，防止对之前的产生影响
        // const copy_g = JSON.parse(JSON.stringify(g));
        // if (!this.check_connectivity(copy_g, this.rows - 2, 1, 1, this.cols - 2)) return false;

        /*         利用后端传参         */
        const g = this.store.state.pk.gamemap;


        for (let r = 0; r < this.rows; r++)
            for (let c = 0; c < this.cols; c++)
                if (g[r][c]) this.walls.push(new Wall(r, c, this));

        // return true;
    }

    add_listening_events() {
        if (this.store.state.record.is_record) {
            let k = 0;

            console.log(this.store.state.record);

            const a_steps = this.store.state.record.a_steps;
            const b_steps = this.store.state.record.b_steps;
            const loser = this.store.state.record.record_loser;
            const [snake0, snake1] = this.snakes;
            const interval_id = setInterval(() => {
                if (k >= a_steps.length - 1) {
                    if (loser === "all" || loser === "A") {
                        snake0.status = "die";
                    }
                    if (loser === "all" || loser === "B") {
                        snake1.status = "die";
                    }
                    clearInterval(interval_id);
                } else {
                    snake0.set_direction(parseInt(a_steps[k]));
                    snake1.set_direction(parseInt(b_steps[k]));
                }
                k++;
            }, 300);
        } else {

            this.ctx.canvas.focus();

            //const [snake0, snake1] = this.snakes;
            this.ctx.canvas.addEventListener("keydown", e => {
                let d = -1;
                if (e.key === 'w') d = 0;
                else if (e.key === 'd') d = 1;
                else if (e.key === 's') d = 2;
                else if (e.key === 'a') d = 3;

                if (d >= 0) {
                    this.store.state.pk.socket.send(JSON.stringify({
                        event: "move",
                        direction: d,
                    }));
                }

                // if (e.key === 'w') snake0.set_direction(0);
                // else if (e.key === 'd') snake0.set_direction(1);
                // else if (e.key === 's') snake0.set_direction(2);
                // else if (e.key === 'a') snake0.set_direction(3);
                // // else if (e.key === 'ArrowUp') snake1.set_direction(0);
                // else if (e.key === 'ArrowRight') snake1.set_direction(1);
                // else if (e.key === 'ArrowDown') snake1.set_direction(2);
                // else if (e.key === 'ArrowLeft') snake1.set_direction(3);
            });
        }
    }

    start() {
        // while (!this.create_walls());
        this.create_walls();
        this.add_listening_events();
    }

    update_size() {
        this.L = parseInt(Math.min(this.parent.clientWidth / this.cols, this.parent.clientHeight / this.rows));
        this.ctx.canvas.width = this.L * this.cols;
        this.ctx.canvas.height = this.L * this.rows;
    }

    check_ready() { //判断双方是否都准备好移动
        for (const snake of this.snakes) {
            if (snake.status !== "idle") return false;
            if (snake.direction === -1) return false;
        }
        return true;
    }

    next_step() { //让双方进入下一回合
        for (const snake of this.snakes) {
            snake.next_step();
        }
    }

    check_valid(cell) { //碰撞检测
        for (const wall of this.walls) {
            if (wall.r === cell.r && wall.c === cell.c)
                return false;
        }

        for (const snake of this.snakes) {
            let k = snake.cells.length;
            if (!snake.check_tail_increasing()) { //当蛇尾会前进，蛇尾不要判断
                k--;
            }
            for (let i = 0; i < k; i++) {
                if (snake.cells[i].r === cell.r && snake.cells[i].c === cell.c)
                    return false;
            }
        }
        return true;
    }

    update() {
        this.update_size();
        if (this.check_ready()) {
            this.next_step();
        }
        this.render();
    }

    render() {
        const color_even = "#AAD751", color_odd = "#A2D149";
        for (let r = 0; r < this.rows; r++)
            for (let c = 0; c < this.cols; c++) {
                if ((r + c) % 2 == 0)
                    this.ctx.fillStyle = color_even;
                else this.ctx.fillStyle = color_odd;
                this.ctx.fillRect(c * this.L, r * this.L, this.L, this.L)
            }
    }
}

