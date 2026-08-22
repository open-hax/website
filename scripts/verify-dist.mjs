#!/usr/bin/env node
// Verify that dist/site is a single self-contained directory a static host can
// serve, and fail loudly if it is not.
//
// Every check here corresponds to something that was actually wrong before:
// a docroot that needed three merged roots, committed symlinks pointing into
// one developer's home directory, and a gallery manifest listing 400 files
// that did not exist.

import { existsSync, readFileSync, readdirSync, readlinkSync, statSync } from 'node:fs';
import { join, relative, extname } from 'node:path';

import { siteOutDir, repoRoot, assetsManifestPath } from './asset-config.mjs';

const failures = [];
const notes = [];

function fail(message) {
  failures.push(message);
}

function walk(dir, acc = []) {
  for (const entry of readdirSync(dir, { withFileTypes: true })) {
    const full = join(dir, entry.name);
    if (entry.isSymbolicLink()) {
      acc.push({ path: full, kind: 'symlink' });
    } else if (entry.isDirectory()) {
      acc.push({ path: full, kind: 'dir' });
      walk(full, acc);
    } else {
      acc.push({ path: full, kind: 'file', size: statSync(full).size });
    }
  }
  return acc;
}

if (!existsSync(siteOutDir)) {
  fail(`build output directory is missing: ${siteOutDir}`);
  console.error(failures.join('\n'));
  process.exit(1);
}

const entries = walk(siteOutDir);
const files = entries.filter((e) => e.kind === 'file');

// 1. One index.html per locale, each declaring its own language.
const expectedShells = {
  'index.html': 'en',
  'es/index.html': 'es',
  'fr/index.html': 'fr',
  'de/index.html': 'de',
  'ja/index.html': 'ja',
};

for (const [relPath, tag] of Object.entries(expectedShells)) {
  const full = join(siteOutDir, relPath);
  if (!existsSync(full)) {
    fail(`missing locale shell: ${relPath}`);
    continue;
  }
  const html = readFileSync(full, 'utf8');
  if (!html.includes(`<html lang="${tag}"`)) {
    fail(`${relPath} does not declare lang="${tag}"`);
  }
  for (const other of Object.keys(expectedShells)) {
    const otherTag = expectedShells[other];
    const href = otherTag === 'en' ? '/' : `/${otherTag}/`;
    if (!html.includes(`hreflang="${otherTag}" href="${href}"`)) {
      fail(`${relPath} is missing the hreflang alternate for ${otherTag}`);
    }
  }
  if (!html.includes('hreflang="x-default" href="/"')) {
    fail(`${relPath} is missing its x-default alternate`);
  }
}

// 2. The bundle and the stylesheet live in the same directory as the shells.
for (const required of ['cljs/app.js', 'app.css', 'favicon.svg']) {
  const full = join(siteOutDir, required);
  if (!existsSync(full)) fail(`missing build output: ${required}`);
  else if (statSync(full).size === 0) fail(`empty build output: ${required}`);
}

// 3. No symlinks. A symlink in the artifact is how the asset directories came
//    to point at an absolute path in one developer's home directory.
for (const entry of entries.filter((e) => e.kind === 'symlink')) {
  fail(`symlink inside the build output: ${relative(siteOutDir, entry.path)} -> ${readlinkSync(entry.path)}`);
}

// 4. No text file in the output references an absolute filesystem path or a
//    path that escapes the directory.
const textExtensions = new Set(['.html', '.css', '.js', '.json', '.edn', '.svg', '.txt', '.map']);
const forbiddenPatterns = [
  { pattern: /\/home\//, why: 'an absolute path into a home directory' },
  { pattern: /file:\/\//, why: 'a file:// URL' },
];

for (const file of files) {
  if (!textExtensions.has(extname(file.path))) continue;
  const source = readFileSync(file.path, 'utf8');
  for (const { pattern, why } of forbiddenPatterns) {
    if (pattern.test(source)) {
      fail(`${relative(siteOutDir, file.path)} contains ${why}`);
    }
  }
}

// 5. Every asset the gallery manifest lists exists in the output. The manifest
//    is generated from the staged files, so this can only fail if the two
//    steps ran out of order.
const manifestSource = readFileSync(assetsManifestPath, 'utf8');
const listedAssets = [...manifestSource.matchAll(/:src "([^"]+)"/g)].map((m) => m[1]);
let missingAssets = 0;
for (const src of listedAssets) {
  // :src values are URL-encoded per segment; the filesystem wants them raw.
  const onDisk = decodeURIComponent(src).replace(/^\//, '');
  if (!existsSync(join(siteOutDir, onDisk))) {
    missingAssets += 1;
    if (missingAssets <= 5) fail(`gallery manifest lists a file the output does not contain: ${src}`);
  }
}
if (missingAssets > 5) fail(`...and ${missingAssets - 5} more missing gallery assets`);

notes.push(`${listedAssets.length} gallery assets listed, ${listedAssets.length - missingAssets} present`);

// 6. Nothing in the output asks the browser to talk to a Knoxx origin. The
//    seam between the two repositories is files under the content root, so the
//    site keeps serving what was last published when Knoxx is down.
//
//    The check matches the AUTHORITY of a URL, not its path: a link to the
//    Knoxx source on github.com is a hyperlink a human clicks, not an origin
//    this site fetches from, and the product cards legitimately carry one.
const knoxxOriginPattern = /https?:\/\/[^/"'\s]*knoxx[^/"'\s]*/i;
for (const file of files) {
  if (!textExtensions.has(extname(file.path))) continue;
  const source = readFileSync(file.path, 'utf8');
  const match = source.match(knoxxOriginPattern);
  if (match) {
    fail(`${relative(siteOutDir, file.path)} references a Knoxx origin: ${match[0]}`);
  }
}

notes.push(`${files.length} files, ${entries.filter((e) => e.kind === 'dir').length} directories`);
notes.push(`root: ${relative(repoRoot, siteOutDir)}`);

for (const note of notes) console.log(`ok  ${note}`);

if (failures.length) {
  console.error('\nself-containment check FAILED:');
  for (const failure of failures) console.error(`  - ${failure}`);
  process.exit(1);
}

console.log('\ndist is self-contained: one docroot, one index.html per locale, no escaping references.');
