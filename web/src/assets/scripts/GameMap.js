import { LQGameObject } from "./LQGameObject";

export class GameMap extends LQGameObject {
    constructor(ctx, parent) {
        super();

        this.ctx = ctx;
        this.parent = parent;
        this.L = 0;
    }

    start() {

    }

    update() {
        this.render();
    }

    render() {

    }
}

