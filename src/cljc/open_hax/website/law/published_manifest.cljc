(ns open-hax.website.law.published-manifest
  "THE READER'S EXPECTATION of the published-content manifest.

  Stated by the website, in the website, as what this reader requires — not a
  copy of Knoxx's writer schema. Two repositories that meet at a directory and
  never share a compile must each be able to fail alone; a reader that borrows
  the writer's schema cannot detect the writer changing it.

  The contract this declares against:
  `foresight:docs/notes/published-content-manifest-cross-repo-contract.md`.
  The writer is the Knoxx static-site publication target adapter (card
  `knoxx-publication-static-site-target`, expected namespace
  `knoxx.backend.infra.publication-target-static-site`, sibling of the existing
  `knoxx.backend.infra.publication-target-memory`). When the fixtures in
  `test/fixtures/published/` and the real manifest diverge, that adapter is
  where to look.

  `decode` is pure and takes ALREADY-PARSED EDN. Parsing and fetching live in
  `open-hax.website.infra.published-manifest`, so every rule below is testable
  with no browser, no server and no deploy.

  The rules, and why they are asymmetric:

  | input                                | outcome                            |
  |--------------------------------------|------------------------------------|
  | absent (no file)                     | ok, no routes, `:source :absent`   |
  | `:manifest/routes` empty             | ok, no routes                      |
  | unknown field                        | ignored, never fatal               |
  | required field missing               | INVALID, loudly                    |
  | `:manifest/version` unsupported      | INVALID, loudly                    |
  | `:route/revision` a selector keyword | INVALID, loudly                    |
  | `:route/artifact` not a relative path| INVALID, loudly                    |

  An absent manifest is the normal state of every deploy before the first
  publication, so it must serve. A malformed manifest is a writer defect, and a
  reader that renders a blank page instead of reporting it turns a writer
  defect into an invisible outage."
  (:require [clojure.string :as str]))

(def supported-versions
  "Manifest versions this reader understands. An unsupported version fails
  loudly rather than being read optimistically: the whole point of the field is
  that the writer can change shape, and guessing defeats it."
  #{1})

(def required-route-keys
  "Ordered for stable error reporting."
  [:route/path :route/locale :route/artifact :route/media-type])

(def known-route-keys
  "Everything this reader reads. Anything else is ignored — dropped from the
  decoded route, so no downstream code can come to depend on a field this
  reader has not declared."
  #{:route/path :route/locale :route/document :route/revision
    :route/artifact :route/media-type :route/encoding :route/title
    :publication/id})

(def absent
  "The value the fetch edge reports when there is no manifest file. Distinct
  from `nil` so that 'no file' and 'a file containing nil' are not the same
  observation."
  ::absent)

