# OpenHax Website

Portfolio website for the OpenHax organization, showcasing our products, art,
and music — in five languages, plus whatever Knoxx has published.

Read [`AGENTS.md`](AGENTS.md) before changing anything. It is the binding house
law for this repository: where copy lives, what the published artifact is, and
why the asset directories are staged rather than symlinked.

## Stack

- **ClojureScript** via shadow-cljs
- **Helix** (React wrapper)
- **UXX** design system and component library
- **Tailwind CSS** with UXX design tokens

## Development

```bash
pnpm install

pnpm dev          # one docroot at http://localhost:8080
pnpm test         # shadow-cljs :test build (:autorun)
pnpm lint         # clj-kondo, zero warnings
pnpm build        # dist/site/, then prove it is self-contained
```

## Locales

`en` (source) at `/`, and `es`, `fr`, `de`, `ja` at `/es/`, `/fr/`, `/de/`,
`/ja/`. An unknown locale-shaped prefix resolves to `en` rather than 404ing.

Copy lives in `src/cljc/open_hax/website/data/copy/<locale>.cljc`. Views hold no
literals — `open-hax.website.domain.copy/text` is the only way a string reaches
the DOM — and `open-hax.website.law.copy` requires every locale to define
exactly the same key set with exactly the same `{placeholder}`s. Adding a key to
`en` fails the other four locales' tests until they are translated.

## Published content

The site renders two content sources and keeps them separate:

```text
the site's own copy   ->  per-locale dictionaries, compiled into the build
published documents   ->  a manifest + artifacts under a read-only content root
```

The manifest is read once at load from `/published/manifest.edn`, where
`open-hax/services` mounts `<stateRoot>/website-content` read-only. The reader's
expectation of that file is stated in
`src/cljc/open_hax/website/law/published_manifest.cljc` and asserted against the
fixtures in `test/fixtures/published/`. The shared contract lives in
`foresight:docs/notes/published-content-manifest-cross-repo-contract.md`.

An **absent or empty** manifest is a valid state that renders the site's own
sections normally — it is the state of every deploy before the first
publication. A **malformed** manifest fails loudly: the site keeps serving its
own sections and pins an error naming the offending fields, because a reader
that renders a blank page turns a writer defect into an invisible outage.

No request from this site reaches a Knoxx origin. The seam is files.

## Build output

`pnpm build` produces one self-contained directory, `dist/site/`:

```text
dist/site/
├── index.html          en, lang="en", localized title + hreflang alternates
├── es/index.html       …and one per locale
├── fr/index.html
├── de/index.html
├── ja/index.html
├── app.css
├── cljs/app.js
├── favicon.svg
├── graphics/           staged, may be empty
└── music/              staged, may be empty
```

`scripts/verify-dist.mjs` runs as the last build step and fails the build if the
directory is not self-contained: a missing locale shell, a symlink, an absolute
path, a gallery entry with no file behind it, or a reference to a Knoxx origin.

## Gallery assets

The galleries are staged into the build output from roots resolved at build
time:

```bash
OPENHAX_GRAPHICS_ROOT=/path/to/graphics \
OPENHAX_MUSIC_ROOT=/path/to/music \
pnpm build
```

Both default to the paths the previous committed symlinks pointed at, so the
author's workflow is unchanged. `scripts/scan-assets.mjs` then regenerates
`src/cljc/open_hax/website/data/assets.cljc` from what was actually staged, so
the gallery can never reference a file the artifact does not contain.

**Zero staged assets is a valid build.** The galleries render empty and the
count sentences read `0`. See `AGENTS.md`, "Assets are staged, never symlinked",
for why this replaced two committed absolute symlinks.

## Translation target

This site is Knoxx's first publication target.
[`docs/translation-target-contract.md`](docs/translation-target-contract.md)
is the implemented contract: the manifest wire shape and its failure semantics,
the HTTP behaviour for published paths and locale prefixes, and where each rule
lives in source.

## Domain

- Production: `open-hax.promethean.rest`
