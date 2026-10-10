(ns open-hax.website.domain.navigation-test
  (:require [clojure.test :refer [deftest is testing]]
            [open-hax.website.domain.locale :as locale]
            [open-hax.website.domain.navigation :as navigation]))

(deftest home-links-resolve-to-the-selected-locale
  (is (= "/" (:home (navigation/destinations :en))))
  (is (= "/es/" (:home (navigation/destinations :es))))
  (is (= "/ja/" (:home (navigation/destinations :ja)))))

(deftest the-personal-portfolio-destinations-stay-shared-across-locales
  (testing "locale selection changes only the home destination"
    (doseq [loc locale/supported-locales]
      (is (= navigation/site-destinations
             (dissoc (navigation/destinations loc) :home)))))
  (is (= "#graphics" (:graphics navigation/site-destinations)))
  (is (= "https://github.com/riatzukiza" (:personal-github navigation/site-destinations)))
  (is (= "https://github.com/open-hax" (:organization-github navigation/site-destinations))))
