(ns open-hax.website.law.published-manifest-fixtures
  "The manifest fixtures, with the outcome each one is required to produce.

  Two representations of the same fixtures exist on purpose:

  - the EDN files under `test/fixtures/published/`, which are the bytes a
    static server would hand this reader, and
  - this table, which is small enough to read and states what each fixture
    MEANS.

  `open-hax.website.infra.published-manifest-fixtures-test` asserts the two
  agree, so the readable version cannot drift from the wire version — the
  precise failure that makes a fixture set a second implementation nobody
  maintains.

  PROVENANCE. The writer is the Knoxx static-site publication target adapter:
  card `knoxx-publication-static-site-target`, expected namespace
  `knoxx.backend.infra.publication-target-static-site`, sibling of the existing
  `knoxx.backend.infra.publication-target-memory`. The contract both sides
  declare against is
  `foresight:docs/notes/published-content-manifest-cross-repo-contract.md`.
  When a real manifest and these fixtures disagree, one of those two is wrong
  and this comment is the pointer to the other one."
  (:require [open-hax.website.law.published-manifest :as law]))

(def fixture-dir "test/fixtures/published")

(def fixtures
  [{:fixture/id :absent
    :fixture/file nil
    :fixture/why "No manifest file at all: every deploy before the first publication."
    :fixture/edn law/absent
    :fixture/expect {:status :ok
                     :source :absent
                     :route-count 0
                     :published-locales []
                     :renders "the site's own sections, unchanged"}}

   {:fixture/id :empty
    :fixture/file "empty.edn"
    :fixture/why "A manifest that exists and publishes nothing."
    :fixture/edn {:manifest/version 1
                  :manifest/generated-at "2026-08-21T18:04:11Z"
                  :manifest/routes []}
    :fixture/expect {:status :ok
                     :source :manifest
                     :route-count 0
                     :published-locales []
                     :renders "the site's own sections, unchanged"}}

   {:fixture/id :single-locale
    :fixture/file "single-locale.edn"
    :fixture/why "One document, one locale."
    :fixture/edn {:manifest/version 1
                  :manifest/generated-at "2026-08-21T18:04:11Z"
                  :manifest/routes
                  [{:route/path "/es/notes/hello"
                    :route/locale :es
                    :route/document "notes/hello"
                    :route/revision "rev-7f3a91c"
                    :route/artifact "artifacts/notes/hello/es/rev-7f3a91c.html"
                    :route/media-type "text/html"
                    :route/encoding "utf-8"
                    :route/title "Hola"
                    :publication/id "pub-0d41ab9c"}]}
    :fixture/expect {:status :ok
                     :source :manifest
                     :route-count 1
                     :published-locales [:es]
                     :renders "/es/notes/hello as a document with lang es"}}

   {:fixture/id :multi-locale
    :fixture/file "multi-locale.edn"
    :fixture/why "One document in two locales, plus a second document in one of them."
    :fixture/edn {:manifest/version 1
                  :manifest/generated-at "2026-08-21T18:11:02Z"
                  :manifest/routes
                  [{:route/path "/es/notes/hello"
                    :route/locale :es
                    :route/document "notes/hello"
                    :route/revision "rev-7f3a91c"
                    :route/artifact "artifacts/notes/hello/es/rev-7f3a91c.html"
                    :route/media-type "text/html"
                    :route/title "Hola"
                    :publication/id "pub-0d41ab9c"}
                   {:route/path "/fr/notes/hello"
                    :route/locale :fr
                    :route/document "notes/hello"
                    :route/revision "rev-7f3a91c"
                    :route/artifact "artifacts/notes/hello/fr/rev-7f3a91c.html"
                    :route/media-type "text/html"
                    :route/title "Bonjour"
                    :publication/id "pub-2b8ce410"}
                   {:route/path "/es/notes/solo"
                    :route/locale :es
                    :route/document "notes/solo"
                    :route/revision "rev-11c0de5"
                    :route/artifact "artifacts/notes/solo/es/rev-11c0de5.html"
                    :route/media-type "text/html"
                    :route/title "Solo"
                    :publication/id "pub-6d1f0a72"}]}
    :fixture/expect {:status :ok
                     :source :manifest
                     :route-count 3
                     :published-locales [:es :fr]
                     :renders "both /es/notes/hello and /fr/notes/hello, each with its own lang"}}

   {:fixture/id :unknown-field
    :fixture/file "unknown-field.edn"
    :fixture/why "A writer newer than this reader."
    :fixture/edn {:manifest/version 1
                  :manifest/generated-at "2026-08-21T18:04:11Z"
                  :manifest/writer "knoxx/static-site@future"
                  :manifest/routes
                  [{:route/path "/es/notes/hello"
                    :route/locale :es
                    :route/document "notes/hello"
                    :route/artifact "artifacts/notes/hello/es/rev-7f3a91c.html"
                    :route/media-type "text/html"
                    :route/experiment true
                    :route/word-count 412}]}
    :fixture/expect {:status :ok
                     :source :manifest
                     :route-count 1
                     :published-locales [:es]
                     :renders "the route normally; unknown fields ignored, never fatal"}}

   {:fixture/id :missing-required-field
    :fixture/file "missing-required-field.edn"
    :fixture/why "A route with no :route/media-type."
    :fixture/edn {:manifest/version 1
                  :manifest/generated-at "2026-08-21T18:04:11Z"
                  :manifest/routes
                  [{:route/path "/es/notes/hello"
                    :route/locale :es
                    :route/document "notes/hello"
                    :route/artifact "artifacts/notes/hello/es/rev-7f3a91c.html"}]}
    :fixture/expect {:status :invalid
                     :source :manifest
                     :route-count 0
                     :published-locales []
                     :error-kinds [:manifest.error/missing-required-field]
                     :renders "the site's own sections plus a loud error naming the field"}}

   {:fixture/id :bad-version
    :fixture/file "bad-version.edn"
    :fixture/why "A manifest version this reader does not understand."
    :fixture/edn {:manifest/version 99
                  :manifest/generated-at "2026-08-21T18:04:11Z"
                  :manifest/routes
                  [{:route/path "/es/notes/hello"
                    :route/locale :es
                    :route/artifact "artifacts/notes/hello/es/rev-7f3a91c.html"
                    :route/media-type "text/html"}]}
    :fixture/expect {:status :invalid
                     :source :manifest
                     :route-count 0
                     :published-locales []
                     :error-kinds [:manifest.error/unsupported-version]
                     :renders "the site's own sections plus a loud error"}}

   {:fixture/id :selector-revision
    :fixture/file "selector-revision.edn"
    :fixture/why "A selector keyword where a concrete revision belongs."
    :fixture/edn {:manifest/version 1
                  :manifest/generated-at "2026-08-21T18:04:11Z"
                  :manifest/routes
                  [{:route/path "/es/notes/hello"
                    :route/locale :es
                    :route/document "notes/hello"
                    :route/revision :source/current
                    :route/artifact "artifacts/notes/hello/es/rev-7f3a91c.html"
                    :route/media-type "text/html"}]}
    :fixture/expect {:status :invalid
                     :source :manifest
                     :route-count 0
                     :published-locales []
                     :error-kinds [:manifest.error/selector-revision]
                     :renders "the site's own sections plus a loud error"}}

   {:fixture/id :cross-origin-artifact
    :fixture/file "cross-origin-artifact.edn"
    :fixture/why "An artifact reference pointing at another origin."
    :fixture/edn {:manifest/version 1
                  :manifest/generated-at "2026-08-21T18:04:11Z"
                  :manifest/routes
                  [{:route/path "/es/notes/hello"
                    :route/locale :es
                    :route/artifact "https://knoxx.internal/artifacts/notes/hello/es/rev-7f3a91c.html"
                    :route/media-type "text/html"}]}
    :fixture/expect {:status :invalid
                     :source :manifest
                     :route-count 0
                     :published-locales []
                     :error-kinds [:manifest.error/non-relative-artifact]
                     :renders "the site's own sections plus a loud error; no request is made"}}])

(defn fixture
  [id]
  (first (filter #(= id (:fixture/id %)) fixtures)))

(defn fixture-edn
  [id]
  (:fixture/edn (fixture id)))
