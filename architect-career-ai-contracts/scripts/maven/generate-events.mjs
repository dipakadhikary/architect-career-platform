#!/usr/bin/env node
/**
 * Exports AsyncAPI payload schemas into the Maven target tree for packaging.
 * Source contracts under asyncapi/ are never modified.
 */
import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import yaml from 'js-yaml';

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '../..');
const outRoot = process.env.ACOS_EVENTS_OUT_DIR
  ? path.resolve(process.env.ACOS_EVENTS_OUT_DIR)
  : path.join(root, 'target', 'generated', 'asyncapi');

const sources = [
  ['common', 'asyncapi/common/common-events.yaml'],
  ['knowledge', 'asyncapi/knowledge/knowledge-events.yaml'],
  ['learning', 'asyncapi/learning/learning-events.yaml'],
  ['career', 'asyncapi/career/career-events.yaml'],
  ['portfolio', 'asyncapi/portfolio/portfolio-events.yaml'],
];

fs.rmSync(outRoot, { recursive: true, force: true });
fs.mkdirSync(outRoot, { recursive: true });

const catalog = [];

for (const [domain, relative] of sources) {
  const doc = yaml.load(fs.readFileSync(path.join(root, relative), 'utf8'));
  const schemas = doc?.components?.schemas || {};
  const messages = doc?.components?.messages || {};
  const domainDir = path.join(outRoot, domain, 'schemas');
  fs.mkdirSync(domainDir, { recursive: true });

  for (const [name, schema] of Object.entries(schemas)) {
    const filePath = path.join(domainDir, `${name}.schema.json`);
    fs.writeFileSync(
      filePath,
      `${JSON.stringify(
        {
          $schema: 'https://json-schema.org/draft/2020-12/schema',
          $id: `https://acos.local/schemas/ai/events/${domain}/${name}.json`,
          title: name,
          ...schema,
        },
        null,
        2,
      )}\n`,
      'utf8',
    );
    catalog.push({
      domain,
      kind: 'schema',
      name,
      path: path.relative(root, filePath).replaceAll('\\', '/'),
    });
  }

  const messageDir = path.join(outRoot, domain, 'messages');
  fs.mkdirSync(messageDir, { recursive: true });
  for (const [name, message] of Object.entries(messages)) {
    const filePath = path.join(messageDir, `${name}.json`);
    fs.writeFileSync(filePath, `${JSON.stringify(message, null, 2)}\n`, 'utf8');
    catalog.push({
      domain,
      kind: 'message',
      name,
      path: path.relative(root, filePath).replaceAll('\\', '/'),
    });
  }
}

fs.writeFileSync(
  path.join(outRoot, 'catalog.json'),
  `${JSON.stringify({ generatedAt: new Date().toISOString(), artifacts: catalog }, null, 2)}\n`,
  'utf8',
);

console.log(`[events] Generated ${catalog.length} AsyncAPI artifacts under ${outRoot}`);
