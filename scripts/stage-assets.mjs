#!/usr/bin/env node
// Stage gallery assets into the build output directory.
//
// Zero staged assets is a VALID build. A missing or empty root is reported and
// the build continues: the galleries render empty and their count sentences
// read "0", which is honest. Failing here would make the site unbuildable on
// every machine but one, which is the situation this replaces.

import { existsSync, mkdirSync, readdirSync, rmSync, statSync, linkSync, copyFileSync } from 'node:fs';
import { dirname, join, relative } from 'node:path';

import {
  graphicsRoot,
  musicRoot,
  siteOutDir,
  assetLimit,
  graphicsExtensions,
  musicExtensions,
} from './asset-config.mjs';

function collect(root, pattern) {
  if (!root || !existsSync(root)) return { found: [], reason: 'root does not exist' };
  let stats;
  try {
    stats = statSync(root);
  } catch {
    return { found: [], reason: 'root is not readable' };
  }
  if (!stats.isDirectory()) return { found: [], reason: 'root is not a directory' };

  const found = [];
  const walk = (dir) => {
    let entries;
    try {
      entries = readdirSync(dir, { withFileTypes: true });
    } catch {
      return;
    }
    for (const entry of entries.sort((a, b) => a.name.localeCompare(b.name))) {
      const full = join(dir, entry.name);
      if (entry.isDirectory()) walk(full);
      else if (entry.isFile() && pattern.test(entry.name)) found.push(full);
    }
  };
  walk(root);
  found.sort((a, b) => a.localeCompare(b));
  return { found, reason: null };
}

function stage(root, pattern, outSubdir) {
  const target = join(siteOutDir, outSubdir);
  rmSync(target, { recursive: true, force: true });
  mkdirSync(target, { recursive: true });

  const { found, reason } = collect(root, pattern);
  const selected = Number.isFinite(assetLimit) && assetLimit > 0 ? found.slice(0, assetLimit) : found;

  for (const source of selected) {
    const destination = join(target, relative(root, source));
    mkdirSync(dirname(destination), { recursive: true });
    try {
      // A hard link keeps a large media library out of the copy budget while
      // still leaving a real file inside the build output.
      linkSync(source, destination);
    } catch {
      copyFileSync(source, destination);
    }
  }

  return { root, reason, available: found.length, staged: selected.length, target };
}

const results = [
  stage(graphicsRoot, graphicsExtensions, 'graphics'),
  stage(musicRoot, musicExtensions, 'music'),
];

for (const result of results) {
  const detail = result.reason ? ` (${result.reason})` : '';
  console.log(
    `staged ${result.staged}/${result.available} from ${result.root}${detail} -> ${result.target}`,
  );
}

if (results.every((result) => result.staged === 0)) {
  console.log(
    'No assets staged. This is a valid build: the galleries render empty and their counts read 0.\n' +
      'Set OPENHAX_GRAPHICS_ROOT / OPENHAX_MUSIC_ROOT to stage a real library.',
  );
}
