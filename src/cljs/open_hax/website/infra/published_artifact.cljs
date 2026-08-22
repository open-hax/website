(ns open-hax.website.infra.published-artifact
  "The fetch edge for one published artifact.

  The manifest is the authority on what exists, so an artifact is only ever
  requested for a route the manifest names. A route whose artifact is missing
  from disk is a writer defect — the manifest is supposed to be the commit
  point — and is reported as such rather than rendered as an empty document."
  (:require [open-hax.website.domain.published :as published]
            [open-hax.website.extern.fetch :as fetch]))

(defn load!
  "Fetch the artifact for `route`, resolving to

    {:artifact/status :ok      :artifact/body \"...\" :artifact/media-type \"text/html\"}
    {:artifact/status :missing :artifact/url \"...\"}
    {:artifact/status :error   :artifact/message \"...\"}"
  [route]
  (if-let [url (published/artifact-url route)]
    (-> (fetch/text url)
        (.then (fn [{:keys [fetch/status fetch/body fetch/message]}]
                 (case status
                   :ok {:artifact/status :ok
                        :artifact/body body
                        :artifact/media-type (:route/media-type route)}
                   :absent {:artifact/status :missing :artifact/url url}
                   {:artifact/status :error :artifact/message message}))))
    (js/Promise.resolve
     {:artifact/status :error
      :artifact/message (str "Route has no usable artifact reference: "
                             (pr-str (:route/path route)))})))
