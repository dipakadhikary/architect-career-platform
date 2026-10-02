#!/usr/bin/env node
/**
 * Normalizes the downloaded OpenAPI document into the canonical YAML snapshot.
 *
 * - Converts JSON → YAML when acos-api.json is present
 * - Leaves acos-api.yaml untouched when it is the only artifact
 * - Does not alter API semantics or backend version metadata
 */
import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import yaml from 'js-yaml';

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const root = path.resolve(__dirname, '..');
const openapiDir = path.join(root, 'openapi');
const jsonPath = path.join(openapiDir, 'acos-api.json');
const yamlPath = path.join(openapiDir, 'acos-api.yaml');

fs.mkdirSync(openapiDir, { recursive: true });

function loadDocument() {
  if (fs.existsSync(jsonPath)) {
    return JSON.parse(fs.readFileSync(jsonPath, 'utf8'));
  }
  if (fs.existsSync(yamlPath)) {
    return yaml.load(fs.readFileSync(yamlPath, 'utf8'));
  }
  throw new Error(
    'No OpenAPI document found. Run npm run openapi:download first, or place openapi/acos-api.yaml.',
  );
}

const document = loadDocument();

if (!document.openapi) {
  throw new Error('Invalid OpenAPI document: missing openapi version field.');
}
if (!document.info?.title) {
  throw new Error('Invalid OpenAPI document: missing info.title.');
}

const yamlText = yaml.dump(document, {
  lineWidth: 120,
  noRefs: true,
  sortingKeys: false,
});

fs.writeFileSync(yamlPath, yamlText, 'utf8');
fs.writeFileSync(jsonPath, `${JSON.stringify(document, null, 2)}\n`, 'utf8');

console.log(`Normalized OpenAPI ${document.openapi} — ${document.info.title}`);
console.log(`  version metadata: ${document.info.version ?? '(none)'}`);
console.log(`  paths: ${Object.keys(document.paths || {}).length}`);
console.log(`  wrote ${path.relative(root, yamlPath)}`);
console.log(`  wrote ${path.relative(root, jsonPath)}`);
