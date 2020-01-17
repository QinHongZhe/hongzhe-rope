import request from '@/router/axios';
import config from '@/util/config'

const baseUrl = `${config.getServerUrl()}/api/metrics`;


export const overviewMetrics = () => request({
    url:  `${baseUrl}/overview`,
    method: 'get',
    disableNProgress: true
});

export const readerMetrics = (processId) => request({
    url:  `${baseUrl}/reader/${processId}`,
    method: 'get'
});

export const writersMetrics = (processId) => request({
    url:  `${baseUrl}/writers/${processId}`,
    method: 'get'
});