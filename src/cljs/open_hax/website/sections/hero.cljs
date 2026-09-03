(ns open-hax.website.sections.hero
  "Hero / landing section. Holds no copy: every string is a message key
  resolved for the rendered locale."
  (:require [helix.core :refer [defnc]]
            [helix.dom :as d]
            [open-hax.website.domain.copy :as copy]))

(defnc HeroSection [{:keys [locale]}]
  (d/section
   {:class-name "relative min-h-screen flex items-center justify-center overflow-hidden"}
   (d/div {:class-name "absolute inset-0 bg-gradient-to-br from-slate-950 via-slate-900 to-slate-950"})
   (d/div {:class-name "absolute inset-0 opacity-20"
           :style {:background-image "radial-gradient(circle at 1px 1px, rgba(255,255,255,0.15) 1px, transparent 0)"
                   :background-size "40px 40px"}})
   (d/div {:class-name "relative z-10 max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 text-center"}
          (d/h1 {:class-name "text-5xl sm:text-7xl font-extrabold tracking-tight text-white mb-6"}
                (copy/text locale :brand/name))
          (d/p {:class-name "text-xl sm:text-2xl text-slate-300 mb-4 max-w-2xl mx-auto"}
               (copy/text locale :hero/tagline))
          (d/p {:class-name "text-base text-slate-400 mb-10 max-w-xl mx-auto"}
               (copy/text locale :hero/lede))
          (d/div {:class-name "flex flex-col sm:flex-row items-center justify-center gap-4"}
                 (d/a {:href "#products"
                       :class-name "px-8 py-3 bg-white text-slate-950 font-semibold rounded-lg hover:bg-slate-100 transition"}
                      (copy/text locale :hero/cta-work))
                 (d/a {:href "https://github.com/open-hax" :target "_blank" :rel "noreferrer"
                       :class-name "px-8 py-3 border border-slate-600 text-slate-200 font-semibold rounded-lg hover:border-slate-400 hover:text-white transition"}
                      (copy/text locale :hero/cta-github))))))
