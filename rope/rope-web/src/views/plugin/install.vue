<template>
    <basic-container class='uploadDragger'>
        <p class='avue-crud__title'>安装插件</p>
        <avue-form :option="option"
                   v-model="form"
                   :upload-before="uploadBefore"
                   :upload-after="uploadAfter">
        </avue-form>
    </basic-container>
</template>

<script>
    import { installUrl } from '@/api/plugin/pluginManage';
    import { getFileSuffix } from '@/util/fileUtils';

    export default {
        name: "index",
        data() {
            return {
                form:{},
                loading: true,
                pluginData: []
            }
        },
        created(){

        },
        computed: {
            option() {
                return {
                    labelWidth: 120,
                    column: [
                        {
                            label: '上传插件包',
                            prop: 'file',
                            type: 'upload',
                            span: 24,
                            drag: true,
                            tip: '只能上传jar文件',
                            action: installUrl,
                            propsHttp: {
                                name: ''
                            },
                        }
                    ]
                }
            }
        },
        methods: {
            uploadBefore(file, done, loading) {
                let filename = file.name;
                if(getFileSuffix(filename) === 'jar'){
                    done();
                    return;
                }
                loading();
                this.$message.error(filename + ' 文件格式不正确。需要 jar 文件');
            },
            uploadAfter(res, done, loading) {
                if(res.code === 1){
                    done();
                    this.$message.success('安装成功')
                } else {
                    loading();
                    this.$message.error('安装失败: ' + res.msg)
                }
            },
        }
    }
</script>
<style lang="scss">
@import '~@/styles/process/uploadDragger.scss';
</style>