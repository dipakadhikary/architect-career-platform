#!/usr/bin/env node
/**
 * Generates the Java SDK.
 *
 * Default library: Spring RestClient (java/generated).
 * Optional Feign library: set ACOS_JAVA_CLIENT_LIBRARY=feign (java/generated-feign).
 */
import { spawnSync } from 'node:child_process';
import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const root = path.resolve(__dirname, '..');
const spec = path.join(root, 'openapi', 'acos-api.yaml');

if (!fs.existsSync(spec)) {
  console.error('Missing openapi/acos-api.yaml. Run npm run openapi:normalize first.');
  process.exit(1);
}

const library = (process.env.ACOS_JAVA_CLIENT_LIBRARY || 'restclient').toLowerCase();
const supported = new Set(['restclient', 'feign']);
if (!supported.has(library)) {
  console.error(
    `Unsupported ACOS_JAVA_CLIENT_LIBRARY=${library}. Use one of: ${[...supported].join(', ')}`,
  );
  process.exit(1);
}

const version = fs.readFileSync(path.join(root, 'VERSION'), 'utf8').trim();
const isFeign = library === 'feign';
const outDir = path.join(root, 'java', isFeign ? 'generated-feign' : 'generated');
const apiPackage = isFeign ? 'com.acos.sdk.generated.feign.api' : 'com.acos.sdk.generated.api';
const modelPackage = isFeign
  ? 'com.acos.sdk.generated.feign.model'
  : 'com.acos.sdk.generated.model';
const invokerPackage = isFeign ? 'com.acos.sdk.generated.feign' : 'com.acos.sdk.generated';
const artifactId = isFeign ? 'architect-career-api-sdk-feign' : 'architect-career-api-sdk';

fs.rmSync(outDir, { recursive: true, force: true });
fs.mkdirSync(outDir, { recursive: true });

const args = [
  'generate',
  '-i',
  spec,
  '-g',
  'java',
  '-o',
  outDir,
  '--library',
  library,
  '--additional-properties',
  [
    `artifactId=${artifactId}`,
    'groupId=com.acos',
    `artifactVersion=${version}`,
    `apiPackage=${apiPackage}`,
    `modelPackage=${modelPackage}`,
    `invokerPackage=${invokerPackage}`,
    'dateLibrary=java8',
    'java8=true',
    'useJakartaEe=true',
    'openApiNullable=false',
    'serializationLibrary=jackson',
    'hideGenerationTimestamp=true',
    'disallowAdditionalPropertiesIfNotPresent=false',
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

if (!isFeign) {
  const post = spawnSync('node', [path.join(__dirname, 'postprocess-java.mjs')], {
    cwd: root,
    stdio: 'inherit',
    shell: true,
  });
  if (post.status !== 0) {
    process.exit(post.status ?? 1);
  }
}

console.log(`Java SDK (${library}) generated at ${path.relative(root, outDir)}`);
