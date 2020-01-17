<template>
    <div>
        <el-select class="selectCommon" v-model="configValue" placeholder="请选择输入" size="small" @change="change">
            <el-option
                    v-for="item in configs"
                    :key="item.id"
                    :label="item.name"
                    :value="item.id">
            </el-option>
        </el-select>

        <dynamicForm label="输入配置" :configParam="configParam" :handleSubmit="handleSubmit"/>

        <createReader />

    </div>

</template>

<script>

    import { getInputs } from '@/api/process/processParam';
    import dynamicForm from '../components/dynamicForm';
    import createReader from './createReader';

    export default {
        name: "createInput",
        components: {
            dynamicForm: dynamicForm,
            createReader: createReader
        },
        created(){
            getInputs()
                .then(res => {
                    this.configs = res.data.data;
                });
        },
        data() {
            return {
                configs: [],
                configValue: '',
                form: {},
                configParam: {}
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
                this.$store.commit("setInput", {
                    inputId: this.configValue,
                    params: form
                });
                done();
                this.$message({
                    message: `创建输入成功`,
                    type: "success"
                });
            }
        }
    }
</script>

<style scoped>
@import '~@/styles/process/processCommon.scss';
</style>