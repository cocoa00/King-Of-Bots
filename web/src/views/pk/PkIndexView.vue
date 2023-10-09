<template>
    <PlayGround v-if="$store.state.pk.status === 'playing'" />
    <MatchGround v-if="$store.state.pk.status === 'matching'" />
    <ResultBoard v-if="$store.state.pk.loser !== 'none'" />
    <div class="user-color" v-if="$store.state.pk.status === 'playing' && $store.state.user.id == $store.state.pk.a_id">你在左下角</div>
    <div class="user-color" v-if="$store.state.pk.status === 'playing' && $store.state.user.id == $store.state.pk.b_id">你在右上角</div>
</template>
 
<script>
import PlayGround from '@/components/PlayGround.vue'
import MatchGround from '@/components/MatchGround.vue'
import ResultBoard from '@/components/ResultBoard.vue'
import { onMounted, onUnmounted } from 'vue';
import { useStore } from 'vuex';
export default {
    components: {
        PlayGround,
        MatchGround,
        ResultBoard,
    },
    setup(){
        const store = useStore();
        const socketUrl = `ws://127.0.0.1:3000/websocket/${store.state.user.token}/`;
        let socket = null;
        store.commit("updateLoser", "none");
        store.commit("updateIsRecord", false);
        onMounted(() => {
            store.commit("updateOpponent", {
                username: "我的对手",
                photo: "https://cdn.acwing.com/media/article/image/2022/08/09/1_1db2488f17-anonymous.png",
            })
            socket = new WebSocket(socketUrl);

            socket.onopen = () => {
                console.log("连接成功");
                store.commit("updateSocket", socket);
            };

            socket.onmessage = msg => {
                const data = JSON.parse(msg.data);
                if (data.event === "start-matching"){
                    store.commit("updateOpponent", {
                        username: data.opponent_username,    
                        photo: data.opponent_photo,
                    }); 
                    setTimeout(() => {
                        store.commit("updateStatus", "playing");
                    }, 200);
                    store.commit("updateGame", data.game);
                } else if (data.event === "move"){
                    // console.log(data);
                    const game = store.state.pk.gameObject;
                    console.log("game: "+ game)
                    const [snake0, snake1] = game.snakes;
                    snake0.set_direction(data.a_direction);
                    snake1.set_direction(data.b_direction);
                } else if (data.event === "result"){
                    // console.log(data);
                    const game = store.state.pk.gameObject;
                    console.log(game)
                    const [snake0, snake1] = game.snakes;

                    if(data.loser === "all" || data.loser === "A"){
                        snake0.status = "die";
                    }
                    if(data.loser === "all" || data.loser === "B"){
                        snake1.status = "die";
                    }
                    store.commit("updateLoser", data.loser);
                }
            };

            socket.onclose = () => {
                console.log("连接关闭");
                store.commit("updateStatus", "matching");
            };

        })

        onUnmounted(() => {
            socket.close();
        })
    },
}
</script>

<style scope>
div.user-color{
    text-align: center;
    font-size: 4vh;
    color: white;
    font-weight: bold;
    text-shadow: 0 0 10px black;
}
</style>