(defn relative-path?
  "True when `s` is a path inside this origin: rooted or bare, with no scheme,
  no `//` authority, and no `..` traversal.

  This is the reader-side mechanism behind the epic's one prohibition — no
  request from the site reaches a Knoxx origin. An artifact reference is a
  file path under the content root, and a manifest that supplies a URL instead
  is rejected rather than fetched."
  [s]
  (boolean
   (and (string? s)
        (not (str/blank? s))
        (not (re-find #"^[a-zA-Z][a-zA-Z0-9+.-]*:" s))
        (not (str/starts-with? s "//"))
        (not (some #{".."} (str/split s #"/"))))))

(defn- selector-keyword?
  "A selector names a moving target — `:source/current` is the canonical one.
  It gives a stable-looking identity to something that changes, which is why
  `publish-idempotency-key` refuses one upstream and why a served path may not
  carry one."
  [v]
  (keyword? v))

(defn- error
  [kind message extra]
  (merge {:error/kind kind :error/message message} extra))

(defn- route-errors
  [index route]
  (if-not (map? route)
    [(error :manifest.error/route-not-a-map
            "A route must be a map."
            {:error/route-index index :error/value route})]
    (concat
     (for [k required-route-keys
           :when (nil? (get route k))]
       (error :manifest.error/missing-required-field
              (str "Route is missing required field " k ".")
              {:error/route-index index :error/key k}))
     (when-let [path (:route/path route)]
       (when-not (and (string? path) (str/starts-with? path "/"))
         [(error :manifest.error/malformed-path
                 "`:route/path` must be an absolute site path starting with `/`."
                 {:error/route-index index :error/key :route/path :error/value path})]))
     (when-let [loc (:route/locale route)]
       (when-not (keyword? loc)
         [(error :manifest.error/malformed-locale
                 "`:route/locale` must be a keyword."
                 {:error/route-index index :error/key :route/locale :error/value loc})]))
     (when-let [artifact (:route/artifact route)]
       (when-not (relative-path? artifact)
         [(error :manifest.error/non-relative-artifact
                 "`:route/artifact` must be a relative path under the content root."
                 {:error/route-index index :error/key :route/artifact :error/value artifact})]))
     (when (contains? route :route/revision)
       (let [revision (:route/revision route)]
         (cond
           (selector-keyword? revision)
           [(error :manifest.error/selector-revision
                   "`:route/revision` must be a concrete revision, not a selector keyword."
                   {:error/route-index index :error/key :route/revision :error/value revision})]

           (not (string? revision))
           [(error :manifest.error/malformed-revision
                   "`:route/revision` must be a string."
                   {:error/route-index index :error/key :route/revision :error/value revision})]

           :else nil))))))

(defn- manifest-errors
  [manifest]
  (let [version (:manifest/version manifest)
        routes (:manifest/routes manifest)]
    (concat
     (cond
       (nil? version)
       [(error :manifest.error/missing-version
               "`:manifest/version` is required."
               {:error/key :manifest/version})]

       (not (contains? supported-versions version))
       [(error :manifest.error/unsupported-version
               (str "Manifest version " (pr-str version) " is not supported by this reader.")
               {:error/key :manifest/version
                :error/value version
                :error/supported supported-versions})]

       :else nil)
     (when (and (some? routes) (not (sequential? routes)))
       [(error :manifest.error/routes-not-sequential
               "`:manifest/routes` must be a sequence when present."
               {:error/key :manifest/routes :error/value routes})])
     (when (sequential? routes)
       (mapcat route-errors (range) routes)))))

(defn- normalize-route
  [route]
  (select-keys route known-route-keys))

(defn decode
  "Decode already-parsed manifest EDN into the reader's own shape.

  Returns
  `{:published/status :ok | :invalid
    :published/source :absent | :manifest
    :published/routes [...]
    :published/errors [...]
    :published/version _
    :published/generated-at _}`.

  `:ok` with no routes is the pre-first-publication state and renders the
  site's own sections. `:invalid` carries every error found, not just the
  first, because a writer fixing one field wants the rest of the list."
  [value]
  (cond
    (or (= absent value) (nil? value))
    {:published/status :ok
     :published/source :absent
     :published/routes []
     :published/errors []}

    (not (map? value))
    {:published/status :invalid
     :published/source :manifest
     :published/routes []
     :published/errors [(error :manifest.error/not-a-map
                               "A manifest must be a map."
                               {:error/value value})]}

    :else
    (let [errors (vec (manifest-errors value))]
      (if (seq errors)
        {:published/status :invalid
         :published/source :manifest
         :published/routes []
         :published/errors errors
         :published/version (:manifest/version value)}
        {:published/status :ok
         :published/source :manifest
         :published/routes (mapv normalize-route (:manifest/routes value))
         :published/errors []
         :published/version (:manifest/version value)
         :published/generated-at (:manifest/generated-at value)}))))

(defn valid?
  [decoded]
  (= :ok (:published/status decoded)))

(defn edge-failure
  "An `:invalid` result produced by the fetch/parse edge rather than by the
  manifest's content: the file could not be read, or its bytes are not EDN.

  Both are loud for the same reason a missing required field is loud. 'The
  manifest says nothing is published' and 'we cannot tell what is published'
  are different facts, and only the first one is allowed to look like a normal
  page."
  [kind message extra]
  {:published/status :invalid
   :published/source :manifest
   :published/routes []
   :published/errors [(error kind message extra)]})
