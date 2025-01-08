module.exports = {
  root: true,
  env: {
    browser: true,
    node: true,
  },
  extends: ['eslint:recommended', 'plugin:vue/recommended'],
  parserOptions: {
    parser: '@babel/eslint-parser',
    ecmaVersion: 2021,
    sourceType: 'module',
  },
  rules: {
    'no-console': 'off',
    'no-debugger': 'warn',
  },
};
