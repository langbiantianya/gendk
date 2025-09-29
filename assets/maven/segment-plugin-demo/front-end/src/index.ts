import extensions from './extensions';

export default {
  fragments: [
    {
      id: '{{.ProjectName}}',   // 插件ID   和后端一一对应
      slotId: 'SDH_PLUGIN_SLOT',
      processor: () => extensions,
    }
  ]
}
