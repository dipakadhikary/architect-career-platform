#!/usr/bin/env node
import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import { runOpenApiGenerator } from './openapi-generator.mjs';

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..');
const spec = path.join(root, 'dist', 'openapi', 'ai-platform-v1.bundled.yaml');
const outDir = path.join(root, 'generated', 'python');
const version = fs.readFileSync(path.join(root, 'VERSION'), 'utf8').trim();

if (!fs.existsSync(spec)) {
  console.error('Bundled OpenAPI missing. Run npm run bundle:openapi first.');
  process.exit(1);
}

fs.rmSync(outDir, { recursive: true, force: true });
fs.mkdirSync(outDir, { recursive: true });

const status = runOpenApiGenerator([
  'generate',
  '-i',
  spec,
  '-g',
  'python',
  '-o',
  outDir,
  '-c',
  path.join(root, 'generator', 'python', 'openapi-generator-config.yaml'),
  '--additional-properties',
  `packageName=acos_ai_contracts,projectName=acos-ai-contracts,packageVersion=${version}`,
  '--global-property',
  'apiDocs=false,modelDocs=false,apiTests=false,modelTests=false',
  '--skip-validate-spec',
]);

process.exit(status);
