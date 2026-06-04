(ns open-hax.website.sections.graphics
  "Graphics / art portfolio gallery section."
  (:require [helix.core :as hx :refer [$ defnc]]
            [helix.hooks :as hooks]
            [helix.dom :as d]
            [open-hax.website.data.assets :as assets]))

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

(defnc Lightbox [{:keys [item on-close]}]
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
                             :on-click on-close}
                            "Close")))))

(defnc GraphicsSection []
  (let [[selected-item set-selected] (hooks/use-state nil)]
    (d/div {:class-name "max-w-7xl mx-auto"
            :id "graphics"}
           (d/div {:class-name "text-center mb-16"}
                  (d/h2 {:class-name "text-3xl sm:text-4xl font-bold text-white mb-4"}
                        "Graphics")
                  (d/p {:class-name "text-slate-400 max-w-2xl mx-auto"}
                       (str (count assets/graphics) " visual experiments, generative art, and creative explorations.")))
           (d/div {:class-name "grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-3"}
                  (for [item assets/graphics]
                    ($ GalleryItem {:key (:id item)
                                    :item item
                                    :on-click set-selected})))
           ($ Lightbox {:item selected-item :on-close #(set-selected nil)}))))
