(ns open-hax.website.extern.gallery-test
  (:require [cljs.test :refer [deftest is testing]]
            [open-hax.website.extern.gallery :as gallery]))

(deftest tab-remains-contained-when-the-dialog-itself-has-focus
  (let [previous-document (aget js/globalThis "document")
        listener (atom nil)
        document #js {:body #js {:style #js {:overflow "auto"}}}
        trigger #js {:isConnected true}
        first-button #js {}
        last-button #js {}
        dialog #js {}
        elements (atom [first-button last-button])
        focus! (fn [element] (set! (.-activeElement document) element))
        tab! (fn [backwards?]
               (let [prevented (atom false)]
                 (@listener #js {:key "Tab" :shiftKey backwards?
                                 :preventDefault #(reset! prevented true)})
                 @prevented))]
    (doseq [element [trigger first-button last-button dialog]]
      (aset element "focus" #(focus! element)))
    (aset dialog "querySelectorAll" (fn [_selector] (to-array @elements)))
    (aset dialog "contains"
          (fn [element] (boolean (some #(identical? element %)
                                       [dialog first-button last-button]))))
    (aset document "getElementById" (fn [_id] dialog))
    (aset document "addEventListener" (fn [_kind handler] (reset! listener handler)))
    (aset document "removeEventListener" (fn [_kind _handler] (reset! listener nil)))
    (focus! trigger)
    (aset js/globalThis "document" document)
    (try
      (let [cleanup (gallery/activate-dialog! "gallery-dialog" (fn [] nil))]
        (is (identical? first-button (.-activeElement document)))
        (is (= "hidden" (.. document -body -style -overflow)))
        (testing "Shift+Tab from the focusable container wraps to its last child"
          (.focus dialog)
          (is (tab! true))
          (is (identical? last-button (.-activeElement document))))
        (testing "existing end and start wraps still work"
          (is (tab! false))
          (is (identical? first-button (.-activeElement document)))
          (is (tab! true))
          (is (identical? last-button (.-activeElement document))))
        (testing "forward Tab from the container retains native first-child movement"
          (.focus dialog)
          (is (false? (tab! false))))
        (testing "a dialog without focusable children contains both directions"
          (reset! elements [])
          (is (tab! true))
          (is (identical? dialog (.-activeElement document))))
        (cleanup)
        (is (nil? @listener))
        (is (= "auto" (.. document -body -style -overflow)))
        (is (identical? trigger (.-activeElement document))))
      (finally
        (if (some? previous-document)
          (aset js/globalThis "document" previous-document)
          (js-delete js/globalThis "document"))))))
