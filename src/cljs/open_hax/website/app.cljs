(ns open-hax.website.app
  "Application shell: resolve the location, render the view it names.

  The shell holds two pieces of state and no copy. The decoded manifest starts
  as `absent`, which is a fully valid state that renders the site's own
  sections, so the first paint never waits on I/O and a deploy with no content
  root behaves exactly like a deploy with an empty one.

  Every string reaching the DOM comes from `open-hax.website.domain.copy`;
  every path decision comes from `open-hax.website.domain.published`. This
  namespace only wires them to React."
  (:require ["react-dom/client" :as rdom]
            [helix.core :refer [$ defnc]]
            [helix.dom :as d]
            [helix.hooks :as hooks]
            [open-hax.website.components.footer :as footer]
            [open-hax.website.components.manifest-error :as manifest-error]
            [open-hax.website.components.navigation :as nav]
            [open-hax.website.domain.published :as published]
            [open-hax.website.extern.dom :as dom]
            [open-hax.website.infra.published-manifest :as manifest]
            [open-hax.website.law.published-manifest :as law]
            [open-hax.website.sections.graphics :as graphics]
            [open-hax.website.sections.hero :as hero]
            [open-hax.website.sections.music :as music]
            [open-hax.website.sections.products :as products]
            [open-hax.website.sections.published :as published-views]))

(defnc SiteSections
  "The site's OWN content. Compiled in, always present, never published
  through the publication seam — the epic's non-goals are explicit that these
  sections do not migrate into Knoxx."
  [{:keys [locale manifest]}]
  (d/main
   (d/div {:id "hero"}
          ($ hero/HeroSection {:locale locale}))
   (d/div {:id "products" :class-name "py-24 px-4 sm:px-6 lg:px-8"}
          ($ products/ProductsSection {:locale locale}))
   (d/div {:id "graphics" :class-name "py-24 px-4 sm:px-6 lg:px-8 bg-slate-900/50"}
          ($ graphics/GraphicsSection {:locale locale}))
   (d/div {:id "music" :class-name "py-24 px-4 sm:px-6 lg:px-8"}
          ($ music/MusicSection {:locale locale}))
   (d/div {:id "published" :class-name "px-4 sm:px-6 lg:px-8 pb-24"}
          ($ published-views/PublishedListing {:locale locale
                                               :manifest manifest}))))

(defnc App []
  (let [[manifest-state set-manifest] (hooks/use-state (law/decode law/absent))
        [path set-path] (hooks/use-state (dom/location-path))
        location (published/resolve-location manifest-state path)
        loc (:locale/id location)
        view (:view/kind location)]

    (hooks/use-effect
     :once
     (dom/add-dark-class!)
     (-> (manifest/load!) (.then set-manifest))
     (let [handler (fn [] (set-path (dom/location-path)))]
       (.addEventListener js/window "popstate" handler)
       #(.removeEventListener js/window "popstate" handler)))

    (hooks/use-effect
     [loc]
     (dom/set-document-lang! loc)
     js/undefined)

    (hooks/use-effect
     [view]
     (dom/set-robots-noindex! (= :not-found view))
     js/undefined)

    (d/div {:class-name "min-h-screen bg-slate-950 text-slate-100 font-sans"}
           ($ nav/Navigation
              {:locale loc
               :switcher-options (published/switcher-options manifest-state location)})
           (case view
             :document ($ published-views/PublishedDocument
                          {:locale loc
                           :route (:view/route location)})
             :not-found ($ published-views/PublishedNotFound {:locale loc})
             ($ SiteSections {:locale loc :manifest manifest-state}))
           ($ footer/Footer {:locale loc
                             :year (.getFullYear (js/Date.))})
           (when-not (law/valid? manifest-state)
             ($ manifest-error/ManifestErrorBanner
                {:locale loc
                 :errors (:published/errors manifest-state)})))))

(defonce root-instance* (atom nil))

(defn mount! []
  (let [root-el (.getElementById js/document "root")]
    (when-not root-el
      (throw (js/Error. "Missing #root element")))
    (when-not @root-instance*
      (reset! root-instance* (.createRoot rdom root-el)))
    (.render @root-instance* ($ App))))
