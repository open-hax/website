(ns open-hax.website.components.footer
  "Site footer.

  The copyright line is a template, not a concatenation: `{year}` sits inside
  the translated sentence so each locale keeps its own word order and its own
  punctuation."
  (:require [helix.core :refer [defnc]]
            [helix.dom :as d]
            [open-hax.website.domain.copy :as copy]))

(defnc Footer [{:keys [locale year]}]
  (d/footer {:class-name "border-t border-slate-800 bg-slate-950 py-12"}
            (d/div {:class-name "max-w-7xl mx-auto px-4 sm:px-6 lg:px-8"}
                   (d/div {:class-name "flex flex-col md:flex-row items-center justify-between gap-4"}
                          (d/p {:class-name "text-sm text-slate-500"}
                               (copy/text-with locale :footer/copyright {:year year}))
                          (d/div {:class-name "flex items-center gap-6"}
                                 (d/a {:href "https://github.com/open-hax" :target "_blank" :rel "noreferrer"
                                       :class-name "text-sm text-slate-400 hover:text-slate-200 transition"}
                                      (copy/text locale :link/github))
                                 (d/a {:href "https://promethean.rest" :target "_blank" :rel "noreferrer"
                                       :class-name "text-sm text-slate-400 hover:text-slate-200 transition"}
                                      (copy/text locale :brand/promethean)))))))
