# open-hax/website

The public OpenHax site. A static artifact: per-locale HTML shells, one
ClojureScript bundle, one stylesheet, staged assets, and whatever the published
content manifest says is published.

Foresight declares three invariants against this file
(`foresight:src/foresight/project.cljc`). They are stated here because this is
their basis, and because each one is a rule about how to change this repository,
not a description of it.

## `:website/copy-resolves-from-locale-dictionaries`

**User-visible copy resolves from the per-locale dictionaries. View namespaces
embed no translatable literals.**

- The message catalogues are `src/cljc/open_hax/website/data/copy/<locale>.cljc`.
  `en` is the source locale and the reference every other locale is measured
  against.
- A view obtains a string exactly one way: `open-hax.website.domain.copy/text`,
  or `text-with` when the message carries `{placeholder}`s. A view that holds a
  sentence is a defect, and `test/cljs/open_hax/website/law/view_literals_test.cljs`
  reads the view sources and fails on one.
- Interpolated sentences are templates, never concatenations. `{count}` and
  `{year}` sit inside the translated string so each locale keeps its own word
  order — Japanese puts a counter directly after the numeral, German reorders
  the noun phrase, and no arrangement of a translated fragment plus a number
  produces either.
- `open-hax.website.law.copy` enforces four laws, asserted in
  `test/cljc/open_hax/website/law/copy_test.cljc`: identical key sets across
  every locale, identical placeholder sets per key, byte-identical proper nouns,
  and no blank values. Adding a key to `en` breaks the other four locales' tests
  until they are translated. That is the intended order of events.
- Product names (`Knoxx`, `Proxx`, `OpenPlanner`), `OpenHax`, `GitHub` and
  `Promethean` are proper nouns. They live in
  `src/cljc/open_hax/website/data/products.cljc` and in the `verbatim-keys` set,
  never as five translatable strings.
- Asset titles, categories and folder names are derived from filenames. They are
  not editorial copy, are not translated, and never enter a dictionary.

## `:website/published-artifact-is-static`

**The published site is a static build artifact. No secrets, no runtime
application authority, no backend needed to serve a locale.**

- `pnpm build` produces exactly one self-contained directory, `dist/site/`,
  containing one `index.html` per locale (`/`, `/es/`, `/fr/`, `/de/`, `/ja/`),
  the bundle, the stylesheet, the favicon and the staged assets.
  `scripts/verify-dist.mjs` proves it — one docroot, no symlinks, no absolute
  paths, no reference that leaves the directory — and `pnpm build` fails if it
  cannot.
- One docroot is a requirement, not a preference. The previous layout emitted
  `dist/cljs/app.js` and `dist/app.css` while `index.html` sat at the repository
  root and `:dev-http` merged three roots `["." "dist" "public"]`. No single
  directory could be handed to a static host, so what a developer saw and what a
  deployment could serve were different sites.
- Published documents are read from a **read-only content root** mounted at
  `/published/`, whose single writer is Knoxx. Published content is state, not
  build output: a redeploy that `rsync --delete`s the docroot must not be able
  to erase published translations.
- **No request from this site reaches a Knoxx origin.** The seam is files, so
  the site keeps serving what was last published when Knoxx is down.
  `open-hax.website.extern.fetch` refuses a non-relative path without making a
  request, `open-hax.website.law.published-manifest` rejects a manifest that
  supplies a URL instead of a path, and `scripts/verify-dist.mjs` greps the
  built artifact for a Knoxx authority.
- An absent or empty content root is a valid, fully served state. It is the
  state of every deploy before the first publication.

## `:website/translations-are-reviewed-upstream`

**Translated copy arrives as reviewed data. The build never machine-translates
and the site never fetches copy at runtime.**

- The dictionaries are edited by a person, or exported from the Knoxx
  translation pipeline after a human approval. No build step calls a translation
  service, and none may be added.
- Published documents arrive through the publication seam, already translated
  and already approved. The website renders them; it does not decide anything
  about them.
- The site's own copy and published documents are two different content sources
  and stay separate. The hero, the product cards and the galleries are OpenHax's
  own and ship in the build; they do not migrate into Knoxx.

## Namespace architecture

Four categories, plus inert data and the view layer. The rule is the one Knoxx
uses: the pure layer never depends inward on a runtime.

| Category | Where | May do | May not do |
| --- | --- | --- | --- |
| `shape.*` | `src/cljc` | structure-only morphisms over data | know about locales, manifests or the DOM |
| `domain.*` | `src/cljc` | pure decisions: locales, routing, copy resolution, the document shell | any I/O |
| `law.*` | `src/cljc` | contracts and validators, returning problems as data | any I/O, throwing at callers |
| `data.*` | `src/cljc` | inert data and generated manifests | require anything, decide anything |
| `infra.*` | `src/cljs` | orchestrate an effect: which URL, when, which outcome | hold a rule about a shape |
| `extern.*` | `src/cljs` | the only place raw JS interop is born (`fetch`, `document`) | leak a `Response` or a JS object across a layer |
| `sections.*`, `components.*`, `build.*` | `src/cljs` | render, or write files at build time | hold copy, or a routing decision |

Portable code is `.cljc` by default, per Foresight's mandate: shapes, laws,
routing, copy resolution and the manifest reader are all testable with no
browser, no server and no deploy. `src/cljs` is the outer edge.

## Zero warnings

`clj-kondo --lint src test` and every shadow-cljs build must report **zero**
warnings. A warning is a failed contract, not noise. Do not add to a baseline;
if an unrelated historical warning blocks verification, record it with the
owning file.

## Assets are staged, never symlinked

`public/graphics` and `public/music` used to be committed symlinks with absolute
paths into one developer's home directory:

```text
public/graphics -> /home/err/devel/Graphics_5000/Graphics
public/music    -> /home/err/Music
```

Both were dangling — including on the machine that committed them — while
`data/assets.cljs` listed 200 graphics and 200 tracks. The site referenced 400
files that did not exist, and any container or CI build produced a gallery of
broken images. The decision:

1. Absolute symlinks are never committed. `public/graphics` and `public/music`
   are gitignored.
2. `scripts/stage-assets.mjs` resolves roots from `OPENHAX_GRAPHICS_ROOT` and
   `OPENHAX_MUSIC_ROOT` — defaulting to the two paths above, so the author's
   workflow is unchanged — and stages what actually exists into `dist/site/`.
3. `scripts/scan-assets.mjs` regenerates
   `src/cljc/open_hax/website/data/assets.cljc` **from what was staged**, so the
   gallery manifest cannot claim a file the artifact does not contain. It is a
   generated file; edit the scanner, not the output.
4. **Zero staged assets is a valid build.** The galleries render empty and the
   count sentences read `0`, which is honest. The build does not fail and does
   not ship a dangling reference.

## Decisions

- `docs/decisions/0001-spa-fallback-over-prerendering.md` — why published
  document paths are served by an SPA fallback rather than pre-rendered, and
  what that costs.

## Commands

```bash
pnpm install
pnpm test                # shadow-cljs :test build, :autorun
pnpm lint                # clj-kondo, zero warnings
pnpm build               # dist/site, then verify:dist
pnpm verify:dist         # self-containment check alone
pnpm dev                 # one docroot at http://localhost:8080
```
