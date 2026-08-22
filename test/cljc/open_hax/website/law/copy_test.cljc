(ns open-hax.website.law.copy-test
  "The completeness law for the locale dictionaries.

  This is the test that makes translation maintainable. Every other kind of
  copy defect announces itself when someone looks at the page; a missing key
  does not, because the fallback renders English and English looks fine to
  whoever is reading the code."
  (:require [clojure.string :as str]
            [clojure.test :refer [deftest is testing]]
            [open-hax.website.domain.copy :as copy]
            [open-hax.website.domain.locale :as locale]
            [open-hax.website.law.copy :as law]
            [open-hax.website.shape.template :as template]))

(deftest every-supported-locale-has-a-dictionary
  (testing "a locale the site offers must have copy compiled in"
    (is (= (set locale/supported-locales)
           (set (keys copy/dictionaries))))))

(deftest dictionaries-obey-every-law
  (testing "identical key sets, identical placeholders, verbatim proper nouns, no blanks"
    (let [result (law/verify copy/dictionaries copy/reference-locale)]
      (is (:law/ok? result)
          (str "copy law violations:\n"
               (str/join "\n" (map pr-str (:law/problems result))))))))

(deftest key-sets-are-exactly-equal-per-locale
  (testing "reported per locale, so a failure names the file to edit"
    (let [reference (get copy/dictionaries copy/reference-locale)]
      (doseq [[loc dictionary] (sort-by (comp name key) copy/dictionaries)]
        (let [{:keys [missing extra]} (law/key-diff dictionary reference)]
          (is (empty? missing) (str loc " is missing keys: " (pr-str missing)))
          (is (empty? extra) (str loc " defines keys en does not: " (pr-str extra))))))))

(deftest a-missing-key-fails-the-law
  (testing "the law detects the defect it exists for"
    (let [reference (get copy/dictionaries :en)
          broken (dissoc (get copy/dictionaries :es) :hero/lede)
          result (law/verify {:en reference :es broken} :en)]
      (is (not (:law/ok? result)))
      (is (= [:copy.problem/missing-keys]
             (mapv :problem/kind (:law/problems result))))
      (is (= #{:hero/lede}
             (set (:problem/keys (first (:law/problems result)))))))))

(deftest an-extra-key-fails-the-law
  (testing "dead copy is a defect, not a harmless addition"
    (let [reference (get copy/dictionaries :en)
          broken (assoc (get copy/dictionaries :fr) :hero/subtitle "Sous-titre")
          result (law/verify {:en reference :fr broken} :en)]
      (is (not (:law/ok? result)))
      (is (= [:copy.problem/extra-keys]
             (mapv :problem/kind (:law/problems result)))))))

(deftest a-dropped-placeholder-fails-the-law
  (testing "a translation that loses {count} reads fine and states nothing"
    (let [reference (get copy/dictionaries :en)
          broken (assoc (get copy/dictionaries :de) :music/lede
                        "Klangliche Experimente und Kompositionen.")
          result (law/verify {:en reference :de broken} :en)]
      (is (not (:law/ok? result)))
      (is (= [:copy.problem/placeholder-mismatch]
             (mapv :problem/kind (:law/problems result))))
      (is (= #{:count} (:problem/expected (first (:law/problems result)))))
      (is (= #{} (:problem/actual (first (:law/problems result))))))))

(deftest a-translated-proper-noun-fails-the-law
  (testing "OpenHax is OpenHax in every locale"
    (let [reference (get copy/dictionaries :en)
          broken (assoc (get copy/dictionaries :ja) :brand/name "オープンハックス")
          result (law/verify {:en reference :ja broken} :en)]
      (is (not (:law/ok? result)))
      (is (= [:copy.problem/verbatim-differs]
             (mapv :problem/kind (:law/problems result)))))))

(deftest a-blank-translation-fails-the-law
  (testing "the one translation that passes completeness while saying nothing"
    (let [reference (get copy/dictionaries :en)
          broken (assoc (get copy/dictionaries :es) :hero/cta-work "   ")
          result (law/verify {:en reference :es broken} :en)]
      (is (not (:law/ok? result)))
      (is (= [:copy.problem/blank-value]
             (mapv :problem/kind (:law/problems result)))))))

(deftest interpolated-messages-are-templates-not-concatenations
  (testing "the three interpolated sentences carry their placeholder in every locale"
    (doseq [[k expected] {:graphics/lede #{:count}
                          :music/lede #{:count}
                          :footer/copyright #{:year}}
            loc locale/supported-locales]
      (is (= expected (template/placeholders (copy/text loc k)))
          (str loc " " k)))))

(deftest word-order-differs-and-that-is-the-point
  (testing "Japanese puts its counter after the numeral; English does not"
    (let [en (copy/text-with :en :music/lede {:count 12})
          ja (copy/text-with :ja :music/lede {:count 12})
          de (copy/text-with :de :graphics/lede {:count 7})]
      (is (str/starts-with? en "12 sonic"))
      (is (str/includes? ja "12 点の"))
      (is (str/starts-with? de "7 visuelle"))
      (is (not (str/includes? en "{count}")))
      (is (not (str/includes? ja "{count}")))
      (is (not (str/includes? de "{count}"))))))
