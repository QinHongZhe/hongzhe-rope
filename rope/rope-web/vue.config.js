let publicPath = 'admin';
module.exports = {
  publicPath: publicPath,
  lintOnSave: true,
  productionSourceMap: false,
  chainWebpack: (config) => {
    //忽略的打包文件
    config.externals({
      'vue': 'Vue',
      'vue-router': 'VueRouter',
      'vuex': 'Vuex',
      'axios': 'axios',
      'element-ui': 'ELEMENT',
    });
    const entry = config.entry('app')
    entry
      .add('babel-polyfill')
      .end();
    entry
      .add('classlist-polyfill')
      .end();
    entry
      .add('@/mock')
      .end();
  },
  //配置转发代理
  devServer: {
    open: process.platform === 'darwin',
    host: '0.0.0.0',
    port: 80,
    https: false,
    hotOnly: false,
    proxy: null, // 设置代理
    // 开启热更新
    hot: true
  }
};