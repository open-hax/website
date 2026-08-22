# 0001 — Published document paths use the SPA fallback, not pre-rendering

- Status: accepted
- Date: 2026-08-21
- Card: `website-published-content-source` (epic
  `knoxx-translated-publication-to-website`)
- Contract:
  `foresight:docs/notes/published-content-manifest-cross-repo-contract.md`

## The question the card asks

> Static hosting means client-side routing needs an SPA fallback, and an SPA
> fallback means a bad path returns 200 with the shell. Decide deliberately
> whether published document paths should be pre-rendered instead, and record
> why.

## Decision

Published document paths are served by the **SPA fallback**. They are not
pre-rendered. The site's own pages *are* pre-rendered, one shell per locale.

## Why pre-rendering published paths is not available

Pre-rendering a route requires knowing the route at build time. The manifest is
not build input:

```text
build time    the repository: dictionaries, components, staged assets
deploy time   <stateRoot>/website-content/manifest.edn, written by Knoxx
```

The content root is **state**, not build output — that separation is the whole
reason the deployment model splits `build.output` from `serve.docroot`, and the
reason a website redeploy that `rsync --delete`s its docroot cannot erase
published translations. A build cannot read it. Making it build input would:

1. **Invert the publication direction.** Every publication would require a
   website rebuild and redeploy. Today the reconciler writes an artifact,
   renames a manifest, and the next page load serves it. That is Law 5 of the
   epic — the manifest is the published fact — and pre-rendering would move the
   commit point from a rename into a CI pipeline.
2. **Break Law 6.** An empty or absent content root is a valid state. A build
   that reads the manifest has to decide what to do when there isn't one, and
   the honest answer is "the same thing", which is what we already do without
   reading it.
3. **Give two writers to one directory.** The content root has exactly one
   writer (Law 7). A build that emits pre-rendered documents into the docroot is
   a second one.

So the trade is not "pre-render or fall back". It is "fall back, or couple the
website's build to Knoxx's publication state". The epic's first non-goal forbids
the coupled version at request time; coupling it at build time is worse, because
it also makes publication slow.

## What the fallback costs, and what we do about it

A static fallback answers an unknown path with **200 and the shell**. Three
consequences, each handled:

| Consequence | Mitigation |
| --- | --- |
| A bad document path is a 200, not a 404 | The client resolves the path against the manifest and renders an explicit `PublishedNotFound` view that says nothing is published here. A crawler and a human both get an unambiguous answer, in the right language. |
| That 200 is indexable | `open-hax.website.extern.dom/set-robots-noindex!` adds `<meta name="robots" content="noindex">` for exactly the not-found view, and removes it otherwise. |
| A missing `/published/manifest.edn` would be answered with the shell | Two defences. The deployment must exclude `/published/` from the fallback (see the follow-up below); and independently, `open-hax.website.extern.fetch` treats a `text/html` response to a data request as **absent**, and `open-hax.website.infra.published-manifest` rejects a body that starts with `<`. A misconfigured host degrades to "nothing published", never to "the manifest is corrupt". |

A published document's *content* is fetched, not pre-rendered, so a document is
two round trips rather than one. That is acceptable for a document behind a link
and unacceptable for the site's own landing pages — which is exactly why those
are pre-rendered per locale and carry their `lang`, `<title>` and `<meta
description>` in the served bytes.

One consequence is worth stating plainly, because it is visible in the served
bytes and was confirmed against the dev server: a deep path such as
`/es/notes/hello` is answered with the **fallback shell**, which is the default
locale's. The served `lang` is `en` and the served `<title>` is the English one
until the bundle runs, resolves the path and corrects both. The site's own
locale roots (`/`, `/es/`, `/fr/`, `/de/`, `/ja/`) are real files and never have
this problem. A host that supports per-prefix fallbacks can remove it for
documents too — see the follow-up below.

## Follow-up owned by another repository

`open-hax/services` (`services-website-content-root`,
`services-website-as-gated-service`) must, in its serving config:

1. Mount `<stateRoot>/website-content` read-only at `/published/`.
2. Serve `.edn` as `application/edn`. A static server does not know the
   extension, and `octet-stream` is read correctly by `fetch().text()` but
   believed by nobody debugging in a browser.
3. **Exclude `/published/` from the SPA fallback**, so a missing artifact or
   manifest is a 404 rather than the shell.
4. Fall back to `/index.html` for everything else — and, if the host supports
   per-prefix fallbacks, prefer `/<locale>/*` -> `/<locale>/index.html` so a
   document path is answered with a shell already in the right language.
   `shadow-cljs`'s `:dev-http` takes a single `:push-state/index`, so local dev
   always answers deep paths with the default locale's shell and relies on the
   client to correct `lang`. Production does not have to.

## Revisit when

A published document needs to be indexable with a real 404 status, or a document
needs its content in the first byte for performance. The answer then is a
publish-time hook that writes a per-route shell into the content root — the
writer emitting HTML beside its artifact, still one writer, still no build
coupling — not a website rebuild.
