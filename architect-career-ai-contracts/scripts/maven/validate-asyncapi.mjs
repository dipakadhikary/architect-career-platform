#!/usr/bin/env node
/**
 * Maven validate phase: AsyncAPI 3.x structural validation.
 * Does not modify contract sources.
 */
import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import yaml from 'js-yaml';

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '../..');

const specs = [
  'asyncapi/ai-platform-events-v1.yaml',
  'asyncapi/common/common-events.yaml',
  'asyncapi/knowledge/knowledge-events.yaml',
  'asyncapi/learning/learning-events.yaml',
  'asyncapi/career/career-events.yaml',
  'asyncapi/portfolio/portfolio-events.yaml',
];

let failed = false;

for (const relative of specs) {
  const filePath = path.join(root, relative);
  process.stdout.write(`[asyncapi] Validating ${relative} ... `);
  try {
    if (!fs.existsSync(filePath)) {
      throw new Error('File not found');
    }
    const doc = yaml.load(fs.readFileSync(filePath, 'utf8'));
    if (!doc || typeof doc !== 'object') {
      throw new Error('Document is empty');
    }
    if (!String(doc.asyncapi || '').startsWith('3.')) {
      throw new Error(`Expected asyncapi 3.x, found ${doc.asyncapi}`);
    }
    if (!doc.info?.title || !doc.info?.version) {
      throw new Error('Missing info.title or info.version');
    }
    if (doc.channels) {
      for (const [channelName, channel] of Object.entries(doc.channels)) {
        if (channel?.$ref) {
          continue;
        }
        if (!channel?.address) {
          throw new Error(`Channel ${channelName} missing address`);
        }
        if (!channel.messages || Object.keys(channel.messages).length < 1) {
          throw new Error(`Channel ${channelName} missing messages`);
        }
      }
    }
    if (doc.components?.messages) {
      for (const [messageName, message] of Object.entries(doc.components.messages)) {
        if (!message.payload) {
          throw new Error(`Message ${messageName} missing payload`);
        }
        if (!message.headers) {
          throw new Error(`Message ${messageName} missing headers`);
        }
      }
    }
    console.log('ok');
  } catch (error) {
    failed = true;
    console.log('FAILED');
    console.error(`  ${error.message}`);
  }
}

if (failed) {
  console.error('[asyncapi] Validation failed. Fix contract issues before generation.');
  process.exit(1);
}

console.log('[asyncapi] All AsyncAPI specifications are valid.');
