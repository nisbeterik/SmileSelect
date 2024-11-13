import globals from 'globals';
import pluginVue from 'eslint-plugin-vue';
import vueParser from 'vue-eslint-parser';
import babelParser from '@babel/eslint-parser';

/** @type {import('eslint').Linter.FlatConfig[]} */
export default [
  {
    files: ['smile-select-frontend/src/**/*.js'], // Target JavaScript files
    languageOptions: {
      globals: globals.browser,
      parser: babelParser, // Use Babel parser for JS files
      parserOptions: {
        ecmaVersion: 2020,
        sourceType: 'module',
        requireConfigFile: false, // Disable the requirement for a Babel config file
      },
    },
    rules: {
      semi: ['error', 'always'],
      quotes: ['error', 'single'],
      indent: ['error', 2],
      'no-unused-vars': 'warn',
      'no-console': 'warn',
      'comma-dangle': ['error', 'always-multiline'],
      'eol-last': ['error', 'always'],
      'arrow-parens': ['error', 'as-needed'],
      'object-curly-spacing': ['error', 'always'],
      'no-multiple-empty-lines': ['error', { max: 1 }],
    },
  },
  {
    files: ['smile-select-frontend/src/**/*.vue'], // Target Vue files
    languageOptions: {
      globals: globals.browser,
      parser: vueParser, // Use vue-eslint-parser for Vue files
      parserOptions: {
        parser: babelParser, // Use Babel as sub-parser for script in .vue files
        ecmaVersion: 2020,
        sourceType: 'module',
        requireConfigFile: false, // Disable the requirement for a Babel config file
      },
    },
    plugins: {
      vue: pluginVue,
    },
    rules: {
      'vue/html-indent': ['error', 2],
      'vue/max-attributes-per-line': [
        'error',
        {
          singleline: 3,
          multiline: 1,
        },
      ],
      'vue/singleline-html-element-content-newline': 'off',
      'vue/multiline-html-element-content-newline': 'off',
      'vue/html-self-closing': [
        'error',
        {
          html: {
            void: 'always',
            normal: 'never',
            component: 'never',
          },
          svg: 'always',
          math: 'always',
        },
      ],
    },
  },
];
