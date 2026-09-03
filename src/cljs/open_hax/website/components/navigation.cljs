(ns open-hax.website.components.navigation
  "Top navigation. Holds no copy and no routing decisions."
  (:require [helix.core :refer [$ defnc]]
            [helix.dom :as d]
            [helix.hooks :as hooks]
            [open-hax.website.components.language-switcher :as switcher]
            [open-hax.website.domain.copy :as copy]
            [open-hax.website.domain.locale :as locale]))

(defnc Navigation [{:keys [locale switcher-options]}]
  (let [[scrolled? set-scrolled] (hooks/use-state false)]
    (hooks/use-effect
     :once
     (let [handler (fn []
                     (set-scrolled (> (.-scrollY js/window) 20)))]
       (.addEventListener js/window "scroll" handler)
       #(.removeEventListener js/window "scroll" handler)))

    (d/header
     {:class-name (str "fixed top-0 left-0 right-0 z-50 transition-all duration-300 "
                       (if scrolled?
                         "bg-slate-950/90 backdrop-blur-md border-b border-slate-800"
                         "bg-transparent"))}
     (d/div {:class-name "max-w-7xl mx-auto px-4 sm:px-6 lg:px-8"}
            (d/div {:class-name "flex items-center justify-between h-16"}
                   (d/a {:href (locale/locale-root locale)
                         :class-name "text-xl font-bold text-white tracking-tight"}
                        (copy/text locale :brand/name))
                   (d/div {:class-name "flex items-center gap-8"}
                          (d/nav {:class-name "hidden md:flex items-center gap-8"}
                                 (d/a {:href "#products" :class-name "text-sm text-slate-300 hover:text-white transition"}
                                      (copy/text locale :nav/products))
                                 (d/a {:href "#graphics" :class-name "text-sm text-slate-300 hover:text-white transition"}
                                      (copy/text locale :nav/graphics))
                                 (d/a {:href "#music" :class-name "text-sm text-slate-300 hover:text-white transition"}
                                      (copy/text locale :nav/music))
                                 (d/a {:href "https://github.com/open-hax" :target "_blank" :rel "noreferrer"
                                       :class-name "text-sm text-slate-300 hover:text-white transition"}
                                      (copy/text locale :link/github)))
                          ($ switcher/LanguageSwitcher {:locale locale
                                                        :options switcher-options})))))))
