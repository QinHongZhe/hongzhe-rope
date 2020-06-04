<template>
    <div v-if="refresh">
        <avue-form  :option="option" v-model="formData" @submit="currentHandleSubmit" />
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
                refresh: false,
                option: {
                    icon:'el-icon-info',
                    label: this.label,
                    labelWidth: 200,
                    column: [

                    ]
                },
                formData: {}
            }
        },
        methods: {
            currentHandleSubmit(form, done){
                const f = {};
                const columns = this.option.column;
                console.log(form);
                for(let key in this.formData){
                    for(let j=0; j<columns.length; j++){
                        const prop = columns[j].prop;
                        if(prop && prop === key){
                            if(key.indexOf("$") !== 0){
                                f[key] = this.formData[key];
                            }
                        }
                    }
                }
                done();
                this.handleSubmit(f, done);
            },
            clear(){

            }
        },
        watch: {
            configParam(configParam){
                this.refresh = false;
                this.option.column = [];
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