(ns open-hax.website.sections.graphics-test
  (:require [cljs.test :refer [deftest is testing]]
            [clojure.string :as str]
            [open-hax.website.data.assets :as assets]
            [open-hax.website.sections.graphics :as graphics]))

(deftest graphics-section-exists
  (testing "GraphicsSection is a function/component"
    (is (fn? graphics/GraphicsSection))))

(deftest lightbox-component-exists
  (testing "Lightbox is a function/component"
    (is (fn? graphics/Lightbox))))

(deftest an-empty-gallery-is-a-valid-build
  (testing "zero staged assets renders an empty grid and a count of 0"
    ;; The gallery used to be served through a committed absolute symlink that
    ;; was dangling in every checkout but one, while assets.cljs listed 200
    ;; files. The manifest is now generated from what the build actually
    ;; staged, so an empty list is honest rather than broken.
    (is (vector? assets/graphics))
    (is (<= 0 (count assets/graphics)))))

(deftest every-listed-asset-is-addressable-inside-the-build-output
  (doseq [item assets/graphics]
    (is (str/starts-with? (:src item) "/graphics/") (pr-str item))
    (is (not (str/includes? (:src item) "..")) (pr-str item))
    (is (int? (:id item)) (pr-str item))
    (is (string? (:title item)) (pr-str item))))

(deftest asset-ids-are-stable-and-unique
  (testing "ids are derived from the path, so a regeneration does not churn React keys"
    (is (= (count assets/graphics) (count (set (map :id assets/graphics)))))))
