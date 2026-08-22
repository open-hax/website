(ns open-hax.website.extern.dom
  "The only namespace that mutates or reads the document outside React.

  Three facts live above the React root and therefore cannot be rendered:
  the `lang` attribute (the static shell already carries the right one, and
  this keeps it right when the client resolves a different locale from the
  path), the dark-theme class, and a `robots` directive for the one page a
  static SPA fallback cannot give a 404 status to."
  (:require [open-hax.website.domain.locale :as locale]))

(def ^:private robots-meta-id "published-robots")

(defn location-path
  []
  (.. js/window -location -pathname))

(defn set-document-lang!
  "Set `lang` on the document element for the locale actually being rendered.

  The per-locale shells ship with the correct `lang` already, so this is a
  correction, not the primary mechanism: it matters when a path resolves to a
  locale other than the shell's — an unknown prefix falling back to the
  default, for instance."
  [loc]
  (set! (.-lang js/document.documentElement) (locale/language-tag loc)))

(defn add-dark-class!
  []
  (.add (.-classList js/document.documentElement) "dark"))

(defn set-robots-noindex!
  "Add or remove `<meta name=\"robots\" content=\"noindex\">`.

  A static SPA fallback answers an unknown path with 200 and the shell, so a
  bad link is indexable unless the client says otherwise. This is the client
  saying otherwise; `docs/decisions/0001-spa-fallback-over-prerendering.md`
  records why the fallback is the chosen trade rather than pre-rendering."
  [noindex?]
  (let [existing (.getElementById js/document robots-meta-id)]
    (cond
      (and noindex? (nil? existing))
      (let [el (.createElement js/document "meta")]
        (set! (.-id el) robots-meta-id)
        (.setAttribute el "name" "robots")
        (.setAttribute el "content" "noindex")
        (.appendChild js/document.head el))

      (and (not noindex?) existing)
      (.remove existing)

      :else nil)))
