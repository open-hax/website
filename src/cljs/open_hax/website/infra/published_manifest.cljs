(ns open-hax.website.infra.published-manifest
  "The fetch edge for the published-content manifest. Thin by design.

  Everything this namespace decides is one of: which URL, when to read it, and
  which of `open-hax.website.law.published-manifest`'s outcomes an I/O result
  maps to. Every rule about the manifest's shape lives in that law namespace,
  where it is testable without a browser.

  Read ONCE at load. The manifest is a published fact, not a live feed, and
  polling it would turn a static site into a client that needs the writer to be
  up. The site keeps serving whatever it read; a new publication becomes
  visible on the next page load."
  (:require [cljs.reader :as reader]
            [clojure.string :as str]
            [open-hax.website.domain.published :as published]
            [open-hax.website.extern.fetch :as fetch]
            [open-hax.website.law.published-manifest :as law]))

(defn- parse
  "Read the manifest bytes as EDN.

  Two shapes of non-EDN are named rather than left to the reader, because both
  are things a static host does rather than things a writer does:

  - blank bytes. An atomic write-beside-then-rename cannot produce a zero-byte
    manifest, so an empty file means the write failed. That is not the same
    fact as 'nothing is published' and must not render like it.
  - markup. A host answering a missing file with its SPA fallback shell hands
    back HTML, and `<!doctype` happens to read as a valid EDN symbol, so
    without this check a fallback shell would be reported as 'a manifest that
    is not a map' and nobody would guess why."
  [body]
  (cond
    (str/blank? body)
    {:parse/status :error
     :parse/message "The manifest is empty. A zero-byte manifest is a failed write, not an empty publication set."}

    (str/starts-with? (str/triml body) "<")
    {:parse/status :error
     :parse/message "The manifest response is markup, not EDN. A static host answering with its SPA fallback shell looks like this; exclude the content root from the fallback."}

    :else
    (try
      {:parse/status :ok :parse/value (reader/read-string body)}
      (catch :default err
        {:parse/status :error :parse/message (or (.-message err) (str err))}))))

(defn decode-fetch
  "Map a `open-hax.website.extern.fetch/text` result onto a decoded manifest.

  Pure given the fetch result, so the mapping itself is testable."
  [{:keys [fetch/status fetch/body fetch/message]}]
  (case status
    :absent (law/decode law/absent)

    :ok (let [parsed (parse body)]
          (if (= :ok (:parse/status parsed))
            (law/decode (:parse/value parsed))
            (law/edge-failure :manifest.error/unparseable
                              "The manifest is not readable EDN."
                              {:error/message (:parse/message parsed)})))

    (law/edge-failure :manifest.error/unreadable
                      "The manifest could not be read."
                      {:error/message message
                       :error/fetch-status status})))

(defn load!
  "Read the manifest once, resolving to a decoded value that is always safe to
  render: `:ok` with no routes when there is nothing published yet, `:invalid`
  with errors when the writer produced something this reader will not serve."
  []
  (-> (fetch/text published/manifest-url)
      (.then (fn [result]
               (let [decoded (decode-fetch result)]
                 (when-not (law/valid? decoded)
                   (js/console.error "[open-hax-website] invalid published content manifest"
                                     (pr-str (:published/errors decoded))))
                 decoded)))))
