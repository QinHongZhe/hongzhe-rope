<template>
    <basic-container class='processManagement'>
        <avue-crud :data="tableData" :option="option" v-model="obj"
                   :table-loading="loading"
                   :before-close="beforeOpen"
                   @search-change="search"
                   @refresh-change="refresh" >
            <template slot-scope="scope" slot="menu">
                <el-button type="primary"
                           size="small"
                           plain
                           v-if="showStartBtn(scope.row)"
                           @click="start(scope.row)">启动</el-button>
                <el-button type="primary"
                           size="small"
                           plain
                           v-if="scope.row.state === 'RUNNING'"
                           @click="stop(scope.row)">停止</el-button>
                <el-button type="primary"
                           size="small"
                           plain
                           @click="remove(scope.row)">删除</el-button>
            </template>
        </avue-crud>
    </basic-container>
</template>

<script>
    import { getProcess, startProcess, stopProcess, removeProcess } from '@/api/process/processManage';
    import { requestCall, requestThrown } from '@/util/messageUtils';

    export default {
        name: "index",
        data() {
            return {
                obj:{},
                loading: true,
                sourceData: [],
                tableData: [],
            }
        },
        created(){
            this.getData();
        },
        computed: {
            option() {
                return {
                    title: '流程管理',
                    page: false,
                    align: 'center',
                    menuAlign: 'center',
                    addBtn: false,
                    columnBtn: false,
                    delBtn: false,
                    editBtn: false,
                    column:[
                        {
                            label: '流程id',
                            prop: 'processId',
                            search: true,
                        },
                        {
                            label: '流程状态',
                            prop: 'state'
                        }
                    ]
                }
            }
        },
        methods: {
            showStartBtn(row){
                const state = row.state;
                if(state === 'NEW' || state === 'STOP' || state === 'FAILED'){
                    return true;
                } else {
                    return false;
                }
            },
            beforeOpen(done){
                done();
            },
            refresh(){
                this.getData();
            },
            search(params, done){
                let processId = params.processId;
                this.tableData = this.sourceData.filter( item => (~item.processId.indexOf(processId)));
                done();
            },
            start(row){
                this.loading = true;
                const processId = row.processId;
                startProcess(processId)
                    .then(res => {
                        requestCall(res, this, `启动流程 ${processId}`);
                        this.getData();
                    })
                    .catch(error => {
                        requestThrown(this, error);
                        this.getData();
                    });
            },
            stop(row){
                this.loading = true;
                const processId = row.processId;
                stopProcess(processId).then(res => {
                        requestCall(res, this, `停止流程 ${processId}`);
                        this.getData();
                    })
                    .catch(error => {
                        requestThrown(this, error);
                        this.getData();
                    });
            },
            remove(row){
                this.loading = true;
                const processId = row.processId;
                removeProcess(processId).then(res => {
                    requestCall(res, this, `删除流程 ${processId}`);
                    this.getData();
                })
                    .catch(error => {
                        requestThrown(this, error);
                        this.getData();
                    });
            },
            getData(){
                this.loading = true;
                getProcess()
                    .then(res => {
                        const data = res.data.data;
                        this.sourceData = data;
                        this.tableData = data;
                        this.loading = false;
                    });
            }
        }
    }
</script>
<style lang="scss">
@import '~@/styles/process/processManagement.scss';
</style>
