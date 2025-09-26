const isProduction = process.env.NODE_ENV === 'production';

module.exports = {
  type: 'module', // can be 'module', 'component', do not edit this after template init
  webpack: {
    // minimize: false, // false 表示不压缩代码，正式版请勿提交
    buildTarget: 'fe2022', // 以 es6 为编译目标，@sef/toolchain >= 0.4.0 缺省值
    babel: {
      plugins: [require.resolve('babel-plugin-date-fns')],
      cacheDirectory: false, // 此处不要删除，国际化 collect 需要去掉babel缓存，现在toolchain 内部有问题，先这样解决
    },
    devtool: isProduction ? null : 'eval-cheap-module-source-map',
    needAnalyzer: false,
  },
  migrate: {
    theme: true,
  },
  wrapperWebpack: {
    devtool: isProduction ? null : 'eval-cheap-module-source-map',
    needAnalyzer: false,
  },
  customSnapshot: {
    managedPaths: [],
  },
  proxy: {
    '/api/': {
      target: 'http://10.129.24.199:8107/', // admin 123+++sdh
      changeOrigin: true,
      secure: false,
    },
  },
};
