const getConfig = require('./config/getConfig');

module.exports = getConfig({
  type: 'extension',
  proxy: {
    '/api/': {
      target: 'http://10.129.6.123:8107/',
      changeOrigin: true,
      secure: false
    },
    '/modules/horizon/_sdh_extension_entity_segment_create_type_plugin/': {
      changeOrigin: true,
      secure: false,
      target: 'http://10.129.6.123:8107/',
    }
  }
}, {
  type: "sc",
  packageName: "pushChannelPlugin",
  include: [],
  exclude: []
});