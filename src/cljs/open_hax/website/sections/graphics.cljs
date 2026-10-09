(ns open-hax.website.sections.graphics
  "A featured selection and a browsable gallery of the staged artwork."
  (:require [helix.core :refer [$ defnc]]
            [helix.dom :as d]
            [helix.hooks :as hooks]
            [open-hax.website.data.assets :as assets]
            [open-hax.website.domain.copy :as copy]
            [open-hax.website.domain.gallery :as gallery]
            [open-hax.website.extern.gallery :as browser]))

(defnc GalleryItem [{:keys [locale item on-click]}]
  (d/button {:class-name "group w-full text-left rounded-xl overflow-hidden border border-slate-800 bg-slate-900 hover:border-slate-600 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-cyan-300 focus-visible:ring-offset-4 focus-visible:ring-offset-slate-950 transition"
             :type "button"
             :aria-label (copy/text-with locale :graphics/open {:title (:title item)})
             :on-click #(on-click item)}
            (d/div {:class-name "aspect-square bg-slate-100 overflow-hidden p-3 sm:p-4"}
                   (d/img {:src (:src item)
                           :alt (:title item)
                           :class-name "w-full h-full object-contain transition-transform duration-300 group-hover:scale-[1.03]"
                           :loading "lazy"
                           :decoding "async"}))
            (d/div {:class-name "p-3 sm:p-4"}
                   (d/h4 {:class-name "text-slate-100 font-medium truncate"} (:title item))
                   (d/p {:class-name "text-slate-300 text-xs mt-1 truncate"} (:category item)))))

(defnc Lightbox [{:keys [locale item on-close]}]
  (hooks/use-effect
   [(:id item)]
   (when item
     (browser/activate-dialog! "graphics-lightbox" on-close)))
  (when item
    (d/div {:class-name "fixed inset-0 z-50 flex items-center justify-center bg-slate-950/95 backdrop-blur-sm p-4 sm:p-6 overflow-y-auto"
            :on-click #(when (browser/backdrop-event? %) (on-close))}
           (d/div {:id "graphics-lightbox"
                   :role "dialog"
                   :aria-modal true
                   :aria-labelledby "graphics-lightbox-title"
                   :tab-index -1
                   :class-name "max-w-5xl w-full rounded-2xl bg-slate-900 border border-slate-700 overflow-hidden shadow-2xl"}
                  (d/div {:class-name "flex items-start justify-between gap-4 p-4 sm:p-5"}
                         (d/div {:class-name "min-w-0"}
                                (d/h3 {:id "graphics-lightbox-title"
                                       :class-name "text-lg sm:text-xl font-semibold text-white break-words"}
                                      (:title item))
                                (d/p {:class-name "text-slate-300 text-sm mt-1"} (:category item)))
                         (d/button {:class-name "shrink-0 rounded-lg px-3 py-2 border border-slate-600 text-slate-200 hover:bg-slate-800 hover:text-white focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-cyan-300 transition"
                                    :type "button"
                                    :on-click on-close}
                                   (copy/text locale :graphics/close)))
                  (d/div {:class-name "bg-slate-100 p-3 sm:p-6"}
                         (d/img {:src (:src item)
                                 :alt (:title item)
                                 :class-name "w-full max-h-[55vh] sm:max-h-[65vh] object-contain"}))))))

(defnc GraphicsSection [{:keys [locale]}]
  (let [[selected-item set-selected] (hooks/use-state nil)
        [query set-query] (hooks/use-state "")
        [category set-category] (hooks/use-state "")
        [visible-count set-visible-count] (hooks/use-state gallery/page-size)
        featured (take 12 (gallery/featured-items assets/graphics))
        page (gallery/collection-page assets/graphics query category visible-count)]
    (d/div {:class-name "max-w-7xl mx-auto"}
           (d/div {:class-name "text-center mb-12"}
                  (d/h2 {:class-name "text-3xl sm:text-4xl font-bold text-white mb-4"}
                        (copy/text locale :graphics/heading))
                  (d/p {:class-name "text-slate-300 max-w-2xl mx-auto"}
                       (copy/text-with locale :graphics/lede
                                       {:count (count assets/graphics)})))
           (when (seq featured)
             (d/div {:class-name "mb-14"}
                    (d/h3 {:class-name "text-xl font-semibold text-white mb-5"}
                          (copy/text locale :graphics/featured))
                    (d/div {:class-name "grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4 sm:gap-5"}
                           (for [item featured]
                             ($ GalleryItem {:key (:id item)
                                             :locale locale
                                             :item item
                                             :on-click set-selected})))))
           (d/div {:class-name "flex flex-col sm:flex-row sm:items-end gap-4 mb-6"}
                  (d/h3 {:class-name "text-xl font-semibold text-white sm:mr-auto"}
                        (copy/text locale :graphics/collection))
                  (d/div {:class-name "w-full sm:w-64"}
                         (d/label {:html-for "graphics-search"
                                   :class-name "sr-only"}
                                  (copy/text locale :graphics/search))
                         (d/input {:id "graphics-search"
                                   :type "search"
                                   :value query
                                   :placeholder (copy/text locale :graphics/search)
                                   :class-name "w-full rounded-lg border border-slate-700 bg-slate-900 px-4 py-2.5 text-slate-100 placeholder:text-slate-300 focus:outline-none focus:ring-2 focus:ring-cyan-300"
                                   :on-change #(do (set-query (browser/event-value %))
                                                   (set-visible-count gallery/page-size))}))
                  (d/div {:class-name "w-full sm:w-48"}
                         (d/label {:html-for "graphics-category"
                                   :class-name "sr-only"}
                                  (copy/text locale :graphics/collection))
                         (d/select {:id "graphics-category"
                                    :value category
                                    :class-name "w-full rounded-lg border border-slate-700 bg-slate-900 px-3 py-2.5 text-slate-100 focus:outline-none focus:ring-2 focus:ring-cyan-300"
                                    :on-change #(do (set-category (browser/event-value %))
                                                    (set-visible-count gallery/page-size))}
                                   (d/option {:value ""} (copy/text locale :graphics/all))
                                   (for [value (gallery/categories assets/graphics)]
                                     (d/option {:key value :value value} value)))))
           (d/p {:role "status"
                 :class-name "text-sm text-slate-300 mb-5"}
                (copy/text-with locale :graphics/results
                                {:shown (:shown page) :count (:count page)}))
           (if (seq (:items page))
             (d/div {:class-name "grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-4 sm:gap-5"}
                    (for [item (:items page)]
                      ($ GalleryItem {:key (:id item)
                                      :locale locale
                                      :item item
                                      :on-click set-selected})))
             (d/p {:class-name "rounded-xl border border-slate-800 bg-slate-900/50 px-6 py-12 text-center text-slate-300"}
                  (copy/text locale :graphics/empty)))
           (when (:more? page)
             (d/div {:class-name "text-center mt-8"}
                    (d/button {:type "button"
                               :class-name "rounded-lg border border-slate-600 px-6 py-3 text-slate-100 hover:border-slate-400 hover:bg-slate-800 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-cyan-300 transition"
                               :on-click #(set-visible-count + gallery/page-size)}
                              (copy/text locale :graphics/more))))
           ($ Lightbox {:locale locale
                        :item selected-item
                        :on-close #(set-selected nil)}))))
