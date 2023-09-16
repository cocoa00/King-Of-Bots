<template>
    <ContentField>
        bots
    </ContentField>
</template>

<script>
import ContentField from '@/components/ContentField.vue'
import $ from 'jquery'
import { useStore } from 'vuex';
export default {
    components: {
        ContentField
    },
    setup(){
        // const store = useStore();
        // $.ajax({
        //     url: "http://127.0.0.1:3000/user/bot/add/",
        //     type: "post",
        //     data: {
        //         title: "bot的标题",
        //         description: "bot描述",
        //         content: "bot代码",
        //     },
        //     headers: {
        //         Authorization: "Bearer " + store.state.user.token,
        //     },
        //     success(resp){
        //         console.log(resp);
        //     },
        //     error(resp){
        //         console.log(resp);
        //     }
        // })

        // const store = useStore();
        // $.ajax({
        //     url: "http://127.0.0.1:3000/user/bot/remove/",
        //     type: "post",
        //     data: {
        //         bot_id: "3",
        //     },
        //     headers: {
        //         Authorization: "Bearer " + store.state.user.token,
        //     },
        //     success(resp){
        //         console.log(resp);
        //     },
        //     error(resp){
        //         console.log(resp);
        //     }
        // })


        // const store = useStore();
        // $.ajax({
        //     url: "http://127.0.0.1:3000/user/bot/update/",
        //     type: "post",
        //     data: {
        //         bot_id: "5",
        //         title: "bot的标题111",
        //         description: "bot描述111",
        //         content: "bot代码111",
        //     },
        //     headers: {
        //         Authorization: "Bearer " + store.state.user.token,
        //     },
        //     success(resp){
        //         console.log(resp);
        //     },
        //     error(resp){
        //         console.log(resp);
        //     }
        // })

        const store = useStore();
        $.ajax({
            url: "http://127.0.0.1:3000/user/bot/getlist/",
            type: "get",
            headers: {
                Authorization: "Bearer " + store.state.user.token,
            },
            success(resp){
                console.log(resp);
            },
            error(resp){
                console.log(resp);
            }
        })
    }
}
</script>

<style scope>

</style>