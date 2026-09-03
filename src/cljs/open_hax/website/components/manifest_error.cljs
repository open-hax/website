(ns open-hax.website.components.manifest-error
  "The loud failure for an invalid published-content manifest.

  The contract requires a malformed manifest to fail loudly rather than render
  a blank page, and this is the chosen shape of that loudness: a pinned,
  unmissable banner naming the errors, above a site that otherwise keeps
  working. The site's own sections do not depend on the manifest, so a writer
  defect in Knoxx must not become an outage of OpenHax's own pages — but it
  also must not be invisible, which is the failure mode the contract names.

  The errors are printed as data because the reader of this banner is whoever
  is about to go fix the writer."
  (:require [helix.core :refer [defnc]]
            [helix.dom :as d]
            [open-hax.website.domain.copy :as copy]))

(defnc ManifestErrorBanner [{:keys [locale errors]}]
  (d/div {:role "alert"
          :class-name "fixed bottom-0 left-0 right-0 z-[60] border-t-2 border-red-500 bg-red-950/95 px-4 py-3 text-left"}
         (d/p {:class-name "text-sm font-semibold text-red-100"}
              (copy/text locale :manifest/error-title))
         (d/p {:class-name "text-xs text-red-200/90 mt-1"}
              (copy/text locale :manifest/error-body))
         (d/ul {:class-name "mt-2 space-y-1"}
               (for [[idx {:keys [error/kind error/message error/route-index]}] (map-indexed vector errors)]
                 (d/li {:key (str idx)
                        :class-name "font-mono text-[11px] text-red-200/80"}
                       (str kind
                            (when route-index (str " [route " route-index "]"))
                            " — " message))))))
