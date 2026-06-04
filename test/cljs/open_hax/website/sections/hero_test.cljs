(ns open-hax.website.sections.hero-test
  (:require [cljs.test :refer [deftest is testing]]
            [open-hax.website.sections.hero :as hero]))

(deftest hero-section-exists
  (testing "HeroSection is a function/component"
    (is (fn? hero/HeroSection))))
