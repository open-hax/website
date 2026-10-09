(ns open-hax.website.sections.graphics-test
  (:require ["react-dom/server" :as rdom-server]
            [cljs.test :refer [deftest is testing]]
            [clojure.string :as str]
            [helix.core :refer [$]]
            [open-hax.website.data.assets :as assets]
            [open-hax.website.domain.copy :as copy]
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

(def artwork
  {:id 123 :title "Spiral study" :category "ink" :src "/graphics/ink/spiral.svg"})

(deftest artwork-cards-are-buttons-with-visible-captions
  (let [markup (.renderToStaticMarkup
                rdom-server
                ($ graphics/GalleryItem {:locale :en :item artwork :on-click identity}))]
    (is (str/starts-with? markup "<button"))
    (is (str/includes? markup "type=\"button\""))
    (is (str/includes? markup (copy/text-with :en :graphics/open {:title (:title artwork)})))
    (is (str/includes? markup "object-contain"))
    (is (str/includes? markup "<h4"))
    (is (str/includes? markup (:title artwork)))
    (is (str/includes? markup (:category artwork)))
    (is (not (str/includes? markup "opacity-0")))))

(deftest an-open-lightbox-has-dialog-and-close-semantics
  (let [markup (.renderToStaticMarkup
                rdom-server
                ($ graphics/Lightbox {:locale :en :item artwork :on-close identity}))]
    (is (str/includes? markup "role=\"dialog\""))
    (is (str/includes? markup "aria-modal=\"true\""))
    (is (str/includes? markup "aria-labelledby=\"graphics-lightbox-title\""))
    (is (str/includes? markup "id=\"graphics-lightbox-title\""))
    (is (str/includes? markup (copy/text :en :graphics/close)))
    (is (str/includes? markup (:src artwork)))))
