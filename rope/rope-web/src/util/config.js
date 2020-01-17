const Config = {
    getConfig() {
        return window.config;
    },
    getEnv(){
        return window.config.env;
    },
    getServerUrl(){
        return window.config.serverUrl;
    },
    getMenuUrl(){
        if(this.getEnv() === 'dev'){
            return "";
        } else {
            return this.getServerUrl();
        }
    }
};
export default Config;
