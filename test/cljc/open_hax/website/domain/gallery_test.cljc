(ns open-hax.website.domain.gallery-test
  (:require [clojure.test :refer [deftest is testing]]
            [open-hax.website.domain.gallery :as gallery]))

(def artworks
  [{:id 1 :title "Blue spiral" :category "ink"}
   {:id 2 :title "Spiral study" :category "paint" :featured? true :feature-order 2}
   {:id 3 :title "Red horizon" :category "paint" :featured? true :feature-order 1}
   {:id 4 :title "Ink horizon" :category ""}])

(deftest featured-selection-is-explicit-and-editorially-ordered
  (is (= [3 2] (mapv :id (gallery/featured-items artworks))))
  (testing "the domain does not impose the gallery view's twelve-item limit"
    (is (= 15 (count (gallery/featured-items
                     (mapv #(hash-map :id % :title (str %) :featured? true
                                     :feature-order %)
                           (range 15))))))))

(deftest category-options-come-from-the-collection
  (is (= ["ink" "paint"] (gallery/categories artworks)))
  (is (= [] (gallery/categories []))))

(deftest search-is-case-insensitive-and-requires-every-term
  (is (= [1 2] (mapv :id (gallery/matching-items artworks " SPIRAL " nil))))
  (testing "a term can match a category while another matches the title"
    (is (= [2] (mapv :id (gallery/matching-items artworks "paint spiral" "")))))
  (is (= [] (gallery/matching-items artworks "blue horizon" nil))))

(deftest category-and-search-both-apply
  (is (= [2] (mapv :id (gallery/matching-items artworks "spiral" "paint"))))
  (is (= [2 3] (mapv :id (gallery/matching-items artworks "" "paint"))))
  (is (= artworks (gallery/matching-items artworks nil nil)))
  (is (= [] (gallery/matching-items artworks "" "unknown"))))

(deftest paging-retains-order-and-honest-counts
  (let [items (mapv #(hash-map :id % :title (str "Work " %) :category "archive")
                    (range 55))
        first-page (gallery/collection-page items "" nil gallery/page-size)
        next-page (gallery/collection-page items "" nil (* 2 gallery/page-size))
        final-page (gallery/collection-page items "" nil (* 3 gallery/page-size))]
    (is (= {:count 55 :shown 24 :more? true} (dissoc first-page :items)))
    (is (= (vec (range 24)) (mapv :id (:items first-page))))
    (is (= (vec (range 48)) (mapv :id (:items next-page))))
    (is (= {:count 55 :shown 55 :more? false} (dissoc final-page :items)))
    (is (= items (:items final-page)))))

(deftest empty-and-bounded-pages
  (is (= {:items [] :count 0 :shown 0 :more? false}
         (gallery/collection-page artworks "no-match" nil gallery/page-size)))
  (is (= {:items [] :count 4 :shown 0 :more? true}
         (gallery/collection-page artworks "" nil -1)))
  (is (= {:items artworks :count 4 :shown 4 :more? false}
         (gallery/collection-page artworks "" nil 100))))
