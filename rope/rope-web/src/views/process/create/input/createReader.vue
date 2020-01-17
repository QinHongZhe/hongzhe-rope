<template>
    <div>
        <el-select class="selectCommon" v-model="configValue" placeholder="请选择读取者" size="small" @change="change">
            <el-option
                    v-for="item in configs"
                    :key="item.id"
                    :label="item.name"
                    :value="item.id">
            </el-option>
        </el-select>

        <dynamicForm :configParam="configParam" :handleSubmit="handleSubmit" label="数据读取者配置"/>

    </div>

</template>

<script>

    import { getReaders } from '@/api/process/processParam';
    import dynamicForm from '../components/dynamicForm';


    export default {
        name: "createWriter",
        components: {
            dynamicForm: dynamicForm
        },

        created(){
            getReaders()
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
                const reader = {
                    readerId: this.configValue,
                    params: form
                };
                this.$store.commit("setReader", reader);
                done();
                this.$message({
                    message: `创建数据读取者成功`,
                    type: "success"
                });
            }
        }
    }
</script>

<style scoped>
@import '~@/styles/process/processCommon.scss';
</style>