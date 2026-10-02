#!/usr/bin/env node
/**
 * Post-processes generated Java sources for ACOS packaging conventions.
 * - Targets Java 21 compiler settings
 * - Removes unused generator scaffolding (Gradle, Travis, sample CI)
 */
import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const root = path.resolve(__dirname, '..');
const generatedRoot = path.join(root, 'java', 'generated');

function rm(target) {
  fs.rmSync(target, { recursive: true, force: true });
}

const junk = [
  '.travis.yml',
  'build.gradle',
  'build.gradle.kts',
  'settings.gradle',
  'gradle.properties',
  'gradlew',
  'gradlew.bat',
  'gradle',
  'git_push.sh',
  '.github',
  'api',
  'docs',
];

for (const item of junk) {
  rm(path.join(generatedRoot, item));
}

const pomPath = path.join(generatedRoot, 'pom.xml');
if (fs.existsSync(pomPath)) {
  let pom = fs.readFileSync(pomPath, 'utf8');
  pom = pom.replace(/<source>17<\/source>/g, '<source>21</source>');
  pom = pom.replace(/<target>17<\/target>/g, '<target>21</target>');
  // Avoid reactor confusion: generated module is standalone under the parent aggregator.
  if (!pom.includes('architect-career-api-sdk-parent')) {
    // keep standalone; parent reactor references it as a module without requiring parent POM.
  }
  fs.writeFileSync(pomPath, pom, 'utf8');
}

const marker = path.join(generatedRoot, 'GENERATED.md');
fs.writeFileSync(
  marker,
  [
    '# Generated sources',
    '',
    'This directory is produced by OpenAPI Generator.',
    'Do not edit files under `src/` manually.',
    'Re-run `npm run generate:java` after updating `openapi/acos-api.yaml`.',
    '',
  ].join('\n'),
  'utf8',
);

console.log('Post-processed Java generated module');
