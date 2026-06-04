(ns open-hax.website.core
  "shadow-cljs browser entrypoint for the OpenHax portfolio website."
  (:require [open-hax.website.app :as app]))

(defn ^:dev/after-load after-load []
  (js/console.log "[open-hax-website] hot reload")
  (app/mount!))

(defn ^:dev/once init []
  (js/console.log "[open-hax-website] initializing")
  (app/mount!))
