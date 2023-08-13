<template>
    <div class="container">
        <div class="row">
            <div class="col-3">
                <div class="card" style="margin-top: 20px;">
                    <div class="card-body">
                        <img :src="$store.state.user.avatar" alt="可莉头像" style="width: 100%">
                    </div>
                </div>
            </div>
            <div class="col-9">
                <div class="card" style="margin-top: 20px;">
                    <div class="card-header">
                        <span style="font-size:130%" justify-content-md-center> My Bot </span>
                    </div>
                    <!-- Button trigger modal -->
                    <button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#add-bot-btn">
                        Create Bots
                    </button>

                    <!-- Modal -->
                    <div class="modal fade" id="add-bot-btn" tabindex="-1" aria-labelledby="exampleModalLabel"
                        aria-hidden="true">
                        <div class="modal-dialog modal-xl">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <h1 class="modal-title fs-5" id="exampleModalLabel">Create Bots</h1>
                                    <button type="button" class="btn-close" data-bs-dismiss="modal"
                                        aria-label="Close"></button>
                                </div>
                                <div class="modal-body">
                                    <div class="mb-3">
                                        <label for="add-bot-title" class="form-label">Title</label>
                                        <input v-model="botadd.title" type="text" class="form-control" id="add-bot-title"
                                            placeholder="please enter your bot's name here.">
                                    </div>
                                    <div class="mb-3">
                                        <label for="add-bot-code" class="form-label">Code</label>
                                        <input v-model="botadd.content" type="text" class="form-control" id="add-bot-code"
                                            placeholder="please code your bot here.">
                                    </div>
                                    <div class="mb-3">
                                        <label for="add-bot-des" class="form-label">Description</label>
                                        <textarea v-model="botadd.description" class="form-control" id="add-bot-des"
                                            rows="3" placeholder="Please enter bot's description here."></textarea>
                                    </div>
                                </div>
                                <div class="modal-footer">
                                    <div class="error-message">{{ botadd.error_message }}</div>
                                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                                    <button type="button" class="btn btn-primary" @click="add_bot">Create</button>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="card-body">
                        <table class="table table-striped table-hover">
                            <thead>
                                <tr>
                                    <th> Name of Bot </th>
                                    <th> created time</th>
                                    <th> Operations</th>
                                </tr>
                            </thead>
                            <tr v-for="bot in bots" :key="bot.id">
                                <td> {{ bot.title }}</td>
                                <td> {{ bot.createtime }}</td>
                                <td>
                                    <button type="button" class="btn btn-secondary" style="margin-right: 10px;"
                                        data-bs-toggle="modal" :data-bs-target="'#update-bot-btn' + bot.id"
                                        @click="update_bot(bot)">Modify</button>
                                    <button type="button" class="btn btn-danger" @click="remove_bot(bot)">Delete</button>

                                    <div class="modal fade" :id="'update-bot-btn' + bot.id" tabindex="-1"
                                        aria-labelledby="exampleModalLabel" aria-hidden="true">
                                        <div class="modal-dialog modal-xl">
                                            <div class="modal-content">
                                                <div class="modal-header">
                                                    <h1 class="modal-title fs-5" id="exampleModalLabel">Create Bots</h1>
                                                    <button type="button" class="btn-close" data-bs-dismiss="modal"
                                                        aria-label="Close"></button>
                                                </div>
                                                <div class="modal-body">
                                                    <div class="mb-3">
                                                        <label for="add-bot-title" class="form-label">Title</label>
                                                        <input v-model="bot.title" type="text" class="form-control"
                                                            id="add-bot-title"
                                                            placeholder="please enter your bot's name here.">
                                                    </div>
                                                    <div class="mb-3">
                                                        <label for="add-bot-code" class="form-label">Code</label>
                                                        <input v-model="bot.content" type="text" class="form-control"
                                                            id="add-bot-code" placeholder="please code your bot here.">
                                                    </div>
                                                    <div class="mb-3">
                                                        <label for="add-bot-des" class="form-label">Description</label>
                                                        <textarea v-model="bot.description" class="form-control"
                                                            id="add-bot-des" rows="3"
                                                            placeholder="Please enter bot's description here."></textarea>
                                                    </div>
                                                </div>
                                                <div class="modal-footer">
                                                    <div class="error-message">{{ bot.error_message }}</div>
                                                    <button type="button" class="btn btn-secondary"
                                                        data-bs-dismiss="modal">Close</button>
                                                    <button type="button" class="btn btn-primary"
                                                        @click="update_bot(bot)">Save</button>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </td>
                            </tr>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>

