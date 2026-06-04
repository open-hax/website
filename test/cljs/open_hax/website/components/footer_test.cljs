(ns open-hax.website.components.footer-test
  (:require [cljs.test :refer [deftest is testing]]
            [open-hax.website.components.footer :as footer]))

(deftest footer-exists
  (testing "Footer is a function/component"
    (is (fn? footer/Footer))))
