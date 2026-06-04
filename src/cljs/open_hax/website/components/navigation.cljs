(ns open-hax.website.components.navigation
  "Top navigation component."
  (:require [helix.core :as hx :refer [defnc]]
            [helix.hooks :as hooks]
            [helix.dom :as d]))

(defnc Navigation []
  (let [[scrolled? set-scrolled] (hooks/use-state false)]
    (hooks/use-effect
     :once
     (fn []
       (let [handler (fn []
                       (set-scrolled (> (.-scrollY js/window) 20)))]
         (.addEventListener js/window "scroll" handler)
         #(js/window.removeEventListener "scroll" handler))))

    (d/header
     {:class-name (str "fixed top-0 left-0 right-0 z-50 transition-all duration-300 "
                       (if scrolled?
                         "bg-slate-950/90 backdrop-blur-md border-b border-slate-800"
                         "bg-transparent"))}
     (d/div {:class-name "max-w-7xl mx-auto px-4 sm:px-6 lg:px-8"}
            (d/div {:class-name "flex items-center justify-between h-16"}
                   (d/a {:href "#" :class-name "text-xl font-bold text-white tracking-tight"}
                        "OpenHax")
                   (d/nav {:class-name "hidden md:flex items-center gap-8"}
                          (d/a {:href "#products" :class-name "text-sm text-slate-300 hover:text-white transition"}
                               "Products")
                          (d/a {:href "#graphics" :class-name "text-sm text-slate-300 hover:text-white transition"}
                               "Graphics")
                          (d/a {:href "#music" :class-name "text-sm text-slate-300 hover:text-white transition"}
                               "Music")
                          (d/a {:href "https://github.com/open-hax" :target "_blank"
                                :class-name "text-sm text-slate-300 hover:text-white transition"}
                               "GitHub")))))))
