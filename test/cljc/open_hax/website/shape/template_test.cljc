(ns open-hax.website.shape.template-test
  (:require [clojure.test :refer [deftest is testing]]
            [open-hax.website.shape.template :as template]))

(deftest placeholders-are-extracted
  (is (= #{} (template/placeholders "no holes here")))
  (is (= #{:count} (template/placeholders "{count} items")))
  (is (= #{:year} (template/placeholders "© {year} OpenHax.")))
  (is (= #{:count :year} (template/placeholders "{count} in {year}")))
  (testing "a brace that is not a placeholder is not one"
    (is (= #{} (template/placeholders "{Count}")))
    (is (= #{} (template/placeholders "{ count }")))))

(deftest render-fills-holes
  (is (= "12 items" (template/render "{count} items" {:count 12})))
  (is (= "0 items" (template/render "{count} items" {:count 0})))
  (testing "the same placeholder may appear more than once"
    (is (= "1 of 1" (template/render "{n} of {n}" {:n 1})))))

(deftest render-leaves-an-unsupplied-hole-visible
  (testing "a visible {count} is a bug report; an invisible one is a lie"
    (is (= "{count} items" (template/render "{count} items" {})))
    (is (= "{count} items" (template/render "{count} items" {:other 3})))))

(deftest render-preserves-word-order
  (testing "interpolation happens inside the sentence, so each locale keeps its own order"
    (is (= "7 visuelle Experimente." (template/render "{count} visuelle Experimente." {:count 7})))
    (is (= "7 点の実験。" (template/render "{count} 点の実験。" {:count 7})))))
