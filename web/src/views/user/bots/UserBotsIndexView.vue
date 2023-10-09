<template>
    <div class="container">
        <div class="row">
            <div class="col-3">
                <div class="card" style="margin-top: 20px;">
                    <div class="card-body">
                        <img :src="$store.state.user.photo" alt="" style="width: 100%;">
                    </div>
                </div>
            </div>
            <div class="col-9">
                <div class="card" style="margin-top: 20px;">
                    <div class="card-header">
                        <span style="font-size: 130%;">我的bots</span>
                        <button type="button" class="btn btn-primary float-end" data-bs-toggle="modal"
                            data-bs-target="#bot-add">
                            增加bot
                        </button>
                    </div>
                    <!-- modal -->
                    <div class="modal fade" id="bot-add" tabindex="-1">
                        <div class="modal-dialog modal-xl">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <h1 class="modal-title fs-5" id="exampleModalLabel">创建Bot</h1>
                                    <button type="button" class="btn-close" data-bs-dismiss="modal"
                                        aria-label="Close"></button>
                                </div>
                                <div class="modal-body">
                                    <form>
                                        <div class="mb-3">
                                            <label for="add-bot-title" class="form-label">标题</label>
                                            <input v-model="botadd.title" type="text" class="form-control"
                                                id="add-bot-title">
                                        </div>
                                        <div class="mb-3">
                                            <label for="add-bot-description" class="form-label">描述</label>
                                            <textarea v-model="botadd.description" type="text" class="form-control" rows="3"
                                                id="add-bot-description">
                                            </textarea>
                                        </div>
                                        <div class="mb-3">
                                            <label for="add-bot-code" class="form-label">代码</label>
                                            <VAceEditor v-model:value="botadd.content" @init="editorInit" lang="c_cpp"
                                                theme="textmate" style="height: 300px" />
                                        </div>
                                    </form>
                                </div>
                                <div class="modal-footer">
                                    <div class="error-message">{{ botadd.error_message }}</div>
                                    <button type="button" class="btn btn-primary" @click="bot_add">创建</button>
                                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="card-body">
                        <table class="table table-hover">
                            <thead>
                                <tr>
                                    <th>bot名称</th>
                                    <th>创建时间</th>
                                    <th>操作</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr v-for="bot in bots" :key="bot.id">
                                    <td>{{ bot.title }}</td>
                                    <td>{{ bot.createtime }}</td>
                                    <td>
                                        <button type="button" class="btn btn-secondary" style="margin-right: 10px;"
                                            data-bs-toggle="modal" :data-bs-target="'#bot-update-' + bot.id">修改</button>
                                        <button type="button" class="btn btn-danger" @click="remove_bot(bot)">删除</button>
                                        <!-- modal -->
                                        <div class="modal fade" :id="'bot-update-' + bot.id" tabindex="-1">
                                            <div class="modal-dialog modal-xl">
                                                <div class="modal-content">
                                                    <div class="modal-header">
                                                        <h1 class="modal-title fs-5">更新Bot</h1>
                                                        <button type="button" class="btn-close" data-bs-dismiss="modal"
                                                            aria-label="Close"></button>
                                                    </div>
                                                    <div class="modal-body">
                                                        <form>
                                                            <div class="mb-3">
                                                                <label for="add-bot-title" class="form-label">标题</label>
                                                                <input v-model="bot.title" type="text" class="form-control"
                                                                    id="add-bot-title">
                                                            </div>
                                                            <div class="mb-3">
                                                                <label for="add-bot-description"
                                                                    class="form-label">描述</label>
                                                                <textarea v-model="bot.description" type="text"
                                                                    class="form-control" rows="3"
                                                                    id="add-bot-description" />
                                                            </div>
                                                            <div class="mb-3">
                                                                <label for="add-bot-code" class="form-label">代码</label>
                                                                <VAceEditor v-model:value="bot.content"
                                                                    @init="editorInit" lang="c_cpp" theme="textmate"
                                                                    style="height: 300px" />
                                                            </div>
                                                        </form>
                                                    </div>
                                                    <div class="modal-footer">
                                                        <div class="error-message">{{ bot.error_message }}</div>
                                                        <button type="button" class="btn btn-primary"
                                                            @click="update_bot(bot)">保存修改</button>
                                                        <button type="button" class="btn btn-secondary"
                                                            data-bs-dismiss="modal">取消</button>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import { ref, reactive } from 'vue';
import $ from 'jquery'
import { useStore } from 'vuex';
import { Modal } from 'bootstrap/dist/js/bootstrap';
import { VAceEditor } from 'vue3-ace-editor';
import ace from 'ace-builds';

export default {
    components: {
        VAceEditor,
    },
    setup() {
        ace.config.set(
            "basePath",
            "https://cdn.jsdelivr.net/npm/ace-builds@" + require('ace-builds').version + "/src-noconflict/")

        const store = useStore();
        let bots = ref([]);

        const botadd = reactive({
            title: "",
            description: "",
            content: "",
            error_message: "",
        });


        const refresh_bots = () => {
            $.ajax({
                url: "http://127.0.0.1:3000/api/user/bot/getlist/",
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp) {
                    bots.value = resp;
                },
            })
        }
        refresh_bots();

        const bot_add = () => {
            botadd.error_message = "";
            $.ajax({
                url: "http://127.0.0.1:3000/api/user/bot/add/",
                type: "post",
                data: {
                    title: botadd.title,
                    description: botadd.description,
                    content: botadd.content,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp) {
                    if (resp.error_message === "success") {
                        botadd.title = "";
                        botadd.description = "";
                        botadd.content = "";
                        Modal.getInstance("#bot-add").hide();
                        refresh_bots();
                    } else {
                        botadd.error_message = resp.error_message;
                    }
                },
            })
        }

        const remove_bot = (bot) => {
            $.ajax({
                url: "http://127.0.0.1:3000/api/user/bot/remove/",
                type: "post",
                data: {
                    bot_id: bot.id,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success() {
                    refresh_bots();
                },
            })
        }

        const update_bot = (bot) => {
            $.ajax({
                url: "http://127.0.0.1:3000/api/user/bot/update/",
                type: "post",
                data: {
                    bot_id: bot.id,
                    title: bot.title,
                    description: bot.description,
                    content: bot.content,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp) {
                    if (resp.error_message === "success") {
                        Modal.getInstance("#bot-update-" + bot.id).hide();
                        refresh_bots();
                    } else {
                        bot.error_message = resp.error_message;
                    }
                },
            })

        }

        return {
            bots,
            botadd,
            refresh_bots,
            bot_add,
            remove_bot,
            update_bot,
        }

    }
}
</script>

<style scope>
div.error-message {
    color: red;
}
</style>