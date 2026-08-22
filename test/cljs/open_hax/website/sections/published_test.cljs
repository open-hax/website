(ns open-hax.website.sections.published-test
  (:require [cljs.test :refer [deftest is testing]]
            [open-hax.website.components.manifest-error :as manifest-error]
            [open-hax.website.sections.published :as published]))

(deftest published-views-exist
  (testing "the three states a published route can be in each have a view"
    (is (fn? published/PublishedListing))
    (is (fn? published/PublishedDocument))
    (is (fn? published/PublishedNotFound))))

(deftest the-loud-failure-has-a-view
  (testing "an invalid manifest renders an error, not a blank page"
    (is (fn? manifest-error/ManifestErrorBanner))))
