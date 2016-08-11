var webpack = require('webpack');
var path = require('path');

module.exports = {
  devtool: 'eval-source-app',
  entry: './src/main.js',
  output: {
    path: path.join(__dirname, 'build'),
    filename: 'bundle.js'
  },
  module: {
    loaders: [
      { test: /\.js/, loader: 'babel' },
      { test: /\.less/, loader: 'style!css!less' },
      { test: /\.css/, loader: 'style!css' },
      { test: /\.(woff2|woff|ttf|svg|eot)$/, loader: 'file' }
    ]
  },
  devServer: {
    colors: true,
    historyApiFallback: true,
    inline: true,
    hot: true
  }
};
