(ns open-hax.website.domain.html-document-test
  "The static shells, asserted without running the build."
  (:require [clojure.string :as str]
            [clojure.test :refer [deftest is testing]]
            [open-hax.website.domain.copy :as copy]
            [open-hax.website.domain.html-document :as html]
            [open-hax.website.domain.locale :as locale]))

(deftest one-page-per-locale-the-default-at-the-root
  (let [pages (html/pages)]
    (is (= (count locale/supported-locales) (count pages)))
    (is (= ["index.html" "es/index.html" "fr/index.html" "de/index.html" "ja/index.html"]
           (mapv :page/output-path pages)))))

(deftest every-page-declares-its-own-lang
  (doseq [{:keys [page/locale page/language-tag page/html]} (html/pages)]
    (is (str/includes? html (str "<html lang=\"" language-tag "\""))
        (str locale " lang"))
    (testing "and no page claims to be English unless it is"
      (when-not (= :en locale)
        (is (not (str/includes? html "<html lang=\"en\"")) (str locale))))))

(deftest every-page-carries-its-localized-title-and-description
  (doseq [loc locale/supported-locales]
    (let [markup (html/render loc)]
      (is (str/includes? markup (str "<title>" (html/escape-html (copy/text loc :meta/title)) "</title>"))
          (str loc " title"))
      (is (str/includes? markup (html/escape-html (copy/text loc :meta/description)))
          (str loc " description"))
      (is (str/includes? markup (html/escape-html (copy/text loc :meta/noscript)))
          (str loc " noscript")))))

(deftest titles-are-actually-translated
  (testing "not five copies of the English one"
    (let [titles (mapv #(copy/text % :meta/title) locale/supported-locales)]
      (is (= (count titles) (count (set titles)))))))

(deftest every-page-lists-every-alternate-and-an-x-default
  (doseq [loc locale/supported-locales]
    (let [markup (html/render loc)]
      (doseq [other locale/supported-locales]
        (is (str/includes? markup
                           (str "<link rel=\"alternate\" hreflang=\"" (locale/language-tag other)
                                "\" href=\"" (locale/locale-root other) "\" />"))
            (str loc " -> " other)))
      (is (str/includes? markup "hreflang=\"x-default\" href=\"/\"") (str loc " x-default"))
      (is (str/includes? markup (str "<link rel=\"canonical\" href=\"" (locale/locale-root loc) "\" />"))
          (str loc " canonical")))))

(deftest every-page-references-only-root-absolute-build-assets
  (testing "so /es/ resolves the same bundle as /, from one docroot"
    (doseq [{:keys [page/html]} (html/pages)]
      (is (str/includes? html "href=\"/app.css\""))
      (is (str/includes? html "src=\"/cljs/app.js\""))
      (testing "and nothing points outside the build output"
        (is (not (str/includes? html "http://")))
        (is (not (str/includes? html "https://")))
        (is (not (str/includes? html "/home/")))
        (is (not (str/includes? html "../")))))))

(deftest markup-is-escaped
  (is (= "&amp;&lt;&gt;&quot;" (html/escape-html "&<>\"")))
  (testing "the ampersand in the English title survives as an entity"
    (is (str/includes? (html/render :en) "Art, Music &amp; Programs"))
    (is (not (str/includes? (html/render :en) "Music & Programs")))))
