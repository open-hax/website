(ns open-hax.website.extern.fetch
  "The only namespace that touches `js/fetch`.

  Returns CLJS data, so no caller has to hold a `Response` object or know that
  a promise is involved beyond `.then`. Two rules live here because this is
  the only place they can be enforced:

  1. A request path must be relative and same-origin. The epic's one
     prohibition is that no request from the site reaches a Knoxx origin, and
     a prohibition enforced by convention is enforced nowhere. A non-relative
     path is refused without a request being made.
  2. An HTML response to a request for a data file means the static server
     answered with its SPA fallback shell, which is how a static host says
     'no such file'. That is `:absent`, not a parse error — otherwise every
     deploy before the first publication reports a corrupt manifest."
  (:require [clojure.string :as str]
            [open-hax.website.law.published-manifest :as law]))

(defn- html-response?
  [response]
  (let [content-type (or (.get (.-headers response) "content-type") "")]
    (str/starts-with? (str/lower-case content-type) "text/html")))

(defn text
  "GET `path`, resolving to one of

    {:fetch/status :ok      :fetch/body \"...\"}
    {:fetch/status :absent}
    {:fetch/status :error   :fetch/message \"...\" :fetch/http-status n}
    {:fetch/status :refused :fetch/message \"...\"}"
  [path]
  (if-not (law/relative-path? path)
    (js/Promise.resolve
     {:fetch/status :refused
      :fetch/message (str "Refusing a non-relative fetch: " (pr-str path))})
    (-> (js/fetch path #js {:cache "no-store" :credentials "omit"})
        (.then (fn [response]
                 (cond
                   (= 404 (.-status response))
                   {:fetch/status :absent}

                   (not (.-ok response))
                   {:fetch/status :error
                    :fetch/http-status (.-status response)
                    :fetch/message (str "HTTP " (.-status response) " for " path)}

                   (html-response? response)
                   {:fetch/status :absent}

                   :else
                   (.then (.text response)
                          (fn [body] {:fetch/status :ok :fetch/body body})))))
        (.catch (fn [err]
                  {:fetch/status :error
                   :fetch/message (or (.-message err) (str err))})))))
