/**
 * Metro configuration for React Native
 * https://github.com/facebook/react-native
 *
 * @format
 */
const path = require('path');
const exclusionList = require('metro-config/src/defaults/exclusionList');
const escape = require('escape-string-regexp');
const pak = require('../package.json');

const root = path.resolve(__dirname, '..');

const modules = Object.keys({
  ...pak.peerDependencies,
});

module.exports = {
  // dependencies: {
  //   [pak.name]: {
  //     root: path.join(__dirname, '..'),
  //   },
  // },
  // // workaround for an issue with symlinks encountered starting with
  // // metro@0.55 / React Native 0.61
  // // (not needed with React Native 0.60 / metro@0.54)
  // resolver: {
  //   extraNodeModules: new Proxy(
  //     {},
  //     {get: (_, name) => path.resolve('.', 'node_modules', name)},
  //   ),
  // },
  // // quick workaround for another issue with symlinks
  // watchFolders: ['.', '..'],

  projectRoot: __dirname,
  watchFolders: [root],
  resolver: {
    blockList: exclusionList(
      modules.map(
        m => new RegExp(`^${escape(path.join(root, 'node_modules', m))}\\/.*$`),
      ),
    ),

    extraNodeModules: modules.reduce((acc, name) => {
      acc[name] = path.join(__dirname, 'node_modules', name);
      return acc;
    }, {}),
  },
  transformer: {
    getTransformOptions: async () => ({
      transform: {
        experimentalImportSupport: false,
        inlineRequires: true,
      },
    }),
  },
};

// module.exports = {
//   transformer: {
//     getTransformOptions: async () => ({
//       transform: {
//         experimentalImportSupport: false,
//         inlineRequires: true,
//       },
//     }),
//   },
// };
