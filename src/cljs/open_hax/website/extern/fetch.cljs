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

(defn- request-text
  [path data-file?]
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

                   (and data-file? (html-response? response))
                   {:fetch/status :absent}

                   :else
                   (.then (.text response)
                          (fn [body] {:fetch/status :ok :fetch/body body})))))
        (.catch (fn [err]
                  {:fetch/status :error
                   :fetch/message (or (.-message err) (str err))})))))

(defn text
  "GET a data file, treating an HTML SPA fallback as absent.

  Resolves to `:ok` with `:fetch/body`, `:absent`, `:error` with a message,
  or `:refused` for a non-relative request."
  [path]
  (request-text path true))

(defn artifact-text
  "GET the artifact named by a validated publication route.

  HTML is valid artifact content, so its Content-Type does not mean the file
  is absent. The same relative-path, HTTP-status and request policy applies.
  The static host must return 404 for missing files under `/published/`."
  [path]
  (request-text path false))
