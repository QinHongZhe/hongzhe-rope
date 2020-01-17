import request from '@/router/axios';
import config from '@/util/config'

const baseUrl = `${config.getServerUrl()}/api/process`;

export const getProcess = () => request({
    url:  `${baseUrl}`,
    method: 'get'
});


export const startProcess = (processId) => request({
    url:  `${baseUrl}/start/${processId}`,
    method: 'post'
});


export const stopProcess = (processId) => request({
    url:  `${baseUrl}/stop/${processId}`,
    method: 'post'
});


export const removeProcess = (processId) => request({
    url:  `${baseUrl}/remove/${processId}`,
    method: 'post'
});


export const countProcess = () => request({
    url:  `${baseUrl}/count`,
    method: 'get'
});