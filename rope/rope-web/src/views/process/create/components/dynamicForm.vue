<template>
    <div v-if="refresh">
        <avue-form :option="option" v-model="form" @submit="currentHandleSubmit">
        </avue-form>
    </div>

</template>

<script>
    import { fromFactory } from './fromFactory.js'

    export default {
        name: "dynamicForm",
        props: {
            configParam: {
                type: Object,
                default(){
                    return undefined;
                }
            },
            label: {
                type: String,
                default(){
                    return "分组";
                }
            },
            handleSubmit: {
                type: Function,
                default(){
                    return (form, done) => {
                        done();
                    }
                }
            }
        },
        data() {
            return {
                form: {},
                refresh: false,
                option: {
                    icon:'el-icon-info',
                    label: this.label,
                    labelWidth: 200,
                    column: [

                    ]
                }
            }
        },
        methods: {
            currentHandleSubmit(form, done){
                const f = {};
                for(var key in form){
                    if(key.indexOf("$") !== 0){
                        f[key] = form[key];
                    }
                }
                this.handleSubmit(f, done);
            }
        },
        watch: {
            configParam(configParam){
                this.refresh = false;
                if(configParam){
                    this.option.column = fromFactory(configParam);
                } else {
                    this.option.column = [];
                }
                this.refresh = true;
            }
        }
    }
</script>

<style scoped>

</style>