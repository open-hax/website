(ns open-hax.website.components.navigation-test
  (:require [cljs.test :refer [deftest is testing]]
            [open-hax.website.components.navigation :as nav]))

(deftest navigation-exists
  (testing "Navigation is a function/component"
    (is (fn? nav/Navigation))))
