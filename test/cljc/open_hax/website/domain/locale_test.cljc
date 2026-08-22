(ns open-hax.website.domain.locale-test
  (:require [clojure.test :refer [deftest is testing]]
            [open-hax.website.domain.locale :as locale]))

(deftest default-locale-serves-the-root
  (is (= :en locale/default-locale))
  (is (= "/" (locale/locale-root :en)))
  (is (= "" (locale/locale-prefix :en))))

(deftest every-other-locale-takes-a-prefix
  (is (= ["/es/" "/fr/" "/de/" "/ja/"]
         (mapv locale/locale-root [:es :fr :de :ja])))
  (is (= ["/es" "/fr" "/de" "/ja"]
         (mapv locale/locale-prefix [:es :fr :de :ja]))))

(deftest the-root-resolves-to-the-default-locale
  (is (= {:locale/id :en :locale/fallback? false :location/path "/"}
         (locale/resolve-path "/")))
  (is (= {:locale/id :en :locale/fallback? false :location/path "/"}
         (locale/resolve-path ""))))

(deftest a-known-prefix-resolves-to-its-locale
  (is (= {:locale/id :es :locale/fallback? false :location/path "/"}
         (locale/resolve-path "/es/")))
  (is (= {:locale/id :ja :locale/fallback? false :location/path "/notes/hello"}
         (locale/resolve-path "/ja/notes/hello"))))

(deftest an-unknown-locale-prefix-resolves-to-the-default-never-a-404
  (testing "a locale-shaped prefix this build does not carry falls back"
    (is (= {:locale/id :en :locale/fallback? true :location/path "/"}
           (locale/resolve-path "/pt/")))
    (is (= {:locale/id :en :locale/fallback? true :location/path "/notes/hello"}
           (locale/resolve-path "/pt/notes/hello")))
    (is (= {:locale/id :en :locale/fallback? true :location/path "/"}
           (locale/resolve-path "/zh-hans/")))))

(deftest a-document-path-is-not-mistaken-for-a-locale
  (testing "only locale-SHAPED first segments are treated as locale prefixes"
    (is (= {:locale/id :en :locale/fallback? false :location/path "/notes/hello"}
           (locale/resolve-path "/notes/hello")))))

(deftest an-explicit-default-prefix-is-accepted
  (is (= {:locale/id :en :locale/fallback? false :location/path "/notes/hello"}
         (locale/resolve-path "/en/notes/hello"))))

(deftest localize-path-inverts-resolve-path
  (doseq [loc locale/supported-locales
          path ["/" "/notes/hello"]]
    (let [public-path (locale/localize-path loc path)
          resolved (locale/resolve-path public-path)]
      (is (= loc (:locale/id resolved)) public-path)
      (is (= path (:location/path resolved)) public-path))))

(deftest language-tags-cover-every-supported-locale
  (is (= (set locale/supported-locales) (set (keys locale/language-tags))))
  (is (= (set locale/supported-locales) (set (keys locale/endonyms))))
  (testing "endonyms name each language in itself"
    (is (= "日本語" (locale/endonym :ja)))
    (is (= "Deutsch" (locale/endonym :de)))))
