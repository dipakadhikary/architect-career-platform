#!/usr/bin/env node
/**
 * Generates the TypeScript Axios SDK into typescript/generated.
 * Generated sources must not be edited by hand — extend typescript/custom instead.
 */
import { spawnSync } from 'node:child_process';
import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const root = path.resolve(__dirname, '..');
const outDir = path.join(root, 'typescript', 'generated');
const spec = path.join(root, 'openapi', 'acos-api.yaml');

if (!fs.existsSync(spec)) {
  console.error('Missing openapi/acos-api.yaml. Run npm run openapi:normalize first.');
  process.exit(1);
}

fs.rmSync(outDir, { recursive: true, force: true });
fs.mkdirSync(outDir, { recursive: true });

const version = fs.readFileSync(path.join(root, 'VERSION'), 'utf8').trim();

const args = [
  'generate',
  '-i',
  spec,
  '-g',
  'typescript-axios',
  '-o',
  outDir,
  '--additional-properties',
  [
    'npmName=@acos/api-sdk',
    `npmVersion=${version}`,
    'supportsES6=true',
    'withSeparateModelsAndApi=true',
    'apiPackage=api',
    'modelPackage=models',
    'enumPropertyNaming=UPPERCASE',
    'stringEnums=true',
    'useSingleRequestParameter=true',
    'withInterfaces=true',
    'usePromise=true',
  ].join(','),
  '--global-property',
  'apiDocs=false,modelDocs=false,apiTests=false,modelTests=false',
  '--skip-validate-spec',
];

const result = spawnSync('npx', ['openapi-generator-cli', ...args], {
  cwd: root,
  stdio: 'inherit',
  shell: true,
});

if (result.status !== 0) {
  process.exit(result.status ?? 1);
}

fs.writeFileSync(
  path.join(outDir, '.openapi-generator-ignore'),
  ['README.md', 'git_push.sh', '.gitignore', '.npmignore', 'package.json', 'tsconfig.json'].join(
    '\n',
  ) + '\n',
  'utf8',
);

for (const relative of ['common.ts', 'base.ts']) {
  const filePath = path.join(outDir, relative);
  if (!fs.existsSync(filePath)) {
    continue;
  }
  let original = fs.readFileSync(filePath, 'utf8');
  if (!original.startsWith('// @ts-nocheck')) {
    original = `// @ts-nocheck\n${original}`;
  }
  if (relative === 'common.ts') {
    original = original.replace(
      /export const createRequestFunction = function \(([^)]*)\) \{/,
      'export const createRequestFunction = function ($1): any {',
    );
  }
  fs.writeFileSync(filePath, original, 'utf8');
}

fs.writeFileSync(
  path.join(outDir, 'GENERATED.md'),
  [
    '# Generated sources',
    '',
    'This directory is produced by OpenAPI Generator (`typescript-axios`).',
    'Do not edit these files manually.',
    'Re-run `npm run generate:typescript` after updating `openapi/acos-api.yaml`.',
    '',
  ].join('\n'),
  'utf8',
);

console.log(`TypeScript SDK generated at ${path.relative(root, outDir)}`);
