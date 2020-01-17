import request from '@/router/axios';
import config from '@/util/config'

const baseUrl = `${config.getServerUrl()}/api/processStorage`;

/**
 * 得到流程存储者的信息
 */
export const getProcessStorage = () => request({
    url:  `${baseUrl}`,
    method: 'get'
});

/**
 * 得到流程存储者的信息
 */
export const getProcessInfo = (processInfoId) => request({
    url:  `${baseUrl}/processInfo/${processInfoId}`,
    method: 'get'
});


/**
 * 创建流程
 * @param processStorageId 流程存储者id
 * @param processConfig 流程配置
 */
export const createProcess = (processStorageId, processConfig) => request({
    url:  `${baseUrl}/${processStorageId}`,
    method: 'post',
    headers: {
        "Content-Type": "application/json;"
    },
    data: processConfig
});


export const deleteProcess = (processStorageId, processId) => request({
    url:  `${baseUrl}/${processStorageId}/${processId}`,
    method: 'delete',
    headers: {
        "Content-Type": "application/json;"
    }
});


export const downloadUrl = (processId) => {
    return `${baseUrl}/download/${processId}`;
};

export const importUrl = (processStorageId) => {
    return `${baseUrl}/import/${processStorageId}`;
};