#!/usr/bin/env node
/**
 * Patches known OpenAPI Generator defects in free-form oneOf Java models
 * (e.g. Objects.hash(, additionalProperties)) without touching contract sources.
 */
import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '../..');
const javaRoot =
  process.env.ACOS_GENERATED_JAVA || path.join(root, 'target', 'generated', 'java');
const srcRoot = path.join(javaRoot, 'src', 'main', 'java');

if (!fs.existsSync(srcRoot)) {
  console.log('[java-patch] No Java sources to patch');
  process.exit(0);
}

let patched = 0;
for (const file of walk(srcRoot)) {
  if (!file.endsWith('.java')) {
    continue;
  }
  const original = fs.readFileSync(file, 'utf8');
  let next = original;
  next = next.replace(/Objects\.hash\(\s*,/g, 'Objects.hash(');
  next = next.replace(/Arrays\.hashCode\(\s*,/g, 'Arrays.hashCode(');
  if (next !== original) {
    fs.writeFileSync(file, next, 'utf8');
    patched += 1;
    console.log(`[java-patch] Fixed ${path.relative(javaRoot, file)}`);
  }
}

console.log(`[java-patch] Patched ${patched} file(s)`);

function* walk(dir) {
  for (const entry of fs.readdirSync(dir, { withFileTypes: true })) {
    const full = path.join(dir, entry.name);
    if (entry.isDirectory()) {
      yield* walk(full);
    } else {
      yield full;
    }
  }
}
