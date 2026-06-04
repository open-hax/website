(ns open-hax.website.sections.graphics-test
  (:require [cljs.test :refer [deftest is testing]]
            [open-hax.website.sections.graphics :as graphics]
            [open-hax.website.data.assets :as assets]))

(deftest graphics-section-exists
  (testing "GraphicsSection is a function/component"
    (is (fn? graphics/GraphicsSection))))

(deftest assets-graphics-count
  (testing "assets has graphics loaded"
    (is (> (count assets/graphics) 0))))

(deftest lightbox-component-exists
  (testing "Lightbox is a function/component"
    (is (fn? graphics/Lightbox))))
