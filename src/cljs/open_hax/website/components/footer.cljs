(ns open-hax.website.components.footer
  "Site footer component."
  (:require [helix.core :as hx :refer [defnc]]
            [helix.dom :as d]))

(defnc Footer []
  (d/footer {:class-name "border-t border-slate-800 bg-slate-950 py-12"}
            (d/div {:class-name "max-w-7xl mx-auto px-4 sm:px-6 lg:px-8"}
                   (d/div {:class-name "flex flex-col md:flex-row items-center justify-between gap-4"}
                          (d/p {:class-name "text-sm text-slate-500"}
                                "© " (.getFullYear (js/Date.)) " OpenHax. All rights reserved.")
                          (d/div {:class-name "flex items-center gap-6"}
                                 (d/a {:href "https://github.com/open-hax" :target "_blank"
                                       :class-name "text-sm text-slate-400 hover:text-slate-200 transition"}
                                      "GitHub")
                                 (d/a {:href "https://promethean.rest" :target "_blank"
                                       :class-name "text-sm text-slate-400 hover:text-slate-200 transition"}
                                      "Promethean"))))))
