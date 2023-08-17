

export default {
    state: {
        status: "matching",   //router, 匹配切换界面
        socket: null,
        opponent_username: "",
        opponent_avatar: "",
        gamemap: null,
    },
    getters: {

    },
    mutations: {
        updateSocket(state, socket) {
            state.socket = socket;
        },
        updateOpponent(state, opponent) {
            state.opponent_username = opponent.username;
            state.opponent_avatar = opponent.avatar;
        },
        updateStatus(state, status) {
            state.status = status;
        },
        updateGamemap(state, gamemap) {
            state.gamemap = gamemap;
        }
    },
    actions: {

    },
    modules: {

    }
}