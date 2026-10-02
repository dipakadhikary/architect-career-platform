#!/usr/bin/env node
/**
 * Packages language SDK zip artifacts into target/artifacts.
 */
import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import { spawnSync } from 'node:child_process';

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '../..');
const artifactsDir = process.env.ACOS_ARTIFACTS_DIR
  ? path.resolve(process.env.ACOS_ARTIFACTS_DIR)
  : path.join(root, 'target', 'artifacts');
const pythonDir = process.env.ACOS_GENERATED_PYTHON
  ? path.resolve(process.env.ACOS_GENERATED_PYTHON)
  : path.join(root, 'target', 'generated', 'python');
const typescriptDir = process.env.ACOS_GENERATED_TYPESCRIPT
  ? path.resolve(process.env.ACOS_GENERATED_TYPESCRIPT)
  : path.join(root, 'target', 'generated', 'typescript');
const javaJar =
  process.env.ACOS_JAVA_JAR || path.join(root, 'target', 'career-ai-java-sdk.jar');

const packagePython = process.env.ACOS_PACKAGE_PYTHON !== 'false';
const packageTypescript = process.env.ACOS_PACKAGE_TYPESCRIPT !== 'false';
const packageJava = process.env.ACOS_PACKAGE_JAVA !== 'false';

fs.mkdirSync(artifactsDir, { recursive: true });

if (packageJava) {
  if (!fs.existsSync(javaJar)) {
    throw new Error(`Java SDK jar missing: ${javaJar}`);
  }
  fs.copyFileSync(javaJar, path.join(artifactsDir, 'career-ai-java-sdk.jar'));
  console.log(`[artifacts] career-ai-java-sdk.jar`);
}

if (packagePython) {
  createZip(pythonDir, path.join(artifactsDir, 'career-ai-python-sdk.zip'));
  console.log(`[artifacts] career-ai-python-sdk.zip`);
}

if (packageTypescript) {
  createZip(typescriptDir, path.join(artifactsDir, 'career-ai-typescript-sdk.zip'));
  console.log(`[artifacts] career-ai-typescript-sdk.zip`);
}

console.log(`[artifacts] Staged in ${artifactsDir}`);

function createZip(sourceDir, zipFile) {
  if (!fs.existsSync(sourceDir)) {
    throw new Error(`SDK source directory missing: ${sourceDir}`);
  }
  if (fs.existsSync(zipFile)) {
    fs.rmSync(zipFile, { force: true });
  }

  const staging = `${zipFile}.staging`;
  fs.rmSync(staging, { recursive: true, force: true });
  simpleCopy(sourceDir, staging);

  try {
    // Prefer JDK jar — reliable on Windows vs Compress-Archive file locks
    const jar = spawnSync(
      'jar',
      ['--create', '--file', zipFile, '-C', staging, '.'],
      { stdio: 'inherit', shell: process.platform === 'win32' },
    );
    if ((jar.status ?? 1) !== 0 || !fs.existsSync(zipFile) || fs.statSync(zipFile).size === 0) {
      // Fallback: zip CLI (Linux/macOS CI)
      const zip = spawnSync('zip', ['-r', zipFile, '.'], {
        cwd: staging,
        stdio: 'inherit',
      });
      if ((zip.status ?? 1) !== 0 || !fs.existsSync(zipFile) || fs.statSync(zipFile).size === 0) {
        throw new Error(`Failed to create ${zipFile}`);
      }
    }
  } finally {
    fs.rmSync(staging, { recursive: true, force: true });
  }
}

function simpleCopy(src, dest, relative = '') {
  fs.mkdirSync(dest, { recursive: true });
  for (const entry of fs.readdirSync(src, { withFileTypes: true })) {
    const rel = relative ? `${relative}/${entry.name}` : entry.name;
    if (shouldSkip(rel, entry.name)) {
      continue;
    }
    const from = path.join(src, entry.name);
    const to = path.join(dest, entry.name);
    if (entry.isDirectory()) {
      simpleCopy(from, to, rel);
    } else {
      fs.copyFileSync(from, to);
    }
  }
}

function shouldSkip(relativePath, name) {
  const skipNames = new Set([
    'node_modules',
    'dist',
    '__pycache__',
    '.npm',
    '.pytest_cache',
    '.openapi-generator',
  ]);
  if (skipNames.has(name) || name.endsWith('.pyc')) {
    return true;
  }
  return relativePath.split(/[\\/]/).some((part) => skipNames.has(part));
}
