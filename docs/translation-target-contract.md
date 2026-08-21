# Translation target contract

> Status: **contract statement, not implementation.** Nothing described here is
> built. This document records what this site must become in order to be the
> first publication target of Knoxx's contract-owned publication pipeline, and
> what it deliberately does not take on.
>
> Work is tracked as `knoxx-translated-publication-to-website` on the Knoxx
> board. The website's own cards there are `website-published-content-source`
> and `website-manifest-contract-tests`.

## Where this site stands today

Honestly: it is a single-language brochure with no content source.

- Every string is compiled in. `src/cljs/open_hax/website/sections/hero.cljs`
  carries "OpenHax", "Art, Music, and Programs", and the body copy as literals;
  the other sections are the same shape.
- `index.html` hardcodes `lang="en"`.
- There is no routing. `core.cljs` mounts the app; `app.cljs` composes sections.
  There is no path handling of any kind, and therefore no locale prefix.
- The only external data is `src/cljs/open_hax/website/data/assets.cljs`, an
  auto-generated manifest of images and audio produced by `scripts/scan-assets.mjs`
  at build time — not content, and not translated.

So "translate the website" is not a matter of extracting strings. The site has no
seam through which anything outside this repository can contribute a page.

## The seam: a manifest on disk

The site does **not** call Knoxx. It reads a manifest and files from a directory
that Knoxx writes and this site mounts read-only.

```text
knoxx ──writes──> content root ──read-only──> website nginx ──serves──> browser
```

That choice is deliberate and is the reason this site was picked as the first
target: when Knoxx is down, the site still serves everything last published. A
request-time API would trade that away for nothing this site needs.

Consequences that fall on this repository:

- **The manifest is the authority.** A file present in the content root but
  absent from the manifest is not public. The site renders what the manifest
  declares, and nothing else.
- **Absent and empty are normal.** The first deploy, and every deploy before the
  first publication, has no manifest. That must render the site exactly as it
  renders today — the existing sections, no error, no blank page.
- **Published content is not the site's own content.** The hero and the asset
  galleries belong to this repository. Published documents do not. Keep them
  separated; migrating the existing sections into Knoxx is explicitly out of
  scope.

## What this site must provide

1. **Locale routing.** Default locale at the root, a path prefix per additional
   locale, unknown prefixes resolving to the default rather than 404ing.
2. **`lang` per rendered locale**, replacing the hardcoded `lang="en"`.
3. **A switcher that offers only published locales.** Listing a language with no
   content behind it is worse than listing none.
4. **A declared reader-side expectation of the manifest**, with fixtures in this
   repo and tests in the existing `shadow-cljs` `:test` build. The manifest is a
   writer in Knoxx CLJS and a reader in website CLJS with a filesystem and a
   deploy in between, and no shared compile — the exact configuration that
   produced five same-shaped defects in Knoxx's MCP work, every one an undeclared
   boundary.
5. **A build that emits one directory.** See below; this is a prerequisite for
   being deployable at all.

## The build output problem

`shadow-cljs.edn` serves development from three merged roots:

```clojure
:dev-http {8080 {:roots ["." "dist" "public"] :push-state/index "index.html"}}
```

`index.html` is at the repository root, the compiled app lands in `dist/cljs/`,
the stylesheet in `dist/app.css`, and static assets live in `public/`. A single
nginx docroot cannot reproduce a three-root merge, so a production deploy has to
choose one directory — and the open deployment branch (`open-hax/services#19`)
chose `public/`, which contains only `graphics/` and `music/` and none of the
application.

Before this site is deployable, `pnpm build` must assemble one directory
containing the shell, the compiled app, the stylesheet and the static assets.
That directory is the only thing a deploy ships.

## SPA fallback, stated rather than inherited

Static hosting with client-side routing needs a catch-all rewrite to the shell,
which means an unknown path returns 200 with the shell rather than a 404. For a
brochure that is fine. For published documents addressable by locale and path it
is a decision with real consequences for links, crawlers and error reporting, and
the alternative — pre-rendering a file per published path, which the manifest
makes possible — should be chosen deliberately and the reasoning recorded.

## Non-goals

- No request-time calls to Knoxx or any other origin.
- No translation performed here. This site renders translations; it does not
  produce, review or approve them.
- No editor, no preview, no draft state. Only published content reaches the
  content root.
- No second publication target. One real adapter is the proof.
