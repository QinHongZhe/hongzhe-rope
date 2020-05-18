<template>
    <basic-container class='pluginManagement'>
        <avue-crud :data="pluginData" :option="option" v-model="obj"
                   :table-loading="loading"
                   @search-change="search"
                   @refresh-change="refresh">
            <!-- 暂不提供启动、停止、卸载功能。该操作可能会出现意想不到的bug
            <template slot-scope="scope" slot="menu">-->
                <!--<el-button type="primary"-->
                           <!--size="small"-->
                           <!--plain-->
                           <!--v-if="showStartBtn(scope.row)"-->
                           <!--@click="start(scope.row)">启动</el-button>-->
                <!--<el-button type="primary"-->
                           <!--size="small"-->
                           <!--plain-->
                           <!--v-if="scope.row.pluginState === 'STARTED'"-->
                           <!--@click="stop(scope.row)">停止</el-button>-->
                <!--<el-button type="primary"-->
                           <!--size="small"-->
                           <!--plain-->
                           <!--@click="uninstall(scope.row)">卸载</el-button>-->
            <!--</template>
            -->
        </avue-crud>
    </basic-container>
</template>

<script>
    import { getPluginInfo, startPlugin, stopPlugin, uninstallPlugin} from '@/api/plugin/pluginManage';
    import { requestCall, requestThrown } from '@/util/messageUtils';

    export default {
        name: "index",
        data() {
            return {
                obj:{},
                loading: true,
                pluginData: []
            }
        },
        created(){
            this.getPluginInfo();
        },
        computed: {
            option() {
                return {
                    title: '插件管理',
                    page: false,
                    align: 'center',
                    menuAlign: 'center',
                    addBtn: false,
                    columnBtn: false,
                    delBtn: false,
                    editBtn: false,
                    column:[
                        {
                            label: 'id',
                            prop: 'pluginDescriptor',
                            formatter(row, value){
                                return value.pluginId
                            }
                        },
                        {
                            label: '版本',
                            prop: 'pluginDescriptor',
                            formatter(row, value){
                                return value.version
                            }
                        },
                        {
                            label: '描述',
                            prop: 'pluginDescriptor',
                            formatter(row, value){
                                const description = value.pluginDescription;
                                if(description && description !== ''){
                                    return description;
                                } else {
                                    return "无";
                                }
                            },
                            overHidden: true
                        },
                        {
                            label: '作者',
                            prop: 'pluginDescriptor',
                            formatter(row, value){
                                return value.provider
                            }
                        },
                        {
                            label: '状态',
                            prop: 'pluginState',
                        },
                        {
                            label: '路径',
                            prop: 'path',
                            overHidden: true
                        }
                    ]
                }
            }
        },
        methods: {
            showStartBtn(row){
                const state = row.pluginState;
                console.log(state);
                if(state === 'STARTED'){
                    return false;
                } else {
                    console.log("show");
                    return true;
                }
            },
            refresh(){
                this.getPluginInfo();
            },
            search(params, done){
                this.getData();
                done();
            },
            start(row){
                this.loading = true;
                const pluginId = row.pluginDescriptor.pluginId;
                startPlugin(pluginId).then(res => {
                    requestCall(res, this, `启动插件 ${pluginId}`);
                    this.getPluginInfo();
                }).catch(error => {
                    requestThrown(this, error);
                    this.getPluginInfo();
                });
            },
            stop(row){
                this.loading = true;
                const pluginId = row.pluginDescriptor.pluginId;
                stopPlugin(pluginId).then(res => {
                    requestCall(res, this, `停止插件 ${pluginId}`);
                    this.getPluginInfo();
                }).catch(error => {
                    requestThrown(this, error);
                    this.getPluginInfo();
                });
            },
            uninstall(row){
                this.loading = true;
                const pluginId = row.pluginDescriptor.pluginId;
                uninstallPlugin(pluginId).then(res => {
                    requestCall(res, this, `卸载插件 ${pluginId}`);
                    this.getPluginInfo();
                }).catch(error => {
                    requestThrown(this, error);
                    this.getPluginInfo();
                });
            },
            getPluginInfo(){
                this.loading = true;
                getPluginInfo()
                    .then(res => {
                        this.pluginData = res.data.data;
                        this.loading = false;
                    });
            }
        }
    }
</script>
<style lang="scss">
@import '~@/styles/plugin/pluginManagement.scss';
</style>
