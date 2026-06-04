(ns open-hax.website.sections.hero
  "Hero / landing section."
  (:require [helix.core :as hx :refer [defnc]]
            [helix.dom :as d]))

(defnc HeroSection []
  (d/section
   {:class-name "relative min-h-screen flex items-center justify-center overflow-hidden"}
   (d/div {:class-name "absolute inset-0 bg-gradient-to-br from-slate-950 via-slate-900 to-slate-950"})
   (d/div {:class-name "absolute inset-0 opacity-20"
           :style {:background-image "radial-gradient(circle at 1px 1px, rgba(255,255,255,0.15) 1px, transparent 0)"
                   :background-size "40px 40px"}})
   (d/div {:class-name "relative z-10 max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 text-center"}
          (d/h1 {:class-name "text-5xl sm:text-7xl font-extrabold tracking-tight text-white mb-6"}
                "OpenHax")
          (d/p {:class-name "text-xl sm:text-2xl text-slate-300 mb-4 max-w-2xl mx-auto"}
               "Art, Music, and Programs")
          (d/p {:class-name "text-base text-slate-400 mb-10 max-w-xl mx-auto"}
               "A collective of builders, artists, and musicians pushing the boundaries of creative technology.")
          (d/div {:class-name "flex flex-col sm:flex-row items-center justify-center gap-4"}
                 (d/a {:href "#products"
                       :class-name "px-8 py-3 bg-white text-slate-950 font-semibold rounded-lg hover:bg-slate-100 transition"}
                      "Explore Our Work")
                 (d/a {:href "https://github.com/open-hax" :target "_blank"
                       :class-name "px-8 py-3 border border-slate-600 text-slate-200 font-semibold rounded-lg hover:border-slate-400 hover:text-white transition"}
                      "View on GitHub")))))
