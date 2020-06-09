<template>
    <div class='createDataHandler'>
        <el-select class="selectCommon" v-model="configValue" placeholder="请选择数据处理者" size="small" @change="change">
            <el-option
                    v-for="item in configs"
                    :key="item.id"
                    :label="item.name"
                    :value="item.id">
            </el-option>
        </el-select>

        <dynamicForm label="数据处理者配置" :configParam="configParam" :handleSubmit="handleSubmit" />


        <br/><br/>
        <avue-crud :data="configTableData" :option="tableOption" v-model="obj"
                   @row-del="deleteConfig"></avue-crud>

    </div>

</template>

<script>
    import { mapState } from "vuex";
    import { getDataHandlers } from '@/api/process/processParam';
    import dynamicForm from '../components/dynamicForm';

    export default {
        name: "createDataHandler",
        components: {
            dynamicForm: dynamicForm,
        },
        created(){
            getDataHandlers()
                .then(res => {
                    this.configs = res.data.data;
                });
        },
        data() {
            return {
                obj:{},
                configs: [],
                configValue: '',
                configParam: {},
                configTableData: []
            }
        },
        computed: mapState({
            dateHandlerFlow: state => state.process.dateHandlerFlow,
            dateHandlerChange: state => state.process.dateHandlerChange,
            tableOption() {
                return {
                    title: '新增的数据处理者：',
                    page: false,
                    align: 'center',
                    menuAlign: 'center',
                    addBtn: false,
                    columnBtn: false,
                    editBtn: false,
                    refreshBtn: false,
                    column: [
                        {
                            label: '数据处理者id',
                            prop: 'id'
                        },
                        {
                            label: '数据处理者的配置',
                            prop: 'params'
                        }
                    ]
                }
            }
        }),
        methods:{
            change(item){
                for (let config of this.configs){
                    if(config.id === item){
                        this.configParam = config.configParam;
                    }
                }
            },
            handleSubmit(form, done){
                const dateHandler = {
                    id: this.configValue,
                    params: form
                };
                this.$store.commit("addDateHandler", dateHandler);
                done();
                this.$message({
                    message: `新增数据处理者成功`,
                    type: "success"
                });
            },
            deleteConfig(row){
                this.$store.commit("removeDateHandler", row.id);
            }
        },
        watch: {
            dateHandlerChange: function(){
                const configTableData = [];
                this.dateHandlerFlow.forEach((v, k)=>{
                    let paramsJson = "无";
                    if(v && v.params){
                        paramsJson = JSON.stringify(v.params, null, 2);
                    }
                    configTableData.push({
                        id: k,
                        params: paramsJson
                    });
                });
                this.configTableData = configTableData;
            }
        }
    }
</script>
<style lang="scss">
@import '~@/styles/process/createDataHandler.scss';
@import '~@/styles/process/processCommon.scss';
</style>
