

export default {
    state: {
        status: "matching",   //router, 匹配切换界面
        socket: null,
        opponent_username: "",
        opponent_avatar: "",
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
        }
    },
    actions: {

    },
    modules: {

    }
}