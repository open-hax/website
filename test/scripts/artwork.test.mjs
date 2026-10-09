import { test } from 'node:test';
import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import { createHash } from 'node:crypto';
import { join } from 'node:path';
import { repoRoot } from '../../scripts/asset-config.mjs';

test('committed artwork retains source hashes and has no active or external SVG content', () => {
  const manifest = JSON.parse(readFileSync(join(repoRoot, 'assets/graphics-provenance.json'), 'utf8'));
  const records = manifest.artifacts;
  assert.ok(Array.isArray(records), 'provenance must identify each source file');
  const accepted = records.filter(record => record.disposition === 'accepted');
  assert.ok(accepted.length > 0, 'the default collection must be populated');
  const hashes = new Set();
  for (const record of accepted) {
    const relative = record.relative ?? record.source_path?.replace(/^Graphics\//, '');
    assert.ok(relative && !relative.startsWith('/') && !relative.split('/').includes('..'));
    const bytes = readFileSync(join(repoRoot, 'assets/graphics', relative));
    const hash = createHash('sha256').update(bytes).digest('hex');
    assert.equal(hash, record.sha256, relative);
    assert.ok(!hashes.has(hash), `duplicate artwork: ${relative}`);
    hashes.add(hash);
    if (relative.endsWith('.svg')) {
      const svg = bytes.toString('utf8');
      assert.match(svg, /<svg\b[^>]*\bxmlns=["']http:\/\/www\.w3\.org\/2000\/svg["']/);
      assert.doesNotMatch(svg, /<script\b|\bon\w+\s*=|(?:xlink:)?href\s*=\s*["'](?:https?:|\/\/|javascript:)|url\(\s*["']?(?:https?:|\/\/)/i);
    } else {
      assert.equal(bytes.subarray(0, 8).toString('hex'), '89504e470d0a1a0a');
    }
  }
  const featured = JSON.parse(readFileSync(join(repoRoot, 'assets/featured-graphics.json'), 'utf8'));
  assert.ok(featured.length > 0);
  assert.ok(featured.every(path => accepted.some(record => (record.relative ?? record.source_path?.replace(/^Graphics\//, '')) === path)));
});
