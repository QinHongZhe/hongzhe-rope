export const fromFactory = (configParam)=>{

    const columns = [];
    if(!configParam){
        return columns;
    }

    for(let key in configParam){
        let config = configParam[key];
        if(!config){
            continue;
        }
        let columnConfig = null;
        switch (config.type) {
            case 'text': {
                columnConfig = getTest(config);
                break;
            }
            case 'number': {
                columnConfig = getNumber(config);
                break;
            }
            case 'dropdown': {
                columnConfig = getDropdown(config);
                break
            }
            case 'boolean': {
                columnConfig = getBoolean(config);
                break
            }
            case 'listMap': {
                columnConfig = getListMap(config);
                break
            }
            case 'list': {
                columnConfig = getList(config);
                break
            }
            default: {
                console.error("不支持的配置类型：" + config.type);
            }
        }
        if(columnConfig){
            if(columnConfig instanceof Array){
                for (let item in columnConfig){
                    columns.push(item);
                }
            } else {
                if(config.description){
                    columnConfig['tip'] = config.description;
                }
                columns.push(columnConfig);
            }
        }
    }

    return columns;
};

/**
 * text 输入框
 * @param config 配置
 * @return {{prop: *, display: boolean, icon: string, rules: {message: string, required: *}[], label: *, placeholder: string, type: string, required: *, span: number, valueDefault: *}}
 */
const getTest = (config) => {
    let lable = config.humanName;

    const attributes = config.attributes;
    let type = "input";
    if(attributes){
        if(attributes.indexOf('password') > -1){
            type = "password"
        } else if(attributes.indexOf('textarea') > -1){
            type = "textarea";
        }
    }


    return {
        type: type,
        prop: config.key,
        label: lable,
        icon: "icon-input",
        span: 12,
        display: true,
        placeholder: `请输入${lable}`,
        required: config.required,
        rules: [
            {
                required: config.required,
                message: `${lable}不能为空`
            }
        ],
        valueDefault: config.defaultValue,
    }
};

/**
 * 得到数字输入框
 * @param config 配置
 * @return {{minRows: number, display: boolean, precision: number, icon: string, rules: {message: string, required: *}[], label: *, type: string, required: *, maxRows: number, readonly: boolean, prop: *, step: number, placeholder: string, span: number, valueDefault: *}}
 */
const getNumber = (config) => {
    let lable = config.humanName;

    let defaultValue = config.defaultValue;
    if(!defaultValue){
        defaultValue = 0;
    }

    let minRows = -2147483648;
    let maxRows = 2147483647;
    if(config.attributes){
        if(config.attributes.indexOf('only_positive') > -1){
            // 只能为正数
            minRows = 0
        } else if(config.attributes.indexOf('only_negative') > -1){
            // 只能为负数
            maxRows = -1;
        }
    }

    return {
        type: "number",
        prop: config.key,
        label: lable,
        icon: "icon-input",
        span: 12,
        display: true,
        minRows: minRows,
        maxRows: maxRows,
        placeholder: `请输入${lable}`,
        required: config.required,
        readonly: false,
        rules: [
            {
                required: config.required,
                message: `${lable}不能为空`
            }
        ],
        valueDefault: defaultValue,
        step: 1,
        precision: 0
    };
};

/**
 * 下拉框
 * @param config 下拉框的配置
 * @return {{dicData: Array, display: boolean, prop: *, rules: {message: string, required: *}[], label: *, type: string, required: *, span: number}}
 */
const getDropdown = (config, multiple) => {
    let lable = config.humanName;

    const dicData = [];
    const values = config.additionalInfo.values;
    if(values){
        for (let key in values){
            let data = {
                label: values[key],
                value: key
            };
            dicData.push(data);
        }
    }
    let limit = 0;
    if(multiple){
        multiple = true;
        limit = dicData.length;
    } else {
        multiple = false;
    }
    return {
        type: "select",
        label: lable,
        dicData: dicData,
        span: 12,
        display: true,
        prop: config.key,
        required: config.required,
        rules: [
            {
                required: config.required,
                message: "请选择" + lable
            }
        ],
        multiple: multiple,
        limit: limit
    }
};

/**
 * 得到boolean组件
 * @param config
 * @return {{dicData: *[], display: boolean, prop: *, rules: {message: string, required: *}[], label: *, type: string, required: *, span: number}}
 */
const getBoolean = (config) => {
    let lable = config.humanName;

    let defaultValue = config.defaultValue;
    if(!defaultValue){
        defaultValue = "false";
    }


    return {
        type: "switch",
        label: lable,
        dicData: [
            {
                label: "否",
                value: false
            },
            {
                label: "是",
                value: true
            }
        ],
        span: 12,
        display: true,
        prop: config.key,
        required: config.required,
        valueDefault: defaultValue,
        rules: [
            {
                required: config.required,
                message: "请选择" + lable
            }
        ]
    }
};


const getListMap = (config) => {
    let lable = config.humanName;

    let key = "key";
    let value = "value";


    if(config.additionalInfo && config.additionalInfo.props){
        const props = config.additionalInfo.props;
        key = props.keyProp;
        value = props.valueProp;
    }


    return {
        type: "dynamic",
        label: lable,
        span: 12,
        display: true,
        prop: config.key,
        required: config.required,
        rules: [
            {
                required: config.required,
                message: "请选择" + lable
            }
        ],
        children: {
            align: "center",
            headerAlign: "center",
            column: [
                {
                    type: "input",
                    label: "key",
                    span: 24,
                    display: true,
                    prop: key
                },
                {
                    type: "input",
                    label: "value",
                    span: 24,
                    display: true,
                    prop: value
                }
            ]
        }
    }
};



const getList = (config) => {
    let lable = config.humanName;

    if(config.attributes && config.attributes.indexOf('allow_create') > -1){
        // 组输入框
        const columns = [];
        const values = config.additionalInfo.values;
        if(values){
            for (let key in values){
                const column = {
                    type: "input",
                    prop: key,
                    label: `${lable}[${key}]`,
                    icon: "icon-input",
                    span: 4,
                    display: true,
                    placeholder: `请输入${key}`,
                    required: false,
                    valueDefault: "",
                };
                columns.push(column);
            }
            return columns;
        } else {
            return null;
        }
    } else {
        // 多选下拉框
        return getDropdown(config, true);
    }
};