import { ref, reactive } from 'vue'
import $ from 'jquery'
import { useStore } from 'vuex'
import { Modal } from 'bootstrap/dist/js/bootstrap'

// import ContentField from '../../../components/ContentField.vue'
// import $ from 'jquery'
// import { useStore } from 'vuex'

export default {
    // components: {
    //     ContentField
    // },
    setup() {
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
                url: "http://127.0.0.1:3000/user/bot/getlist/",
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp) {
                    bots.value = resp;
                },
                error(resp) {
                    console.log(resp);
                }
            })
        }

        refresh_bots();

        const add_bot = () => {
            botadd.error_message = "";
            $.ajax({
                url: "http://127.0.0.1:3000/user/bot/add/",
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
                        Modal.getInstance("#add-bot-btn").hide();
                        refresh_bots();
                    } else {
                        botadd.error_message = resp.error_message;
                    }
                },
                error(resp) {
                    console.log(resp);
                }
            })
        }

        const remove_bot = (bot) => {
            $.ajax({
                url: "http://127.0.0.1:3000/user/bot/remove/",
                type: "post",
                data: {
                    bot_id: bot.id,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp) {
                    if (resp.error_message === "success") {
                        refresh_bots();
                    } else {
                        botadd.error_message = resp.error_message;
                    }
                },
                error(resp) {
                    console.log(resp);
                }
            })
        }

        const update_bot = (bot) => {
            $.ajax({
                url: "http://127.0.0.1:3000/user/bot/update/",
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
                    console.log(resp);
                    if (resp.error_message === "success") {
                        Modal.getInstance('#update-bot-btn' + bot.id).hide();
                        refresh_bots();
                    } else {
                        botadd.error_message = resp.error_message;
                    }
                },
                error(resp) {
                    console.log(resp);
                }
            })
        }

        return {
            bots,
            botadd,
            add_bot,
            remove_bot,
            update_bot,
        }

        // $.ajax({
        //     url: "http://127.0.0.1:3000/user/bot/add/",
        //     type: "post",
        //     data: {
        //         title: "Title of Bot",
        //         description: "Des of Bot",
        //         content: "Code of Bot",
        //     },
        //     headers: {
        //         Authorization: "Bearer " + store.state.user.token,
        //     },
        //     success(resp) {
        //         console.log(resp);
        //     },
        //     error(resp) {
        //         console.log(resp);
        //     }
        // })


        // $.ajax({
        //     url: "http://127.0.0.1:3000/user/bot/remove/",
        //     type: "POST",
        //     data: {
        //         bot_id: 8,
        //     },
        //     headers: {
        //         Authorization: "Bearer " + store.state.user.token,
        //     },
        //     success(resp) {
        //         console.log(resp);
        //     },
        //     error(resp) {
        //         console.log(resp);
        //     }
        // })

        // $.ajax({
        //     url: "http://127.0.0.1:3000/user/bot/update/",
        //     type: "post",
        //     data: {
        //         bot_id: 10,
        //         title: "Title of Bot 6",
        //         description: "Des of Bot 6 ",
        //         content: "Code of Bot 6",
        //     },
        //     headers: {
        //         Authorization: "Bearer " + store.state.user.token,
        //     },
        //     success(resp) {
        //         console.log(resp);
        //     },
        //     error(resp) {
        //         console.log(resp);
        //     }
        // })


        // $.ajax({
        //     url: "http://127.0.0.1:3000/user/bot/getlist/",
        //     type: "get",
        //     headers: {
        //         Authorization: "Bearer " + store.state.user.token,
        //     },
        //     success(resp) {
        //         console.log(resp);
        //     },
        //     error(resp) {
        //         console.log(resp);
        //     }
        // })

    }
}

</script>


<style scoped>
.card-body {
    align-items: center;
    display: flex;
}

div.error-message {
    color: red;
}
</style>
