export default [
  {
    key: 'segment-plugin-demo-extension',  // chanelName 通道名
    extensionPointId: 'sdh.pushSetting.Channel',
    processor: () => import('./ExtensionPoint/DemoChanel'),
  },
]
