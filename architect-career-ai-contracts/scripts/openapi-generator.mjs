#!/usr/bin/env node
/**
 * Ensures the OpenAPI Generator CLI JAR is available locally.
 */
import { spawnSync } from 'node:child_process';
import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..');
const version = '7.12.0';
const toolsDir = path.join(root, '.tools');
const jarPath = path.join(toolsDir, `openapi-generator-cli-${version}.jar`);
const url = `https://repo1.maven.org/maven2/org/openapitools/openapi-generator-cli/${version}/openapi-generator-cli-${version}.jar`;

export function ensureOpenApiGeneratorJar() {
  if (fs.existsSync(jarPath)) {
    return jarPath;
  }
  fs.mkdirSync(toolsDir, { recursive: true });
  console.log(`Downloading OpenAPI Generator ${version} ...`);
  const result = spawnSync('curl', ['-fsSL', url, '-o', jarPath], {
    cwd: root,
    stdio: 'inherit',
    shell: true,
  });
  if ((result.status ?? 1) !== 0 || !fs.existsSync(jarPath)) {
    // Fallback for Windows without curl semantics
    const ps = spawnSync(
      'powershell',
      ['-NoProfile', '-Command', `Invoke-WebRequest -Uri '${url}' -OutFile '${jarPath}'`],
      { cwd: root, stdio: 'inherit', shell: true },
    );
    if ((ps.status ?? 1) !== 0 || !fs.existsSync(jarPath)) {
      throw new Error(`Unable to download OpenAPI Generator JAR from ${url}`);
    }
  }
  return jarPath;
}

export function runOpenApiGenerator(args) {
  const jar = ensureOpenApiGeneratorJar();
  const normalizedArgs = args.map((arg, index) => {
    if (index > 0 && args[index - 1] === '-i') {
      // Use forward-slash absolute paths; composed bundle has no external $refs.
      return path.resolve(arg).replaceAll('\\', '/');
    }
    return arg;
  });
  const result = spawnSync('java', ['-jar', jar, ...normalizedArgs], {
    cwd: root,
    stdio: 'inherit',
    shell: true,
  });
  return result.status ?? 1;
}

if (process.argv[1] && path.resolve(process.argv[1]) === fileURLToPath(import.meta.url)) {
  try {
    console.log(ensureOpenApiGeneratorJar());
  } catch (error) {
    console.error(error.message);
    process.exit(1);
  }
}
