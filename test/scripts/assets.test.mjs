import { test } from 'node:test';
import assert from 'node:assert/strict';
import { mkdtempSync, mkdirSync, cpSync, writeFileSync, readFileSync, readdirSync, symlinkSync, rmSync } from 'node:fs';
import { tmpdir } from 'node:os';
import { join } from 'node:path';
import { spawnSync } from 'node:child_process';
import { repoRoot } from '../../scripts/asset-config.mjs';

test('a fresh checkout stages its committed collection with stable, encoded URLs', () => {
  const root = mkdtempSync(join(tmpdir(), 'openhax-assets-'));
  try {
    cpSync(join(repoRoot, 'scripts'), join(root, 'scripts'), { recursive: true });
    const art = join(root, 'assets/graphics');
    mkdirSync(join(art, 'a collection/.unrenderable'), { recursive: true });
    writeFileSync(join(art, 'a collection/one (1).svg'), '<svg xmlns="http://www.w3.org/2000/svg"/>');
    writeFileSync(join(art, 'a collection/.unrenderable/broken.svg'), '<broken');
    writeFileSync(join(art, 'two.svg'), '<svg xmlns="http://www.w3.org/2000/svg"/>');
    symlinkSync(join(art, 'two.svg'), join(art, 'linked.svg'));
    writeFileSync(join(root, 'assets/featured-graphics.json'), '["a collection/one (1).svg"]');
    const env = { ...process.env };
    for (const key of Object.keys(env)) if (key.startsWith('OPENHAX_')) delete env[key];
    const run = (script, overrides = {}) => {
      const result = spawnSync(process.execPath, [join(root, 'scripts', script)], {
        cwd: root, env: { ...env, ...overrides }, encoding: 'utf8',
      });
      assert.ifError(result.error);
      assert.equal(result.status, 0, result.stderr);
      return result.stdout;
    };
    assert.match(run('stage-assets.mjs'), /staged 2\/2/);
    assert.deepEqual(readdirSync(join(root, 'dist/site/graphics')), ['a collection', 'two.svg']);
    run('scan-assets.mjs');
    const manifest = () => readFileSync(join(root, 'src/cljc/open_hax/website/data/assets.cljc'), 'utf8');
    const first = manifest();
    assert.match(first, /:src "\/graphics\/a%20collection\/one%20\(1\)\.svg" :featured\? true/);
    assert.doesNotMatch(first, /broken\.svg|linked\.svg|\/home\//);
    assert.equal([...first.matchAll(/:src "/g)].length, 2);
    run('stage-assets.mjs');
    run('scan-assets.mjs');
    assert.equal(manifest(), first);
    run('stage-assets.mjs', { OPENHAX_GRAPHICS_ROOT: join(root, 'absent') });
    run('scan-assets.mjs');
    assert.match(manifest(), /\(def graphics\s+\[\]\)/);
  } finally {
    rmSync(root, { recursive: true, force: true });
  }
});

test('the build fails when its child process reports a spawn error with status zero', () => {
  const root = mkdtempSync(join(tmpdir(), 'openhax-build-failure-'));
  try {
    cpSync(join(repoRoot, 'scripts'), join(root, 'scripts'), { recursive: true });
    const mock = join(root, 'spawn-error.mjs');
    writeFileSync(mock, `import childProcess from 'node:child_process';
import { syncBuiltinESMExports } from 'node:module';
childProcess.spawnSync = () => ({status: 0, error: new Error('spawn denied')});
syncBuiltinESMExports();
`);
    const result = spawnSync(process.execPath,
      ['--import', mock, join(root, 'scripts/build-site.mjs')],
      { cwd: root, encoding: 'utf8' });
    assert.ifError(result.error);
    assert.equal(result.status, 1);
    assert.match(result.stderr, /FAILED: node scripts\/stage-assets.mjs[\s\S]*spawn denied/);
    assert.doesNotMatch(result.stdout, /Build output:/);
  } finally {
    rmSync(root, { recursive: true, force: true });
  }
});
