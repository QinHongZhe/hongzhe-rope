<template>
    <basic-container class='uploadDragger'>


        <p class='avue-crud__title'>导入流程</p>

        <span>选择流程存储者: </span>
        <el-select v-model="selectStoreDataId" placeholder="请选择流程存储者" size="small" style="padding-left: 10px">
            <el-option
                    v-for="item in storeData"
                    :key="item.id"
                    :label="item.name"
                    :value="item.id">
            </el-option>
        </el-select>

        <avue-form :option="option"
                   v-model="form"
                   :upload-before="uploadBefore"
                   :upload-after="uploadAfter">
        </avue-form>
    </basic-container>
</template>

<script>
    import { getProcessStorage, importUrl } from '@/api/process/processStorage';
    import { getFileSuffix } from '@/util/fileUtils';

    export default {
        name: "import",
        data() {
            return {
                form:{},
                loading: true,
                storeData: [],
                selectStoreDataId: ''
            }
        },
        created(){
            getProcessStorage()
                .then(res => {
                    const data = res.data.data;
                    if(data && data.length > 0){
                        this.storeData = data;
                        this.selectStoreDataId = data[0].id;
                    }
                });
        },
        computed: {
            option() {
                return {
                    labelWidth: 120,
                    menuBtn: false,
                    column: [
                        {
                            label: '流程json文件',
                            prop: 'jsonFile',
                            type: 'upload',
                            span: 24,
                            drag: true,
                            tip: '只能上传json文件',
                            action: importUrl(this.selectStoreDataId),
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
                if(getFileSuffix(filename) === 'json'){
                    done();
                    return;
                }
                loading();
                this.$message.error(filename + ' 文件格式不正确。需要 json 文件');
            },
            uploadAfter(res, done, loading) {
                if(res.code === 1){
                    done();
                    this.$message.success('导入成功')
                } else {
                    loading();
                    this.$message.error('导入失败: ' + res.msg)
                }
            },
        }
    }
</script>
<style lang="scss">
@import '~@/styles/process/uploadDragger.scss';
</style>
