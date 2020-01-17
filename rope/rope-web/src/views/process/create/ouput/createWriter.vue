<template>
    <div>
        <el-select class="selectCommon" v-model="configValue" placeholder="请选择写入者" size="small"
                   @change="change">
            <el-option
                    v-for="item in configs"
                    :key="item.id"
                    :label="item.name"
                    :value="item.id">
            </el-option>
        </el-select>
        <br/>
        <el-input class="selectCommon" style="width: 30%" v-model="writerCode" size="small"
                  placeholder="自定义写入者code. 同一个输出的写入者code不能相同">

        </el-input>
        <dynamicForm :configParam="configParam" :handleSubmit="handleSubmit" label="数据写入者配置"/>

    </div>

</template>

<script>

    import { getWriters } from '@/api/process/processParam';
    import dynamicForm from '../components/dynamicForm';


    export default {
        name: "createWriter",
        components: {
            dynamicForm: dynamicForm
        },

        created(){
            getWriters()
                .then(res => {
                    this.configs = res.data.data;
                });
        },
        data() {
            return {
                configs: [],
                configValue: '',
                form: {},
                configParam: {},
                writerCode: ""
            }
        },
        computed:{
        },
        methods:{
            change(item){
                for (let config of this.configs){
                    if(config.id === item){
                        this.configParam = config.configParam;
                    }
                }
            },
            handleSubmit(form, done){
                done();
                if(this.writerCode === ""){
                    this.$message({
                        message: `写入者code不能为空`,
                        type: "error"
                    });
                    return;
                }
                this.$store.commit("addWriter", {
                    writer: {
                        id: this.configValue,
                        code: this.writerCode,
                        params: form
                    },
                    call: this.addWriterCall
                });
            },
            addWriterCall(type, msg){
                if(type === "success"){
                    this.$message({
                        message: `新增数据写入者成功`,
                        type: "success"
                    });
                    this.writerCode = "";
                } else {
                    this.$message({
                        message: msg,
                        type: "error"
                    });
                }
            }
        }
    }
</script>

<style scoped>
@import '~@/styles/process/processCommon.scss';
</style>