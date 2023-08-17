const LQ_GAME_OBJECTS = [];

export class LQGameObject {
    constructor() {
        LQ_GAME_OBJECTS.push(this);
        this.timedelta = 0;
        this.has_called_start = false;
    }

    start() {
    }

    update() {
    }

    on_destroy() {
    }

    destory() {
        this.on_destory();

        for (let i in LQ_GAME_OBJECTS) {
            const obj = LQ_GAME_OBJECTS[i];
            if (obj === this) {
                LQ_GAME_OBJECTS.splice(i);
                break;
            }
        }
    }
}

let last_timestamp;
const step = timestamp => {
    for (let obj of LQ_GAME_OBJECTS) {
        if (!obj.has_called_start) {
            obj.has_called_start = true;
            obj.start();
        } else {
            obj.timedelta = timestamp - last_timestamp;
            obj.update();
        }
    }

    last_timestamp = timestamp;
    requestAnimationFrame(step);
}

requestAnimationFrame(step)

