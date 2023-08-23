<template>
    <PlayGround v-if="$store.state.pk.status === 'playing'" />
    <MatchGround v-if="$store.state.pk.status === 'matching'" />
    <ResultBoard v-if="$store.state.pk.loser !== 'none'" />
</template>

<script>
import PlayGround from '../../components/PlayGround.vue'
import MatchGround from '../../components/MatchGround.vue'
import ResultBoard from '../../components/ResultBoard.vue'
import { onMounted, onUnmounted } from 'vue';  //组件被挂载的函数
import { useStore } from 'vuex'

export default {
    components: {
        PlayGround,
        MatchGround,
        ResultBoard
    },
    setup() {
        const store = useStore();
        const socketUrl = `ws://127.00.0.1:3000/websocket/${store.state.user.token}/`;

        store.commit("updateIsRecord", false);

        let socket = null;
        onMounted(() => {  //挂载时执行
            store.commit("updateOpponent", {
                username: "My Opponent",
                avatar: "https:/cdn.acwing.com/media/article/image/2022/08/09/1_1db2488f17-anonymous.png",
            });
            socket = new WebSocket(socketUrl);

            socket.onopen = () => {
                store.commit("updateSocket", socket);
                console.log("connected!");
            }

            socket.onmessage = msg => {
                const data = JSON.parse(msg.data);
                if (data.event === "start-matching") {
                    store.commit("updateOpponent", {
                        username: data.opponent_username,
                        avatar: data.opponent_avatar,
                    });
                    console.log("success update upponent");
                    setTimeout(() => {
                        store.commit("updateStatus", "playing");
                        console.log(data);
                        console.log(store.state.pk.status);
                    }, 5000);
                    store.commit("updateGame", {
                        map: data.game.map,
                        a_id: data.game.a_id,
                        a_sx: data.game.a_sx,
                        a_sy: data.game.a_sy,
                        b_id: data.game.b_id,
                        b_sx: data.game.b_sx,
                        b_sy: data.game.b_sy,
                    });
                } else if (data.event === "move") {
                    console.log(data);
                    const game = store.state.pk.gameObject;
                    const [snake0, snake1] = game.snakes;
                    snake0.set_direction(data.a_direction);
                    snake1.set_direction(data.b_direction);
                } else if (data.event === "result") {
                    console.log(data);
                    const game = store.state.pk.gameObject;
                    const [snake0, snake1] = game.snakes;

                    if (data.loser === "all" || data.loser === "A") {
                        snake0.status = "die";
                    }
                    if (data.loser === "all" || data.loser === "B") {
                        snake1.status = "die";
                    }
                    store.commit("updateLoser", data.loser);
                }

                if (data.event === "test") {
                    console.log("successfully received");
                }
                console.log(data);
            }

            socket.onclose = () => {
                console.log("disconnected!");
            }
        });

        onUnmounted(() => {
            store.commit("updateStatus", "matching");
            socket.close();
        });

    }
}

</script>


<style scoped></style>

