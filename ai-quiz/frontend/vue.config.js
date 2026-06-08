const { defineConfig } = require("@vue/cli-service");
module.exports = defineConfig({
  transpileDependencies: true,
  lintOnSave: false,
  devServer: {
    proxy: {
      "/api": {
        target: "http://localhost:8101",
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
            test: /[\\/]node_modules[\\/](vue|vue-router|pinia)[\\/]/,
            priority: 20,
          },
          arco: {
            name: "chunk-arco",
            test: /[\\/]node_modules[\\/]@arco-design[\\/]/,
            priority: 20,
          },
          axios: {
            name: "chunk-axios",
            test: /[\\/]node_modules[\\/](axios)[\\/]/,
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
});
