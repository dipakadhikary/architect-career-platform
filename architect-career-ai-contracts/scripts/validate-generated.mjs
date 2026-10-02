#!/usr/bin/env node
import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..');

const requiredPaths = [
  'dist/openapi/ai-platform-v1.bundled.yaml',
  'dist/asyncapi/ai-platform-events-v1.yaml',
  'generated/java',
  'generated/python',
  'generated/typescript',
  'generated/asyncapi/catalog.json',
];

let failed = false;
for (const relative of requiredPaths) {
  const full = path.join(root, relative);
  if (!fs.existsSync(full)) {
    console.error(`Missing generated artifact: ${relative}`);
    failed = true;
    continue;
  }
  console.log(`OK ${relative}`);
}

const javaModels = path.join(root, 'generated', 'java', 'src', 'main', 'java');
const pythonPkg = path.join(root, 'generated', 'python');
const tsModels = path.join(root, 'generated', 'typescript');

if (fs.existsSync(javaModels)) {
  const count = countFiles(javaModels, '.java');
  console.log(`Java sources: ${count}`);
  if (count < 1) {
    failed = true;
  }
}
if (fs.existsSync(pythonPkg)) {
  const count = countFiles(pythonPkg, '.py');
  console.log(`Python sources: ${count}`);
  if (count < 1) {
    failed = true;
  }
}
if (fs.existsSync(tsModels)) {
  const count = countFiles(tsModels, '.ts');
  console.log(`TypeScript sources: ${count}`);
  if (count < 1) {
    failed = true;
  }
}

process.exit(failed ? 1 : 0);

function countFiles(dir, extension) {
  let total = 0;
  for (const entry of fs.readdirSync(dir, { withFileTypes: true })) {
    const full = path.join(dir, entry.name);
    if (entry.isDirectory()) {
      total += countFiles(full, extension);
    } else if (entry.name.endsWith(extension)) {
      total += 1;
    }
  }
  return total;
}
