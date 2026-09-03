(ns open-hax.website.domain.locale
  "The site's locale model and its path grammar. Pure and portable.

  Two things are decided here and nowhere else:

  1. Which locales this build carries copy for. These always exist, because
     their dictionaries are compiled into the artifact. This is NOT the set of
     locales that carry published documents — see
     `open-hax.website.domain.published/switcher-options` for that distinction,
     which is the one that is easy to get wrong.
  2. How a locale maps to a URL prefix. The default locale serves at `/`;
     every other locale takes a path prefix. An unknown prefix resolves to the
     default locale rather than 404ing, because a stale or guessed link should
     land somewhere readable."
  (:require [clojure.string :as str]))

(def default-locale :en)

(def supported-locales
  "Ordered: the language switcher renders them in this order."
  [:en :es :fr :de :ja])

(def supported-locale-set (set supported-locales))

(def language-tags
  "BCP-47 tags for `lang` and `hreflang`."
  {:en "en" :es "es" :fr "fr" :de "de" :ja "ja"})

(def endonyms
  "Each language named in itself. Endonyms are locale-invariant — a switcher
  that translates its own options is harder to use, not easier — so they are
  data here rather than message keys in the per-locale dictionaries."
  {:en "English" :es "Español" :fr "Français" :de "Deutsch" :ja "日本語"})

(defn supported?
  [locale]
  (contains? supported-locale-set locale))

(defn language-tag
  [locale]
  (get language-tags locale (get language-tags default-locale)))

(defn endonym
  [locale]
  (get endonyms locale (name locale)))

(defn locale-prefix
  "The URL prefix owned by `locale`: `\"\"` for the default, `\"/es\"` otherwise."
  [locale]
  (if (= locale default-locale)
    ""
    (str "/" (name locale))))

(defn locale-root
  "The locale's own home path: `\"/\"` for the default, `\"/es/\"` otherwise."
  [locale]
  (if (= locale default-locale)
    "/"
    (str "/" (name locale) "/")))

(defn locale-code-shaped?
  "True when `segment` looks like a language code, whether or not this build
  supports it. `pt` and `zh-hans` are code-shaped; `notes` is not. The
  distinction is what lets an unknown *locale* prefix fall back to the default
  locale while an unknown *document* path stays a not-found."
  [segment]
  (some? (re-matches #"[a-z]{2}(-[a-z0-9]{2,8})?" (str segment))))

(defn- segments
  [path]
  (->> (str/split (str path) #"/")
       (remove str/blank?)
       vec))

(defn normalize-path
  "A leading slash, no trailing slash except for the root, no empty segments."
  [path]
  (let [segs (segments path)]
    (if (seq segs)
      (str "/" (str/join "/" segs))
      "/")))

(defn resolve-path
  "Split a request path into the locale that owns it and the path within that
  locale.

  Returns `{:locale/id, :locale/fallback?, :location/path}` where
  `:location/path` is normalized and locale-free, and `:locale/fallback?` marks
  a prefix that looked like a locale but is not one this build carries."
  [path]
  (let [segs (segments path)
        head (first segs)
        head-kw (when head (keyword head))]
    (cond
      (and head-kw (supported? head-kw) (not= head-kw default-locale))
      {:locale/id head-kw
       :locale/fallback? false
       :location/path (normalize-path (str/join "/" (rest segs)))}

      (and head (locale-code-shaped? head) (not= head-kw default-locale))
      {:locale/id default-locale
       :locale/fallback? true
       :location/path (normalize-path (str/join "/" (rest segs)))}

      (= head-kw default-locale)
      {:locale/id default-locale
       :locale/fallback? false
       :location/path (normalize-path (str/join "/" (rest segs)))}

      :else
      {:locale/id default-locale
       :locale/fallback? false
       :location/path (normalize-path path)})))

(defn localize-path
  "The public path at which `locale` serves `path`. Inverse of `resolve-path`
  for every path that survives normalization."
  [locale path]
  (let [within (normalize-path path)]
    (if (= "/" within)
      (locale-root locale)
      (str (locale-prefix locale) within))))
