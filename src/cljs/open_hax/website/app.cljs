(ns open-hax.website.app
  "Main application shell for the OpenHax portfolio website."
  (:require [helix.core :as hx :refer [$ defnc]]
            [helix.hooks :as hooks]
            [helix.dom :as d]
            [open-hax.website.components.navigation :as nav]
            [open-hax.website.components.footer :as footer]
            [open-hax.website.sections.hero :as hero]
            [open-hax.website.sections.products :as products]
            [open-hax.website.sections.graphics :as graphics]
            [open-hax.website.sections.music :as music]
            ["react-dom/client" :as rdom]))

(defnc App []
  (hooks/use-effect
   :once
   (fn []
     (.add (.-classList js/document.documentElement) "dark")
     js/undefined))

  (d/div {:class-name "min-h-screen bg-slate-950 text-slate-100 font-sans"}
         ($ nav/Navigation)
         (d/main
          (d/div {:id "hero"}
                 ($ hero/HeroSection))
          (d/div {:id "products" :class-name "py-24 px-4 sm:px-6 lg:px-8"}
                 ($ products/ProductsSection))
          (d/div {:id "graphics" :class-name "py-24 px-4 sm:px-6 lg:px-8 bg-slate-900/50"}
                 ($ graphics/GraphicsSection))
          (d/div {:id "music" :class-name "py-24 px-4 sm:px-6 lg:px-8"}
                 ($ music/MusicSection)))
         ($ footer/Footer)))

(defonce root-instance* (atom nil))

(defn mount! []
  (let [root-el (.getElementById js/document "root")]
    (when-not root-el
      (throw (js/Error. "Missing #root element")))
    (when-not @root-instance*
      (reset! root-instance* (.createRoot rdom root-el)))
    (.render @root-instance* ($ App))))
