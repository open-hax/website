// Shared configuration for the asset staging and manifest-generation steps.
//
// The galleries used to be served through two committed symlinks
// (public/graphics -> /home/err/devel/Graphics_5000/Graphics,
//  public/music    -> /home/err/Music).
// An absolute symlink is correct on exactly one machine and dangling in every
// other checkout, container and CI runner, so the site referenced 400 files
// that did not exist. Roots are now inputs, resolved from the environment,
// with a committed collection as the default so fresh builds include the work.
//
// See AGENTS.md, "Assets are staged, never symlinked".

import { fileURLToPath } from 'node:url';
import { dirname, resolve } from 'node:path';

export const repoRoot = resolve(dirname(fileURLToPath(import.meta.url)), '..');

export const graphicsRoot =
  process.env.OPENHAX_GRAPHICS_ROOT ?? resolve(repoRoot, 'assets/graphics');

export const musicRoot =
  process.env.OPENHAX_MUSIC_ROOT ?? resolve(repoRoot, 'assets/music');

export const siteOutDir = resolve(
  repoRoot,
  process.env.OPENHAX_SITE_OUT ?? 'dist/site',
);

// The gallery reveals the collection in pages. An explicit limit remains
// available for preview builds; the published default includes the collection.
export const assetLimit = Number.parseInt(process.env.OPENHAX_ASSET_LIMIT ?? '0', 10);

export const graphicsExtensions = /\.(png|jpe?g|gif|webp|avif|svg)$/i;
export const musicExtensions = /\.(wav|mp3|ogg|flac|m4a)$/i;

export const assetsManifestPath = resolve(
  repoRoot,
  'src/cljc/open_hax/website/data/assets.cljc',
);

// Stable, path-derived ids. The previous generator used Math.random(), so the
// generated file changed on every run and every React key changed with it.
export function stableId(relativePath) {
  let hash = 0x811c9dc5;
  for (let i = 0; i < relativePath.length; i += 1) {
    hash ^= relativePath.charCodeAt(i);
    hash = Math.imul(hash, 0x01000193) >>> 0;
  }
  return hash;
}
