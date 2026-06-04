(ns open-hax.website.sections.products-test
  (:require [cljs.test :refer [deftest is testing]]
            [open-hax.website.sections.products :as products]))

(deftest products-section-exists
  (testing "ProductsSection is a function/component"
    (is (fn? products/ProductsSection))))

(deftest product-card-exists
  (testing "ProductCard is a function/component"
    (is (fn? products/ProductCard))))
