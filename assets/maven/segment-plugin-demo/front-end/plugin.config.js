// 此文件设计其他调试模式，暂时不要修改此文件

module.exports = {
  port: 3300,
  // 是否使用本地服务容器，在需要需要调试本地代码时打开
  useLocalWrapper: true,
  // 静态资源访问代理
  resourceProxy: {
    target: '',
    logs: true,
  },
  // 远程接口请求
  remoteProxy: {
    target: '',
    changeOrigin: true,
    logs: true
  },
  // http接口 访问代理
  interfaceProxy: {
  }
}
