# Translation target contract

> Status: **implemented.** This site is Knoxx's first publication target, and
> the contract below is the one the code enforces, not a proposal. Where a rule
> has a home in source, this document names it and does not restate it — a
> second copy of a contract is a second thing to drift.
>
> | concern | authority |
> |---|---|
> | manifest shape and reader rules | `src/cljc/open_hax/website/law/published_manifest.cljc` |
> | decisions over a decoded manifest | `src/cljc/open_hax/website/domain/published.cljc` |
> | locale set and path grammar | `src/cljc/open_hax/website/domain/locale.cljc` |
> | HTTP behaviour | `services:digitalocean/services/website/nginx.conf` |
> | the directory and its writer | `services:docs/published-content-root.md` |
> | cross-repo manifest agreement | `foresight:docs/notes/published-content-manifest-cross-repo-contract.md` |
> | why published paths are not pre-rendered | `docs/decisions/0001-spa-fallback-over-prerendering.md` |

## The seam: a manifest on disk

The site does **not** call Knoxx. It reads a manifest and files from a directory
Knoxx writes and this site mounts read-only.

```text
knoxx ──rw──> /srv/open-hax/state/website-content ──ro──> nginx ──> browser
                                                    served at /published/
```

That choice is why this site was the first target: when Knoxx is down, the site
still serves everything last published. A request-time API would trade that away
for nothing this site needs.

**The manifest is the published fact.** An artifact file that no route names is
not rendered by this site.

## Manifest wire contract

Declared in `law.published-manifest` as *this reader's expectation* — stated in
this repository rather than copied from Knoxx's writer schema, because two repos
that meet at a directory and never share a compile must each be able to fail
alone. A reader that borrows the writer's schema cannot detect the writer
changing it.

- **Version.** `:manifest/version`, and `supported-versions` is `#{1}`. An
  unsupported version fails loudly rather than being read optimistically: the
  point of the field is that the writer can change shape, and guessing defeats
  it.
- **Routes.** `:manifest/routes`, each requiring
  `[:route/path :route/locale :route/artifact :route/media-type]`. Optional and
  read: `:route/document`, `:route/revision`, `:route/encoding`, `:route/title`,
  `:publication/id`.
- **Ordering.** The manifest owns each route's public path including its locale
  prefix; the reader resolves a request path against it and does not re-derive
  paths. Switcher order comes from the site's locale order, not the manifest's.
- **Failure semantics**, and they are deliberately asymmetric:

  | input | outcome |
  |---|---|
  | absent (no file) | ok, no routes, `:source :absent` |
  | `:manifest/routes` empty | ok, no routes |
  | unknown field | ignored, dropped from the decoded route |
  | required field missing | invalid, loudly |
  | unsupported `:manifest/version` | invalid, loudly |
  | `:route/revision` a selector keyword | invalid, loudly |
  | `:route/artifact` not a relative path | invalid, loudly |

  An absent manifest is the normal state of every deploy before the first
  publication, so it must serve. A malformed manifest is a writer defect, and a
  reader that renders a blank page instead of reporting it turns a writer defect
  into an invisible outage.

- **No artifact reference may leave this origin.** `relative-path?` rejects a
  scheme, a `//` authority and any `..` traversal, and `artifact-url` never
  returns an absolute URL. This is the reader-side mechanism behind the one
  prohibition: no request from the site reaches a Knoxx origin.

`decode` is pure and takes already-parsed EDN, so every rule above is testable
with no browser, no server and no deploy. Fixtures live in
`test/fixtures/published/`.

## HTTP behaviour

Normative, and implemented in the serving config. There is one policy, not a
choice between two:

| request | response |
|---|---|
| `/published/…` present | 200, `no-cache`; `.edn` typed `application/edn` |
| `/published/…` absent | **404** — never the app shell |
| `/es`, `/fr`, `/de`, `/ja` and their subpaths | that locale's shell |
| a locale shell missing from the build | **404** — a deployment defect |
| a path the manifest does not name | **200** with the shell, then an explicit not-found view |
| any other path with no file behind it | 200 with the shell for that locale |

**A missing published document answers 200, not 404, and that is the decided
policy** — see `docs/decisions/0001-spa-fallback-over-prerendering.md`.
Pre-rendering published paths is not available: the manifest is deploy-time
state, not build input, so reading it at build time would invert the publication
direction, break the empty-content-root rule, and give the content root a second
writer. The 200 is mitigated rather than accepted bare: the client resolves the
path against the manifest and renders an explicit not-found view in the right
language, and `set-robots-noindex!` marks exactly that view `noindex` so the
soft-404 is not indexable.

Three more rules worth stating plainly:

- **`/published/` never falls back to the shell**, defended twice. Answering
  `/published/manifest.edn` with 200 `text/html` would turn "nothing published
  yet" — the ordinary pre-first-publication state — into a hard reader failure.
  A 404 is what that state means. The `^~` prefix keeps any later regex location
  from stealing these paths and reintroducing the fallback. Independently of the
  host, `extern.fetch` treats a `text/html` answer to a data request as
  **absent** and `infra.published-manifest` rejects a body starting with `<`, so
  a misconfigured deployment degrades to "nothing published" rather than "the
  manifest is corrupt".
- **Locale prefixes keep their locale.** Per-locale shells carry localized
  `lang`, title and canonical metadata, so falling through to `/index.html`
  would serve the default locale for `/es/notes/hello`. Each locale gets its own
  fallback to its own shell.

An unknown locale prefix resolves to the **default locale**, not a 404: a stale
or guessed link should land somewhere readable. That is a deliberate difference
from a missing *known* locale shell, which is a build defect and 404s.

The shell is what makes an empty content root a valid state — the site's own
sections are compiled into the bundle and do not come through the publication
seam.

## What this site provides

1. **Locale routing.** Default locale at `/`, a path prefix per additional
   locale, unknown prefixes resolving to the default.
2. **`lang` per rendered locale**, from per-locale shells generated at build
   time by `build.html` out of the same dictionaries the running app uses, so a
   translated `<title>` cannot drift from a translated page.
3. **A switcher that offers only locales with content.** `switcher-options`
   distinguishes locales this build carries copy for from locales the manifest
   carries documents for — the distinction that is easy to get wrong. Offering a
   locale the manifest lacks is worse than offering no switcher at all.
4. **Reader-side contract tests** over committed fixtures, in the existing
   `shadow-cljs` `:test` build.
5. **A build that emits one directory.** `pnpm run build:site` → `dist/site`,
   the only directory copied into the serving image. Dev serves from three
   merged roots (`["." "dist" "public"]`) and one docroot cannot reproduce that.

## Deployment shape

Built in CI as a container image: a builder stage runs `build:site`, the serving
stage is nginx plus a single `COPY --from=build` of `dist/site`. Nothing is built
on the runner or on the host, and nothing else from the checkout ships.

The published content root is **not** part of the image. It is a host directory
bind-mounted read-only, written by Knoxx on the same host, so replacing this
site's image never touches published translations.

## Non-goals

- No request-time calls to Knoxx or any other origin.
- No translation performed here. This site renders translations; it does not
  produce, review or approve them.
- No editor, no preview, no draft state. Only published content reaches the
  content root.
- No second publication target. One real adapter is the proof.
