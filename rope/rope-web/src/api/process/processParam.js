import request from '@/router/axios';
import config from '@/util/config'

const baseUrl = `${config.getServerUrl()}/api/processParam`;

export const getInputs = () => request({
    url:  `${baseUrl}/inputs`,
    method: 'get'
});


export const getReaders = () => request({
    url:  `${baseUrl}/readers`,
    method: 'get'
});

export const getDataHandlers = () => request({
    url:  `${baseUrl}/dataHandlers`,
    method: 'get'
});

export const getOutputs = () => request({
    url:  `${baseUrl}/outputs`,
    method: 'get'
});

export const getWriters = () => request({
    url:  `${baseUrl}/writers`,
    method: 'get'
});
