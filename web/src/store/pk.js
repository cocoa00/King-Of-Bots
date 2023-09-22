
export default ({
    state: {
        status: "matching",     // matching匹配中，playing游戏中
        socket: null,
        opponent_username: "",
        opponent_photo: "",
        gamemap:null, 
        a_id: 0,
        a_sx: 0, 
        a_sy: 0,
        b_id: 0,
        b_sx: 0,
        b_sy: 0, 
        gameObject: null,
    },
    getters: {
    },
    mutations: {
        updateSocket(state, socket) {
            state.socket = socket
        },
        updateOpponent(state, opponent) {
            state.opponent_username = opponent.username
            state.opponent_photo = opponent.photo
        },
        updateStatus(state, status) {
            state.status = status
        },
        updateGame(state, gamemap) {
            state.gamemap = gamemap.map;
            state.a_id = gamemap.a_id;
            state.a_sx = gamemap.a_sx;
            state.a_sy = gamemap.a_sy;
            state.b_id = gamemap.b_id;
            state.b_sx = gamemap.b_sx;
            state.b_sy = gamemap.b_sy;
        },
        updateGameObject(state, gameObject) {
            state.gameObject = gameObject;
        }
    },
    actions: {
    },
    modules: {
    }
})
