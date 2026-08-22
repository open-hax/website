#!/usr/bin/env node
// Copy the committed static files into the build output directory.
//
// public/graphics and public/music are deliberately skipped: they are staged
// by scripts/stage-assets.mjs from roots resolved at build time, and they are
// gitignored so the old committed absolute symlinks cannot come back.

import { cpSync, existsSync, mkdirSync, readdirSync } from 'node:fs';
import { join } from 'node:path';

import { repoRoot, siteOutDir } from './asset-config.mjs';

const publicDir = join(repoRoot, 'public');
const skip = new Set(['graphics', 'music']);

mkdirSync(siteOutDir, { recursive: true });

let copied = 0;
if (existsSync(publicDir)) {
  for (const entry of readdirSync(publicDir, { withFileTypes: true })) {
    if (skip.has(entry.name)) continue;
    cpSync(join(publicDir, entry.name), join(siteOutDir, entry.name), {
      recursive: true,
      dereference: true,
    });
    copied += 1;
  }
}

console.log(`copied ${copied} static entries from public/ -> ${siteOutDir}`);
