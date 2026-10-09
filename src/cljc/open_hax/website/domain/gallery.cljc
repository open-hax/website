(ns open-hax.website.domain.gallery
  "Pure selection and paging of the staged artwork manifest."
  (:require [clojure.string :as str]))

(def page-size 24)

(defn featured-items
  "Explicitly featured artwork in its editorial order, without a view limit."
  [items]
  (->> items
       (filter :featured?)
       (sort-by (juxt #(or (:feature-order %) 2147483647) :title :id))
       vec))

(defn categories
  "Nonempty filename-derived categories available in this collection."
  [items]
  (->> items
       (keep :category)
       (remove str/blank?)
       distinct
       sort
       vec))

(defn- normalized
  [value]
  (str/lower-case (str/trim (or value ""))))

(defn matching-items
  "Match every search term against title and category, and an exact category.

  Search and filtering preserve the manifest's order. Empty queries and a
  missing category select the whole collection."
  [items query category]
  (let [terms (remove str/blank? (str/split (normalized query) #"\s+"))]
    (filterv
     (fn [item]
       (and (or (str/blank? category) (= category (:category item)))
            (let [searchable (normalized (str (:title item) " " (:category item)))]
              (every? #(str/includes? searchable %) terms))))
     items)))

(defn collection-page
  "A bounded visible prefix plus counts for a filtered collection."
  [items query category visible-count]
  (let [matches (matching-items items query category)
        total (count matches)
        shown (min total (max 0 visible-count))]
    {:items (subvec matches 0 shown)
     :count total
     :shown shown
     :more? (< shown total)}))
