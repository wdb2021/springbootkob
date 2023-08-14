<template>
    <PlayGround v-if="$store.state.pk.status === 'playing'" />
    <MatchGround v-if="$store.state.pk.status === 'matching'" />
</template>

<script>
import PlayGround from '../../components/PlayGround.vue'
import MatchGround from '../../components/MatchGround.vue'
import { onMounted, onUnmounted } from 'vue';  //组件被挂载的函数
import { useStore } from 'vuex'

export default {
    components: {
        PlayGround,
        MatchGround
    },
    setup() {
        const store = useStore();
        const socketUrl = `ws://127.00.0.1:3000/websocket/${store.state.user.token}/`;

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
                    setTimeout(() => {
                        store.commit("updateStatus", "playing");
                    }, 5000);
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

