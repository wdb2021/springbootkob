<template>
    <div class="matchground">
        <div class="row">
            <div class="col-6">
                <div class="user-avatar">
                    <img :src="$store.state.user.avatar" :alt="$store.state.user.username">
                </div>
                <div class="user-username">
                    {{ $store.state.user.username }}
                </div>
            </div>
            <div class="col-6">
                <!-- <div class="user-avatar">
                    <img :src="$store.state.pk.opponent_avatar" alt="">
                </div>
                <div class="user-username">
                    {{ $sotre.state.pk.opponent_avatar }}
                </div> -->
                <div class="user-avatar">
                    <img :src="$store.state.pk.opponent_avatar" alt="Opponent">
                </div>
                <div class="user-username">
                    {{ $store.state.pk.opponent_username }}
                </div>
            </div>
            <div class="col-12" style="text-align: center; padding-top: 10vh">
                <button type="button" class="btn btn-success btn-lg" @click="click_match_btn">
                    {{ match_btn_info }}</button>
            </div>
        </div>
    </div>
</template>

<script>
import { ref } from 'vue'
import { useStore } from 'vuex'

export default {
    setup() {
        const store = useStore();

        let match_btn_info = ref("Match");

        const click_match_btn = () => {
            if (match_btn_info.value === "Match") {
                store.state.pk.socket.send(JSON.stringify({
                    event: "start-matching",
                }));
                match_btn_info.value = "Cancel";
            } else {
                store.state.pk.socket.send(JSON.stringify({
                    event: "stop-matching",
                }));
                match_btn_info.value = "Match";
            }
        }

        return {
            match_btn_info,
            click_match_btn,
        }
    }
}
</script>

<style scoped> div.matchground {
     width: 90vw;
     height: 80vh;
     margin: 30px auto;
     background-color: rgb(50, 50, 50, 0.5);
 }

 div.user-avatar {
     width: 500px;
     align-items: center;
     display: flex;
     padding-top: 10vh;
 }

 div.user-avatar>img {
     border-radius: 50%;
     width: 30vh;
     margin-left: 15%;
 }

 div.user-username {
     text-align: center;
     font-size: 24px;
     font-weight: 600;
     color: white;
     padding-top: 2vh;
 }
</style>

