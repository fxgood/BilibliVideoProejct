module.exports = {
    dev: {
      // Paths
      assetsSubDirectory: 'static',
      assetsPublicPath: '/',
      proxyTable: {     //axios跨域处理
        '/api': {       //此处并非和url一致 任意取 但要与 Vue.prototype.HOME 的值一样
          target:'http://localhost:8080/', // 你的数据连接
          changeOrigin:true, //允许跨域  关键
          pathRewrite:{
            '^/api': ''
          }
        }
      }
    }
  }
  