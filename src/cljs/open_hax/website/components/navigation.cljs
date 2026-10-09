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
            (d/div {:class-name "flex flex-col items-start gap-2 py-3 sm:py-0 sm:flex-row sm:items-center sm:justify-between sm:h-16"}
                   (d/a {:href (locale/locale-root locale)
                         :class-name "text-xl font-bold text-white tracking-tight"}
                        (copy/text locale :brand/name))
                   (d/div {:class-name "flex items-center gap-4 xl:gap-8 max-w-full"}
                          (d/nav {:class-name "hidden xl:flex items-center gap-6"}
                                 (d/a {:href "#products" :class-name "text-sm text-slate-300 hover:text-white transition"}
                                      (copy/text locale :nav/products))
                                 (d/a {:href "#graphics" :class-name "text-sm text-slate-300 hover:text-white transition"}
                                      (copy/text locale :nav/graphics))
                                 (d/a {:href "#music" :class-name "text-sm text-slate-300 hover:text-white transition"}
                                      (copy/text locale :nav/music))
                                 (d/a {:href "https://github.com/open-hax" :target "_blank" :rel "noreferrer"
                                       :class-name "text-sm text-slate-300 hover:text-white transition"}
                                      (copy/text locale :link/github))
                                 (d/a {:href "https://github.com/riatzukiza" :target "_blank" :rel "noreferrer"
                                       :class-name "text-sm px-3 py-2 rounded-lg border border-slate-700 text-slate-200 hover:border-slate-400 transition"}
                                      (copy/text locale :hero/cta-github)))
                          ($ switcher/LanguageSwitcher {:locale locale
                                                        :options switcher-options})))
            (d/nav {:class-name "xl:hidden flex flex-wrap justify-between gap-x-4 gap-y-2 pb-3 text-sm"}
                   (d/a {:href "#products" :class-name "text-slate-300"} (copy/text locale :nav/products))
                   (d/a {:href "#graphics" :class-name "text-slate-300"} (copy/text locale :nav/graphics))
                   (d/a {:href "#music" :class-name "text-slate-300"} (copy/text locale :nav/music))
                   (d/a {:href "https://github.com/riatzukiza" :target "_blank" :rel "noreferrer"
                         :class-name "text-slate-200"}
                        (copy/text locale :link/github)))))))
