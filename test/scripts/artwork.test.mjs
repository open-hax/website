import { test } from 'node:test';
import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import { createHash } from 'node:crypto';
import { join } from 'node:path';
import { DOMParser } from '@xmldom/xmldom';
import { repoRoot } from '../../scripts/asset-config.mjs';

const activeForeignElements = new Set(['script', 'iframe', 'frame', 'frameset', 'object', 'embed', 'audio', 'video', 'source', 'track', 'link', 'base', 'meta', 'style']);
const foreignResourceAttributes = new Set(['src', 'srcset', 'srcdoc', 'href', 'poster', 'data', 'action', 'formaction', 'background', 'ping', 'codebase', 'archive']);

function assertSvgHasNoActiveContent(svg, label) {
  assert.match(svg, /<svg\b[^>]*\bxmlns=["']http:\/\/www\.w3\.org\/2000\/svg["']/);
  assert.doesNotMatch(svg, /<script\b|\bon\w+\s*=|(?:xlink:)?href\s*=\s*["'](?:https?:|\/\/|javascript:)|url\(\s*["']?(?:https?:|\/\/)/i, label);
  const document = new DOMParser({ onError: (_level, message) => { throw new Error(`${label}: ${message}`); } }).parseFromString(svg, 'image/svg+xml');
  // Inspect only embedded foreign content; keep ordinary SVG and text/style
  // markup supported without defining a general XML element allowlist.
  for (const foreign of Array.from(document.getElementsByTagNameNS('http://www.w3.org/2000/svg', 'foreignObject'))) {
    for (const element of Array.from(foreign.getElementsByTagName('*'))) {
      assert.ok(!activeForeignElements.has(element.localName.toLowerCase()), `${label}: active foreignObject descendant ${element.tagName}`);
      for (const attribute of Array.from(element.attributes)) {
        const name = attribute.localName.toLowerCase();
        assert.ok(!foreignResourceAttributes.has(name) && !name.startsWith('on'), `${label}: foreignObject resource/event attribute ${attribute.name}`);
        if (name === 'style') {
          assert.doesNotMatch(attribute.value, /url\s*\(|@import\b/i, `${label}: foreignObject resource style`);
        }
      }
    }
  }
}

const foreignObjectSvg = content => `<svg xmlns="http://www.w3.org/2000/svg"><foreignObject>${content}</foreignObject></svg>`;

test('foreignObject preserves text and inline styles', () => {
  assert.doesNotThrow(() => assertSvgHasNoActiveContent(foreignObjectSvg('<div xmlns="http://www.w3.org/1999/xhtml" style="color:red;font-size:12px">Hello &amp; welcome</div>'), 'text'));
});

for (const [name, content] of [
  ['embedded frame', '<div xmlns="http://www.w3.org/1999/xhtml"><iframe srcdoc="&lt;p&gt;embedded&lt;/p&gt;"/></div>'],
  ['namespace-qualified frame', '<x:iframe xmlns:x="http://www.w3.org/1999/xhtml"/>'],
  ['local resource', '<div xmlns="http://www.w3.org/1999/xhtml"><img src="local.png"/></div>'],
  ['srcdoc attribute', '<div xmlns="http://www.w3.org/1999/xhtml" srcdoc="&lt;p&gt;embedded&lt;/p&gt;"/>'],
  ['XML-encoded CSS resource', '<div xmlns="http://www.w3.org/1999/xhtml" style="background:u&#114;l(local.png)"/>'],
  ['relative link', '<div xmlns="http://www.w3.org/1999/xhtml"><a href="local.html">Link</a></div>']
]) {
  test(`foreignObject rejects ${name}`, () => {
    assert.throws(() => assertSvgHasNoActiveContent(foreignObjectSvg(content), content), undefined, content);
  });
}

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
      assertSvgHasNoActiveContent(svg, relative);
    } else {
      assert.equal(bytes.subarray(0, 8).toString('hex'), '89504e470d0a1a0a');
    }
  }
  const featured = JSON.parse(readFileSync(join(repoRoot, 'assets/featured-graphics.json'), 'utf8'));
  assert.ok(featured.length > 0);
  assert.ok(featured.every(path => accepted.some(record => (record.relative ?? record.source_path?.replace(/^Graphics\//, '')) === path)));
});
