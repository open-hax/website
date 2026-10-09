(ns open-hax.website.sections.hero
  "Hero / landing section. Holds no copy: every string is a message key
  resolved for the rendered locale."
  (:require [helix.core :refer [defnc]]
            [helix.dom :as d]
            [open-hax.website.data.assets :as assets]
            [open-hax.website.domain.gallery :as gallery]
            [open-hax.website.domain.copy :as copy]))

(defnc HeroSection [{:keys [locale]}]
  (let [art (first (gallery/featured-items assets/graphics))]
   (d/section
   {:class-name "relative min-h-[85vh] flex items-center justify-center overflow-hidden pt-32 pb-20"}
   (d/div {:class-name "absolute inset-0 bg-gradient-to-br from-slate-950 via-slate-900 to-slate-950"})
   (d/div {:class-name "absolute inset-0 opacity-20"
           :style {:background-image "radial-gradient(circle at 1px 1px, rgba(255,255,255,0.15) 1px, transparent 0)"
                   :background-size "40px 40px"}})
   (d/div {:class-name "relative z-10 max-w-7xl w-full mx-auto px-4 sm:px-6 lg:px-8 grid lg:grid-cols-2 items-center gap-12 lg:gap-20"}
    (d/div
          (d/h1 {:class-name "text-5xl sm:text-7xl font-extrabold tracking-tight text-white mb-6"}
                (copy/text locale :brand/name))
          (d/p {:class-name "text-xl sm:text-3xl text-slate-200 mb-4 max-w-2xl"}
               (copy/text locale :hero/tagline))
          (d/p {:class-name "text-base sm:text-lg leading-relaxed text-slate-300 mb-8 max-w-xl"}
               (copy/text locale :hero/lede))
          (d/div {:class-name "flex flex-wrap items-center gap-4"}
                 (d/a {:href "#graphics"
                       :class-name "px-8 py-3 bg-white text-slate-950 font-semibold rounded-lg hover:bg-slate-100 transition"}
                      (copy/text locale :hero/cta-work))
                 (d/a {:href "https://github.com/riatzukiza" :target "_blank" :rel "noreferrer"
                       :class-name "px-8 py-3 border border-slate-600 text-slate-200 font-semibold rounded-lg hover:border-slate-400 hover:text-white transition"}
                      (copy/text locale :hero/cta-github)))
          (d/div {:class-name "mt-8 pt-6 border-t border-slate-800 text-sm leading-relaxed text-slate-300 max-w-xl"}
                 (d/p (copy/text locale :hero/client-lede))))
    (when art
      (d/a {:href "#graphics"
            :class-name "block rounded-2xl border border-slate-700 bg-slate-900/70 p-4 sm:p-6 shadow-2xl hover:border-slate-500 transition"}
           (d/img {:src (:src art) :alt (:title art)
                   :class-name "w-full aspect-[2/1] object-contain rounded-xl bg-slate-950"
                   :fetchpriority "high"})
           (d/div {:class-name "flex items-center justify-between gap-3 pt-4"}
                  (d/span {:class-name "text-sm text-slate-300"} (:title art))
                  (d/span {:class-name "text-sm text-slate-300"}
                          (copy/text locale :graphics/heading)))))))))
