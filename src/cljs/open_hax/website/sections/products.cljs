(ns open-hax.website.sections.products
  "Product showcase section.

  Product names come from `open-hax.website.data.products` untranslated;
  descriptions and tags come from the locale dictionaries by key."
  (:require [helix.core :refer [$ defnc]]
            [helix.dom :as d]
            [open-hax.website.data.products :as data]
            [open-hax.website.domain.copy :as copy]))

(defnc ProductCard [{:keys [locale product]}]
  (d/a {:href (:product/href product) :target "_blank" :rel "noreferrer"
        :class-name "group block p-6 rounded-xl border border-slate-800 bg-slate-900/50 hover:bg-slate-800/50 hover:border-slate-700 transition-all duration-300"}
       (d/div {:class-name "flex items-start justify-between mb-4"}
              (d/h3 {:class-name "text-xl font-bold text-white group-hover:text-slate-100 transition"}
                    (:product/name product))
              (d/span {:class-name "text-2xl" :aria-hidden true} (:product/icon product)))
       (d/p {:class-name "text-slate-400 text-sm leading-relaxed mb-4"}
            (copy/text locale (:product/description-key product)))
       (d/div {:class-name "flex flex-wrap gap-2"}
              (for [tag-key (:product/tag-keys product)]
                (d/span {:key (str tag-key)
                         :class-name "px-2 py-1 text-xs font-medium rounded-md bg-slate-800 text-slate-300 border border-slate-700"}
                        (copy/text locale tag-key))))))

(defnc ProductsSection [{:keys [locale]}]
  (d/div {:class-name "max-w-7xl mx-auto"}
         (d/div {:class-name "text-center mb-16"}
                (d/h2 {:class-name "text-3xl sm:text-4xl font-bold text-white mb-4"}
                      (copy/text locale :products/heading))
                (d/p {:class-name "text-slate-400 max-w-2xl mx-auto"}
                     (copy/text locale :products/lede)))
         (d/div {:class-name "grid grid-cols-1 md:grid-cols-3 gap-6"}
                (for [product data/products]
                  ($ ProductCard {:key (str (:product/id product))
                                  :locale locale
                                  :product product})))))
