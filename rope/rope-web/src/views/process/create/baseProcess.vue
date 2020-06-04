<template>
    <avue-form :option="option" v-model="form" @submit="handleSubmit">
    </avue-form>
</template>

<script>
    import { getProcessStorage } from '@/api/process/processStorage';

    export default {
        name: "baseProcess",
        data() {
            return {
                form: {},
                configs: {}
            };
        },
        created(){
            getProcessStorage()
                .then(res => {
                    this.configs = res.data.data;
                });
        },
        computed: {
            option(){
                return {
                    icon:'el-icon-info',
                    label: "基础配置",
                    labelWidth: 100,
                    column: [
                        {
                            type: "select",
                            label: "流程存储器",
                            dicData: this.configs,
                            props: {
                                label: "name",
                                value: "id"
                            },
                            span: 12,
                            display: true,
                            prop: "processStorageId",
                            required: true,
                            rules: [
                                {
                                    required: true,
                                    message: "请选择流程存储器"
                                }
                            ],
                            multiple: false,
                            limit: 0,
                            placeholder: "请选择流程存储器"
                        },
                        {
                            type: "switch",
                            label: "是否启动流程",
                            span: 12,
                            display: true,
                            valueDefault: "true",
                            dicData: [
                                {
                                    label: "不启动",
                                    value: false
                                },
                                {
                                    label: "启动",
                                    value: true
                                }
                            ],
                            prop: "isStart"
                        },
                        {
                            type: "input",
                            label: "流程id",
                            span: 12,
                            display: true,
                            prop: "processId",
                            required: true,
                            rules: [
                                {
                                    required: true,
                                    message: "流程id必须填写"
                                }
                            ],
                            placeholder: "请自定义流程id, 全局不能重复"
                        },
                        {
                            type: "input",
                            label: "流程名称",
                            span: 12,
                            display: true,
                            prop: "name",
                            rules: [
                                {
                                    required: true,
                                    message: "流程名称必须填写"
                                }
                            ],
                            placeholder: "请自定义流程名称, 全局可重复"
                        }
                    ]
                }
            }
        },
        methods: {
            handleSubmit(form, done){
                this.$store.commit("setProcessInfo", form);
                done();
                this.$message({
                    message: `创建流程基本信息成功`,
                    type: "success"
                });
            }
        }
    }
</script>
<style lang="scss">
@import '~@/styles/process/baseProcess.scss';
</style>
