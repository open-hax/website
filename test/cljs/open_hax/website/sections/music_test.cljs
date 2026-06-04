(ns open-hax.website.sections.music-test
  (:require [cljs.test :refer [deftest is testing]]
            [open-hax.website.sections.music :as music]
            [open-hax.website.data.assets :as assets]))

(deftest music-section-exists
  (testing "MusicSection is a function/component"
    (is (fn? music/MusicSection))))

(deftest assets-music-count
  (testing "assets has music loaded"
    (is (> (count assets/music) 0))))

(deftest track-row-exists
  (testing "TrackRow is a function/component"
    (is (fn? music/TrackRow))))
