(ns open-hax.website.domain.published
  "Decisions over a decoded published-content manifest. Pure, no I/O.

  The manifest is the published fact: an artifact file that no route names is
  not public, so every question below is answered from the decoded routes and
  never from the filesystem. Removing a route therefore stops it rendering,
  with no other action taken anywhere.

  Where the content root is mounted is a deployment fact (open-hax/services
  mounts `<stateRoot>/website-content` read-only at `/published/`), so the
  paths here are the reader's half of that agreement and are relative to this
  origin. Nothing in this namespace can produce a cross-origin URL."
  (:require [clojure.string :as str]
            [open-hax.website.domain.locale :as locale]
            [open-hax.website.law.published-manifest :as law]))

(def content-root
  "Where the read-only published-content mount appears in this origin."
  "/published")

(def manifest-url
  "Fetched once at load. Relative by construction — see
  `open-hax.website.law.published-manifest/relative-path?`."
  (str content-root "/manifest.edn"))

(defn artifact-url
  "Same-origin URL for a route's artifact, or nil when the route has no usable
  artifact reference. Never returns an absolute URL: a manifest that supplies
  one is rejected before it reaches here."
  [route]
  (let [artifact (:route/artifact route)]
    (when (law/relative-path? artifact)
      (str content-root "/" (str/replace artifact #"^/+" "")))))

(defn routes
  [decoded]
  (vec (:published/routes decoded)))

(defn routes-for-locale
  [decoded loc]
  (filterv #(= loc (:route/locale %)) (routes decoded)))

(defn published-locales
  "MANIFEST-DERIVED. The locales that actually carry published documents, in
  the site's locale order, followed by any locale the manifest carries that
  this build has no dictionary for (the writer is allowed to be ahead of us).

  This is NOT `open-hax.website.domain.locale/supported-locales`, which is the
  set of locales whose copy is compiled into the artifact and therefore always
  available. Confusing the two is how a switcher comes to offer a locale with
  nothing behind it."
  [decoded]
  (let [present (into #{} (keep :route/locale) (routes decoded))]
    (into (filterv present locale/supported-locales)
          (sort (remove locale/supported-locale-set present)))))

(defn document-locales
  "The locales in which `document` is published.

  A nil `document` matches nothing rather than matching every route that
  happens to omit `:route/document`: two unrelated untitled routes are not
  translations of each other."
  [decoded document]
  (if (nil? document)
    []
    (let [present (into #{}
                        (comp (filter #(= document (:route/document %)))
                              (keep :route/locale))
                        (routes decoded))]
      (into (filterv present locale/supported-locales)
            (sort (remove locale/supported-locale-set present))))))

(defn route-at
  "The route serving `path`, or nil. `:route/path` is matched literally: the
  manifest owns the public path, including its locale prefix."
  [decoded path]
  (let [wanted (locale/normalize-path path)]
    (first (filter #(= wanted (locale/normalize-path (:route/path %)))
                   (routes decoded)))))

(defn resolve-location
  "Resolve a request path against the manifest.

  Returns
  `{:locale/id, :locale/fallback?, :location/path, :view/kind, :view/route}`
  where `:view/kind` is

    :site       the site's own sections (the root of a locale)
    :document   a published document named by the manifest
    :not-found  a path with no published route behind it

  An unknown locale-shaped prefix resolves to the default locale rather than
  404ing, so `/pt/notes/hello` looks for `/notes/hello` in the default locale."
  [decoded path]
  (let [{:keys [locale/id locale/fallback? location/path] :as resolved} (locale/resolve-path path)
        canonical (locale/localize-path id path)
        route (route-at decoded canonical)]
    (assoc resolved
           :view/kind (cond
                        route :document
                        (= "/" path) :site
                        :else :not-found)
           :view/route route
           :view/canonical-path canonical
           :locale/fallback? (boolean fallback?))))

(defn switcher-options
  "The language switcher's options for a resolved location.

  TWO DISTINCT NOTIONS, and the reason this function takes the location rather
  than just the manifest:

  - On the site's own pages the offerable locales are the ones whose
    dictionaries are compiled in. All of them, always: the copy shipped with
    the build, so there is nothing to be missing.
  - On a published document the offerable locales are MANIFEST-DERIVED — only
    the locales in which that document is actually published. Offering a
    locale the manifest lacks is worse than offering no switcher at all,
    because it promises a translation that does not exist.

  Each option carries the href to switch to, so the caller does no path
  arithmetic."
  [decoded location]
  (let [current (:locale/id location)]
    (case (:view/kind location)
      :document
      (let [document (:route/document (:view/route location))
            siblings (if (nil? document)
                       [(:view/route location)]
                       (filter #(= document (:route/document %)) (routes decoded)))
            by-locale (into {} (map (juxt :route/locale identity)) siblings)]
        (vec (for [loc (if (nil? document)
                         (keep :route/locale siblings)
                         (document-locales decoded document))
                   :let [route (get by-locale loc)]]
               {:locale/id loc
                :locale/label (locale/endonym loc)
                :locale/tag (locale/language-tag loc)
                :locale/href (:route/path route)
                :locale/current? (= loc current)})))

      (vec (for [loc locale/supported-locales]
             {:locale/id loc
              :locale/label (locale/endonym loc)
              :locale/tag (locale/language-tag loc)
              :locale/href (locale/locale-root loc)
              :locale/current? (= loc current)})))))

(defn listing
  "Published routes for `loc`, shaped for a listing. `:route/title` is what a
  listing renders; a route without one falls back to its document id."
  [decoded loc]
  (mapv (fn [route]
          {:listing/href (:route/path route)
           :listing/title (:route/title route)
           :listing/document (:route/document route)})
        (sort-by :route/path (routes-for-locale decoded loc))))
