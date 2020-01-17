import request from '@/router/axios';

var baseUrl = 'http://127.0.0.1:8080/api/plugin';

/**
 * 得到插件的信息
 */
export const getPluginInfo = () => request({
    url:  `${baseUrl}`,
    method: 'get'
});

/**
 * 启动插件
 * @param pluginId 插件id
 * @return {AxiosPromise}
 */
export const startPlugin = (pluginId) => request({
    url:  `${baseUrl}/start/${pluginId}`,
    method: 'POST'
});


/**
 * 停止插件
 * @param pluginId 插件id
 * @return {AxiosPromise}
 */
export const stopPlugin = (pluginId) => request({
    url:  `${baseUrl}/stop/${pluginId}`,
    method: 'POST'
});


/**
 * 卸载插件
 * @param pluginId 插件id
 * @return {AxiosPromise}
 */
export const uninstallPlugin = (pluginId) => request({
    url:  `${baseUrl}/uninstall/${pluginId}`,
    method: 'POST'
});


export const installUrl = () => {
    return `${baseUrl}//api/plugin/upload`;
};