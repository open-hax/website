(ns open-hax.website.sections.graphics
  "Graphics / art portfolio gallery section.

  The gallery renders whatever `open-hax.website.data.assets` was generated
  from at build time, which is whatever the build actually staged. Zero staged
  assets is a valid build: the grid is empty and the count sentence reads `0`,
  which is honest. See `AGENTS.md`, 'Assets are staged, never symlinked'."
  (:require [helix.core :refer [$ defnc]]
            [helix.dom :as d]
            [helix.hooks :as hooks]
            [open-hax.website.data.assets :as assets]
            [open-hax.website.domain.copy :as copy]))

(defnc GalleryItem [{:keys [item on-click]}]
  (d/div {:class-name "group relative aspect-square rounded-lg overflow-hidden cursor-pointer"
          :on-click #(on-click item)}
         (d/img {:src (:src item)
                 :alt (:title item)
                 :class-name "absolute inset-0 w-full h-full object-cover"
                 :loading "lazy"})
         (d/div {:class-name "absolute inset-0 bg-gradient-to-t from-slate-950/90 via-transparent to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-300"})
         (d/div {:class-name "absolute bottom-0 left-0 right-0 p-4 translate-y-2 group-hover:translate-y-0 opacity-0 group-hover:opacity-100 transition-all duration-300"}
                (d/h4 {:class-name "text-white font-semibold truncate"} (:title item))
                (d/p {:class-name "text-slate-300 text-sm truncate"} (:category item)))))

(defnc Lightbox [{:keys [locale item on-close]}]
  (when item
    (d/div {:class-name "fixed inset-0 z-50 flex items-center justify-center bg-slate-950/95 backdrop-blur-sm"
            :on-click #(when (= (.-target %) (.-currentTarget %))
                         (on-close))}
           (d/div {:class-name "max-w-4xl w-full mx-4"}
                  (d/img {:src (:src item)
                          :alt (:title item)
                          :class-name "w-full max-h-[70vh] object-contain rounded-lg mb-4"})
                  (d/h3 {:class-name "text-2xl font-bold text-white mb-2"} (:title item))
                  (d/p {:class-name "text-slate-400"} (:category item))
                  (d/button {:class-name "mt-4 text-slate-300 hover:text-white transition"
                             :type "button"
                             :on-click on-close}
                            (copy/text locale :graphics/close))))))

(defnc GraphicsSection [{:keys [locale]}]
  (let [[selected-item set-selected] (hooks/use-state nil)]
    (d/div {:class-name "max-w-7xl mx-auto"}
           (d/div {:class-name "text-center mb-16"}
                  (d/h2 {:class-name "text-3xl sm:text-4xl font-bold text-white mb-4"}
                        (copy/text locale :graphics/heading))
                  (d/p {:class-name "text-slate-400 max-w-2xl mx-auto"}
                       (copy/text-with locale :graphics/lede
                                       {:count (count assets/graphics)})))
           (d/div {:class-name "grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-3"}
                  (for [item assets/graphics]
                    ($ GalleryItem {:key (:id item)
                                    :item item
                                    :on-click set-selected})))
           ($ Lightbox {:locale locale
                        :item selected-item
                        :on-close #(set-selected nil)}))))
