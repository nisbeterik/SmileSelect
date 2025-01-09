export default [
  {
    files: ['**/*.js', '**/*.vue'], // Specify files to lint
    ignores: [
      '**/node_modules/**',
      '**/dist/**',
      '**/public/**',
      '**/backend/services/**/target/**',
      '**/database-scripts/**',
      '**/*.png',
      '**/*.jpg',
      '**/*.ico',
      '**/*.sql',
      '**/docker-compose.yml',
      '**/README.md',
      '**/coverage/**',
      '**/*.lock',
    ],    
    languageOptions: {
      ecmaVersion: 2021,
      sourceType: 'module',
      parser: '@babel/eslint-parser',
    },
    plugins: {
      vue: require('eslint-plugin-vue'),
    },
    rules: {
      'no-console': 'off',
      'no-debugger': 'warn',
    },
  },
  {
    files: ['**/*.vue'], // Specific rules for Vue files
    processor: 'vue/.vue',
    plugins: {
      vue: require('eslint-plugin-vue'),
    },
    rules: {
      'vue/no-multiple-template-root': 'off', // Example Vue-specific rule
    },
  },
];
