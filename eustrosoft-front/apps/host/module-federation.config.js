// const baseConfig = require('../../module-federation.config');

module.exports = {
  // ...baseConfig,
  name: 'host',
  remotes: ['request-handler', 'file-explorer'],
};
