#!/usr/bin/env node
import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import { runOpenApiGenerator } from './openapi-generator.mjs';

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..');
const spec = path.join(root, 'dist', 'openapi', 'ai-platform-v1.bundled.yaml');
const outDir = path.join(root, 'generated', 'java');
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
  'java',
  '-o',
  outDir,
  '--library',
  'feign',
  '-c',
  path.join(root, 'generator', 'java', 'openapi-generator-config.yaml'),
  '--additional-properties',
  `artifactVersion=${version},useJakartaEe=true,openApiNullable=false,dateLibrary=java8,serializationLibrary=jackson,hideGenerationTimestamp=true,useBeanValidation=true,performBeanValidation=true`,
  '--global-property',
  'apiDocs=false,modelDocs=false,apiTests=false,modelTests=false',
  '--skip-validate-spec',
]);

process.exit(status);
