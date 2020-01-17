import { createProcess } from '@/api/process/processStorage'

const process = {
    state: {
        processStorageId: null,
        processConfig: {},
        input: {},
        dateHandlerFlow: new Map(),
        dateHandlerChange: 0,
        output: {},
        writers: new Map(),
        writerChange: 0,
    },
    actions: {
        createProcess({ state, commit }) {

            if(!state.processStorageId){
                return "流程存储者id不能为空";
            }
            if(!state.input){
                return "输入配置不能为空";
            }
            if(!state.output){
                return "输出配置不能为空";
            }
            const writersMap = state.writers;
            if(!writersMap){
                return "数据写入者不能为空";
            }

            const writers = [];
            writersMap.forEach((v)=>{
                writers.push(v);
            });

            if(writers.length === 0){
                return "数据写入者不能为空";
            }

            // 转换数据处理者
            const dateHandlerFlow = [];
            state.dateHandlerFlow.forEach((v)=>{
                dateHandlerFlow.push(v);
            });


            const processConfig = {
                processId: state.processConfig.processId,
                name: state.processConfig.name,
                isStart: state.processConfig.isStart,
                input: state.input,
                dateHandlerFlow: dateHandlerFlow,
                output: {
                    id: state.output.id,
                    params: state.output.params,
                    writers: writers
                }
            };

            console.log(processConfig);

            return new Promise((resolve, reject) => {
                createProcess(state.processStorageId, processConfig).then((result) => {
                    if(result.data){
                        if(result.data.code === 1){
                            commit('clean');
                            resolve(true);
                        } else {
                            resolve(result.data.msg);
                        }
                    } else {
                        resolve("创建流程失败");
                    }
                }).catch(error => {
                    reject(error)
                })
            })
        }
    },
    mutations: {
        setProcessInfo: (state, {processStorageId, processId, name, isStart}) => {
            if(processStorageId && processId){
                state.processStorageId = processStorageId;
                state.processConfig.processId = processId;
                state.processConfig.name = name;
                state.processConfig.isStart = isStart;
            }
        },
        setInput: (state, {inputId, params}) => {
            if(inputId){
                state.input.id = inputId;
                state.input.params = params;
            }
        },
        setReader: (state, { readerId, params }) => {
            if(readerId){
                state.input.reader = {
                    id: readerId,
                    params: params
                };
            }
        },
        setReadConverter: (state, { converterId, params }) => {
            if(converterId){
                state.input.converter = {
                    id: converterId,
                    params: params
                };
            }
        },
        addDateHandler: (state, dateHandler) => {
            if(dateHandler && dateHandler.id){
                state.dateHandlerFlow.set(dateHandler.id, dateHandler);
                const dateHandlerChange = state.dateHandlerChange;
                state.dateHandlerChange = dateHandlerChange + 1;
            }
        },
        removeDateHandler: (state, dateHandlerId) => {
            if(dateHandlerId){
                state.dateHandlerFlow.delete(dateHandlerId);
                const dateHandlerChange = state.dateHandlerChange;
                state.dateHandlerChange = dateHandlerChange + 1;
            }
        },
        setOutput: (state, { outputId, params }) => {
            if(outputId){
                state.output = {
                    id: outputId,
                    params: params
                };
                console.log(state.output);
            }
        },
        addWriter: (state, { writer, call }) => {
            if(!writer){
                call("error", "写入者信息不能为空");
            }
            if(!writer.code){
                call("error", "写入者code不能为空");
            }
            if(state.writers.has(writer.code)){
                call("error", `已经存在该code: ${writer.code}`);
                return ;
            }
            state.writers.set(writer.code, writer);
            const writerChange = state.writerChange;
            state.writerChange = writerChange + 1;
            call("success", "新增数据写入者成功");
        },
        removeWriter: (state, writerCode) => {
            if(writerCode){
                state.writers.delete(writerCode);
                const writerChange = state.writerChange;
                state.writerChange = writerChange + 1;
            }
        },
        clean: (state) => {
            state.processStorageId = null;
            state.processConfig = {};
            state.input = {};
            state.dateHandlerFlow = new Map();
            state.output = {};
            state.writers = new Map()
        }
    }
};

export default process;

/** 流程配置样例
const processConfig = {
    processId: null,
    name: "",
    isStart: true,
    input: {
        id: null,
        params: null,
        reader: {
            id: null,
            params: null
        },
        converter: {
            id: null,
            params: null
        }
    },
    dateHandlerFlow: [
        {
            id: null,
            params: null
        }
    ],
    output: {
        id: null,
        params: null,
        writers: [
            {
                id: null,
                params: null,
                converter: {
                    id: null,
                    params: null
                }
            }
        ]
    }
};
 **/
