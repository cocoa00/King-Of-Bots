const AC_GAME_OBJECTS = [];

export class AcGameObject {
    constructor() {
        AC_GAME_OBJECTS.push(this);
        this.timedelta = 0;
        this.has_called_start = false;
    }

    start() {

    }
    update(){

    }

    on_destroy() {
    
    }
    destroy() {
        this.on_destroy();
        
        for (let i in AC_GAME_OBJECTS){
            const obj = AC_GAME_OBJECTS[i];
            if (obj === this){
                AC_GAME_OBJECTS.splice(i);
                break;
            }
        }
    }

}

let last_timestamp; //上一次执行的时刻
const step = timestamp => {
    for (let obj of AC_GAME_OBJECTS){
        if (!obj.has_called_start){
            obj.start();
            obj.has_called_start = true;
        }
        else{
            obj.timedelta = timestamp - last_timestamp;
            obj.update();
        }
    }
    last_timestamp = timestamp;
    requestAnimationFrame(step);
}

requestAnimationFrame(step);    //表示下一帧的时候执行step函数