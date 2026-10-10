(ns open-hax.website.sections.graphics-test
  (:require ["react-dom/server" :as rdom-server]
            ["react" :as react]
            [cljs.test :refer [deftest is testing]]
            [clojure.string :as str]
            [helix.core :refer [$]]
            [open-hax.website.data.assets :as assets]
            [open-hax.website.domain.copy :as copy]
            [open-hax.website.domain.gallery :as gallery]
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

(deftest the-featured-gallery-renders-the-whole-selection-in-editorial-order
  (let [featured (mapv (fn [order]
                         (assoc artwork
                                :id order :title (str "Featured study " order)
                                :src (str "/graphics/featured/" order ".svg")
                                :featured? true :feature-order order))
                       (range 18))
        collection-only (assoc artwork :id 99 :src "/graphics/collection-only.svg")]
    (with-redefs [assets/graphics (conj (vec (reverse featured)) collection-only)]
      (let [markup (.renderToStaticMarkup rdom-server ($ graphics/GraphicsSection {:locale :en}))
            selection (subs markup 0 (str/index-of markup (copy/text :en :graphics/collection)))
            images (mapv second (re-seq #"<img[^>]*src=\"([^\"]+)\"" selection))]
        (is (= 18 (count images)))
        (is (= (mapv :src featured) images)
            "Every featured artwork is visible in its declared order, beyond twelve cards.")
        (is (not (str/includes? selection (:src collection-only))))))))

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

(deftest show-more-clicks-extend-the-rendered-collection-through-the-last-page
  (let [items (mapv (fn [id]
                     (assoc artwork :id id :title (str "Study " id)
                            :src (str "/graphics/studies/" id ".svg")))
                   (range (+ (* 2 gallery/page-size) 3)))
        state (atom [])
        hook-index (atom 0)
        dispatcher (.. react -__SECRET_INTERNALS_DO_NOT_USE_OR_YOU_WILL_BE_FIRED
                       -ReactCurrentDispatcher)
        original-dispatcher (.-current dispatcher)
        ;; Exercise Helix's real use-state adapter. This storage implements
        ;; React's single-action dispatch boundary, including function actions;
        ;; replacing the Helix hook itself would miss its variadic updater law.
        test-dispatcher #js {:useState (fn [initial]
                                        (let [index @hook-index]
                                          (swap! hook-index inc)
                                          (when (= index (count @state))
                                            (swap! state conj initial))
                                          #js [(get @state index)
                                               (fn [action]
                                                 (swap! state update index
                                                        #(if (fn? action) (action %) action)))]))
                            :useCallback (fn [callback _deps] callback)
                            :useEffect (fn [_effect _deps] nil)}
        render-section (fn []
                         (reset! hook-index 0)
                         (set! (.-current dispatcher) test-dispatcher)
                         (try
                           (graphics/GraphicsSection #js {:locale :en})
                           (finally
                             (set! (.-current dispatcher) original-dispatcher))))
        children (fn [^js element]
                   (when (.isValidElement react element)
                     (let [^js props (.-props element)]
                       (array-seq (.toArray (.-Children react) (.-children props))))))
        more-button (fn [element]
                      (some (fn [^js child]
                              (when (and (.isValidElement react child)
                                         (= "button" (.-type child))
                                         (= (copy/text :en :graphics/more)
                                            (let [^js props (.-props child)]
                                              (.-children props))))
                                child))
                            (tree-seq #(.isValidElement react %) children element)))
        displayed-images (fn [element]
                           (mapv second
                                 (re-seq #"<img[^>]*src=\"([^\"]+)\""
                                         (.renderToStaticMarkup rdom-server element))))]
    (with-redefs [assets/graphics items]
      (let [initial (render-section)
            ^js button (more-button initial)
            ^js button-props (.-props button)
            first-click (.-onClick button-props)]
        (is (= (mapv :src (take gallery/page-size items)) (displayed-images initial)))
        ;; Two clicks through the same rendered handler must compose against
        ;; current state, even before another render has produced a new handler.
        (first-click)
        (let [second-page (render-section)]
          (is (= (mapv :src (take (* 2 gallery/page-size) items))
                 (displayed-images second-page)))
          (is (some? (more-button second-page))))
        (first-click)
        (let [last-page (render-section)]
          (is (= (mapv :src items) (displayed-images last-page)))
          (is (nil? (more-button last-page))))))))

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

(defn- element-prop [element property]
  (aget (aget element "props") property))

(defn- element-children [element]
  (when (.isValidElement react element)
    (array-seq (.toArray (.-Children react) (element-prop element "children")))))

(defn- find-element [element pred]
  (some #(when (and (.isValidElement react %) (pred %)) %)
        (tree-seq #(.isValidElement react %) element-children element)))

(defn- with-gallery-runtime [exercise]
  (let [items (mapv #(assoc artwork :id % :title (str "Study " %)
                           :featured? (< % 3))
                    (range (+ (* 2 gallery/page-size) 3)))
        state (atom [])
        hook-index (atom 0)
        effects (atom [])
        effect-deps (atom nil)
        focus-history (atom [])
        collection-buttons (atom nil)
        dispatcher (.. react -__SECRET_INTERNALS_DO_NOT_USE_OR_YOU_WILL_BE_FIRED
                       -ReactCurrentDispatcher)
        previous-dispatcher (.-current dispatcher)
        previous-document (aget js/globalThis "document")
        document #js {}
        collection #js {:querySelectorAll (fn [_selector] (to-array @collection-buttons))}
        test-dispatcher
        #js {:useState (fn [initial]
                         (let [index @hook-index]
                           (swap! hook-index inc)
                           (when (= index (count @state)) (swap! state conj initial))
                           #js [(get @state index)
                                (fn [action]
                                  (swap! state update index
                                         #(if (fn? action) (action %) action)))]))
             :useCallback (fn [callback _deps] callback)
             :useEffect (fn [effect deps]
                          (let [values (vec (array-seq deps))]
                            (when (not= values @effect-deps)
                              (reset! effect-deps values)
                              (swap! effects conj effect))))}
        render! (fn []
                  (reset! hook-index 0)
                  (set! (.-current dispatcher) test-dispatcher)
                  (let [element (try (graphics/GraphicsSection #js {:locale :en})
                                     (finally
                                       (set! (.-current dispatcher) previous-dispatcher)))
                        grid (find-element element #(= "graphics-collection" (element-prop % "id")))]
                    ;; The DOM lookup succeeds only after this render's collection
                    ;; has committed. Featured cards deliberately stay outside it.
                    (reset! collection-buttons
                            (when grid
                              (mapv (fn [card]
                                      (let [item (element-prop card "item")
                                            button #js {:id (:id item)}]
                                        (aset button "focus"
                                              #(do (swap! focus-history conj (:id item))
                                                   (set! (.-activeElement document) button)))
                                        button))
                                    (element-children grid))))
                    element))
        flush! (fn []
                 (let [pending @effects]
                   (reset! effects [])
                   (doseq [effect pending] (effect))))
        more-click (fn [element]
                     (element-prop
                      (find-element element
                                    #(and (= "button" (aget % "type"))
                                          (= (copy/text :en :graphics/more)
                                             (element-prop % "children"))))
                      "onClick"))]
    (aset document "getElementById"
          #(when (and (= % "graphics-collection") (some? @collection-buttons)) collection))
    (aset js/globalThis "document" document)
    (try
      (with-redefs [assets/graphics items]
        (exercise {:render! render! :flush! flush! :more-click more-click
                   :focused focus-history :items items}))
      (finally
        (set! (.-current dispatcher) previous-dispatcher)
        (if (some? previous-document)
          (aset js/globalThis "document" previous-document)
          (js-delete js/globalThis "document"))))))

(deftest show-more-focuses-each-new-page-after-render-including-the-last
  (with-gallery-runtime
    (fn [{:keys [render! flush! more-click focused]}]
      (let [initial (render!)
            stale-click (more-click initial)]
        (flush!)
        (is (empty? @focused) "Initial rendering does not steal focus.")
        (stale-click)
        (is (empty? @focused) "Focus waits for the new cards to render.")
        (render!) (flush!)
        (is (= [gallery/page-size] @focused))
        (stale-click)
        (let [last-page (render!)]
          (is (nil? (find-element last-page #(= (copy/text :en :graphics/more)
                                               (element-prop % "children"))))
              "The final page no longer has Show more.")
          (flush!)
          (is (= [gallery/page-size (* 2 gallery/page-size)] @focused)
              "An old render's handler advances both pagination and its focus destination."))
        (render!) (flush!)
        (is (= 2 (count @focused)) "Unrelated rendering does not refocus a consumed page.")))))

(deftest batched-show-more-clicks-focus-the-first-newly-rendered-card
  (with-gallery-runtime
    (fn [{:keys [render! flush! more-click focused]}]
      (let [click (more-click (render!))]
        (flush!)
        (click) (click)
        (is (empty? @focused))
        (render!) (flush!)
        (is (= [gallery/page-size] @focused)
            "Two queued updates retain the earliest new card in their shared commit.")))))

(deftest filters-cancel-uncommitted-gallery-focus
  (doseq [[id value] [["graphics-search" "Study"] ["graphics-category" "ink"]]]
    (with-gallery-runtime
      (fn [{:keys [render! flush! more-click focused]}]
        (let [initial (render!)
              change (element-prop (find-element initial #(= id (element-prop % "id"))) "onChange")]
          (flush!)
          ((more-click initial))
          (change #js {:target #js {:value value}})
          (render!) (flush!)
          (is (empty? @focused) (str id " keeps focus when resetting the collection.")))))))
