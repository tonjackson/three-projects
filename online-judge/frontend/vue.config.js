const { defineConfig } = require("@vue/cli-service");
const MonacoWebpackPlugin = require("monaco-editor-webpack-plugin");

module.exports = defineConfig({
  transpileDependencies: true,
  lintOnSave: false,
  devServer: {
    port: 5174,
    client: {
      overlay: {
        runtimeErrors: (error) => {
          const ignoreErrors = [
            "ResizeObserver loop completed with undelivered notifications.",
          ];
          return !ignoreErrors.includes(error.message);
        },
      },
    },
    proxy: {
      "/api": {
        target: "http://localhost:8121",
        changeOrigin: true,
      },
    },
  },
  configureWebpack: {
    optimization: {
      splitChunks: {
        chunks: "all",
        cacheGroups: {
          vue: {
            name: "chunk-vue",
            test: /[\\/]node_modules[\\/](vue|vue-router|vuex)[\\/]/,
            priority: 20,
          },
          arco: {
            name: "chunk-arco",
            test: /[\\/]node_modules[\\/]@arco-design[\\/]/,
            priority: 20,
          },
          monaco: {
            name: "chunk-monaco",
            test: /[\\/]node_modules[\\/](monaco-editor)[\\/]/,
            priority: 20,
          },
          highlight: {
            name: "chunk-highlight",
            test: /[\\/]node_modules[\\/](highlight\.js)[\\/]/,
            priority: 20,
          },
          codemirror: {
            name: "chunk-codemirror",
            test: /[\\/]node_modules[\\/](codemirror|@codemirror)[\\/]/,
            priority: 20,
          },
          libs: {
            name: "chunk-libs",
            test: /[\\/]node_modules[\\/]/,
            priority: 10,
            reuseExistingChunk: true,
          },
        },
      },
    },
  },
  chainWebpack(config) {
    config.plugin("monaco").use(new MonacoWebpackPlugin());
  },
});
