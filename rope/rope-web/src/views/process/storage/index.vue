<template>
    <basic-container class='processManagement'>
        <avue-crud :data="processSourceData" :option="option" v-model="obj"
                   :table-loading="loading"
                   @search-change="search"
                   @refresh-change="refresh">
            <template slot-scope="scope" slot="storageIdSearch">
                <el-select v-model="selectStoreDataId">
                    <el-option
                            v-for="item in storeData"
                            :key="item.id"
                            :label="item.name"
                            :value="item.id">
                    </el-option>
                </el-select>
            </template>
            <template slot-scope="scope" slot="menu">
                <el-button type="primary"
                           size="small"
                           plain
                           @click="remove(scope.row)">删除</el-button>
                <el-button type="primary"
                           size="small"
                           plain
                           @click="download(scope.row)">导出</el-button>
            </template>
        </avue-crud>
    </basic-container>
</template>

<script>
    import { getProcessStorage, getProcessInfo, deleteProcess, downloadUrl } from '@/api/process/processStorage';
    import { requestCall, requestThrown } from '@/util/messageUtils';

    export default {
        name: "index",
        data() {
            return {
                obj:{},
                loading: true,
                selectStoreDataId: "",
                storeData: [],
                processSourceData: []
            }
        },
        created(){
            this.loading = true;
            getProcessStorage()
                .then(res => {
                    const data = res.data.data;
                    if(data && data.length > 0){
                        this.storeData = data;
                        this.selectStoreDataId = data[0].id;
                        this.getData();
                    }
                });
        },
        computed: {
            option() {
                return {
                    title: '流程存储',
                    page: false,
                    align: 'center',
                    menuAlign: 'center',
                    addBtn: false,
                    columnBtn: false,
                    delBtn: false,
                    editBtn: false,
                    column:[
                        {
                            label: '存储者',
                            prop: 'storageId',
                            searchslot: true,
                            search: true,
                            hide: true,
                        },
                        {
                            label: '流程id',
                            prop: 'processConfig',
                            formatter(row, value){
                                return value.processId
                            }
                        },
                        {
                            label: '状态',
                            prop: 'state'
                        }
                    ],
                    search: {
                        searchClearable: true
                    }
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
            refresh(){
                this.getData();
            },
            search(params, done){
                this.getData();
                done();
            },
            remove(row){
                this.loading = true;
                const processId = row.processConfig.processId;
                deleteProcess(this.selectStoreDataId, processId)
                    .then(res => {
                        requestCall(res, this, `删除流程 ${processId}`);
                        this.getData();
                    })
                    .catch(error => {
                        requestThrown(this, error);
                        this.getData();
                    });
                this.loading = false;
            },
            download(row){
                const link = document.createElement('a');
                link.setAttribute("download", "");
                link.href = downloadUrl(row.processConfig.processId);
                link.click();
            },
            getData(){
                this.loading = true;
                getProcessInfo(this.selectStoreDataId)
                    .then(res => {
                        this.processSourceData = res.data.data;
                        this.loading = false;
                    });
            }
        }
    }
</script>
<style lang="scss">
@import '~@/styles/process/processManagement.scss';
</style>