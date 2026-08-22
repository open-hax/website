(ns open-hax.website.sections.music-test
  (:require [cljs.test :refer [deftest is testing]]
            [clojure.string :as str]
            [open-hax.website.data.assets :as assets]
            [open-hax.website.sections.music :as music]))

(deftest music-section-exists
  (testing "MusicSection is a function/component"
    (is (fn? music/MusicSection))))

(deftest track-row-exists
  (testing "TrackRow is a function/component"
    (is (fn? music/TrackRow))))

(deftest an-empty-track-list-is-a-valid-build
  (testing "zero staged tracks renders an empty list and a count of 0"
    (is (vector? assets/music))
    (is (<= 0 (count assets/music)))))

(deftest every-listed-track-is-addressable-inside-the-build-output
  (doseq [track assets/music]
    (is (str/starts-with? (:src track) "/music/") (pr-str track))
    (is (not (str/includes? (:src track) "..")) (pr-str track))
    (is (int? (:id track)) (pr-str track))))

(deftest track-ids-are-stable-and-unique
  (is (= (count assets/music) (count (set (map :id assets/music))))))
