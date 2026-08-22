#!/usr/bin/env node
// Produce the ONE directory that ships: dist/site.
//
// Before this, `pnpm build` emitted dist/cljs/app.js and dist/app.css while
// index.html sat at the repo root and the dev server merged three docroots
// ["." "dist" "public"]. No single directory could be handed to a static host,
// which is finding #1 against services#19: the thing the developer saw and the
// thing a deployment could serve were different sites.
//
// Order matters. Assets are staged first, the gallery manifest is generated
// from what was staged second, and only then is the application compiled — so
// the compiled manifest can never claim an asset the output does not contain.

import { spawnSync } from 'node:child_process';
import { mkdirSync, rmSync } from 'node:fs';
import { join, relative } from 'node:path';

import { repoRoot, siteOutDir } from './asset-config.mjs';

function run(command, args) {
  console.log(`\n$ ${command} ${args.join(' ')}`);
  const result = spawnSync(command, args, { cwd: repoRoot, stdio: 'inherit' });
  if (result.status !== 0) {
    console.error(`\nFAILED: ${command} ${args.join(' ')}`);
    process.exit(result.status ?? 1);
  }
}

// 1. A build output directory is not incremental state. Start empty so a
//    removed asset or a renamed locale cannot survive into the artifact.
rmSync(siteOutDir, { recursive: true, force: true });
mkdirSync(siteOutDir, { recursive: true });

// 2. Stage the galleries from roots resolved at build time.
run('node', ['scripts/stage-assets.mjs']);

// 3. Regenerate the gallery manifest from what was actually staged.
run('node', ['scripts/scan-assets.mjs']);

// 4. Stylesheet.
run('pnpm', [
  'exec',
  'tailwindcss',
  '-c',
  'tailwind.config.ts',
  '-i',
  'src/index.css',
  '-o',
  join(relative(repoRoot, siteOutDir), 'app.css'),
  '--minify',
]);

// 5. Application bundle.
run('pnpm', ['exec', 'shadow-cljs', 'release', 'app']);

// 6. One HTML shell per locale, generated from the locale dictionaries.
run('pnpm', ['exec', 'shadow-cljs', 'release', 'html']);
run('node', ['target/build/html.cjs', relative(repoRoot, siteOutDir)]);

// 7. Committed static files.
run('node', ['scripts/copy-static.mjs']);

// 8. Prove the claim rather than asserting it.
run('node', ['scripts/verify-dist.mjs']);

console.log(`\nBuild output: ${siteOutDir}`);
