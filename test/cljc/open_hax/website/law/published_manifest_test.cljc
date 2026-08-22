(ns open-hax.website.law.published-manifest-test
  "The manifest contract, asserted from the reader's side.

  Every rule in the contract's reader table has a test here, including the ones
  that are supposed to FAIL. The happy path was never the risk: a writer and a
  reader that only run together against live infrastructure drift unseen, and
  the drift shows up first in the failure modes, because those are the ones
  nobody exercises by hand."
  (:require [clojure.test :refer [deftest is testing]]
            [open-hax.website.domain.published :as published]
            [open-hax.website.law.published-manifest :as law]
            [open-hax.website.law.published-manifest-fixtures :as fixtures]))

(defn- decode-fixture
  [id]
  (law/decode (fixtures/fixture-edn id)))

(deftest every-fixture-produces-its-stated-outcome
  (testing "the fixture table's :fixture/expect is the assertion"
    (doseq [{:keys [fixture/id fixture/edn fixture/expect]} fixtures/fixtures]
      (let [decoded (law/decode edn)]
        (is (= (:status expect) (:published/status decoded))
            (str id " status"))
        (is (= (:source expect) (:published/source decoded))
            (str id " source"))
        (is (= (:route-count expect) (count (:published/routes decoded)))
            (str id " route count"))
        (is (= (:published-locales expect) (published/published-locales decoded))
            (str id " published locales"))
        (when-let [kinds (:error-kinds expect)]
          (is (= kinds (mapv :error/kind (:published/errors decoded)))
              (str id " error kinds")))
        (when (= :ok (:status expect))
          (is (empty? (:published/errors decoded))
              (str id " has no errors")))))))

(deftest an-absent-manifest-is-not-an-error
  (testing "the normal state of every deploy before the first publication"
    (let [decoded (law/decode law/absent)]
      (is (law/valid? decoded))
      (is (= :absent (:published/source decoded)))
      (is (= [] (:published/routes decoded))))
    (testing "and a nil parse is treated the same way"
      (is (law/valid? (law/decode nil))))))

(deftest an-empty-manifest-behaves-exactly-like-an-absent-one
  (let [absent (law/decode law/absent)
        empty-manifest (decode-fixture :empty)]
    (is (= (:published/status absent) (:published/status empty-manifest)))
    (is (= (:published/routes absent) (:published/routes empty-manifest)))
    (is (= [] (published/published-locales empty-manifest)))))

(deftest an-unknown-field-is-ignored-and-dropped
  (let [decoded (decode-fixture :unknown-field)
        route (first (:published/routes decoded))]
    (is (law/valid? decoded))
    (testing "the route still decodes"
      (is (= "/es/notes/hello" (:route/path route)))
      (is (= :es (:route/locale route))))
    (testing "and the undeclared fields are not carried forward"
      (is (not (contains? route :route/experiment)))
      (is (not (contains? route :route/word-count)))
      (is (= law/known-route-keys
             (into law/known-route-keys (keys route)))))))

(deftest a-missing-required-field-fails-loudly
  (testing "never a silently blank page"
    (doseq [k law/required-route-keys]
      (let [route (dissoc {:route/path "/es/x"
                           :route/locale :es
                           :route/artifact "artifacts/x.html"
                           :route/media-type "text/html"}
                          k)
            decoded (law/decode {:manifest/version 1 :manifest/routes [route]})]
        (is (not (law/valid? decoded)) (str "missing " k))
        (is (= [:manifest.error/missing-required-field]
               (mapv :error/kind (:published/errors decoded)))
            (str "missing " k))
        (is (= [k] (mapv :error/key (:published/errors decoded)))
            (str "missing " k))
        (is (= [] (:published/routes decoded))
            "an invalid manifest publishes nothing")))))

(deftest a-missing-version-fails-loudly
  (let [decoded (law/decode {:manifest/routes []})]
    (is (not (law/valid? decoded)))
    (is (= [:manifest.error/missing-version]
           (mapv :error/kind (:published/errors decoded))))))

(deftest an-unsupported-version-fails-loudly
  (let [decoded (decode-fixture :bad-version)]
    (is (not (law/valid? decoded)))
    (is (= [:manifest.error/unsupported-version]
           (mapv :error/kind (:published/errors decoded))))
    (testing "and says what it does support"
      (is (= law/supported-versions
             (:error/supported (first (:published/errors decoded))))))))

(deftest a-selector-revision-is-rejected
  (let [decoded (decode-fixture :selector-revision)]
    (is (not (law/valid? decoded)))
    (is (= [:manifest.error/selector-revision]
           (mapv :error/kind (:published/errors decoded))))
    (is (= :source/current
           (:error/value (first (:published/errors decoded)))))))

(deftest a-concrete-revision-is-accepted-and-an-absent-one-is-fine
  (testing ":route/revision is optional; it is only its shape that is constrained"
    (is (law/valid? (law/decode {:manifest/version 1
                                 :manifest/routes
                                 [{:route/path "/es/x"
                                   :route/locale :es
                                   :route/artifact "artifacts/x.html"
                                   :route/media-type "text/html"}]})))
    (is (law/valid? (law/decode {:manifest/version 1
                                 :manifest/routes
                                 [{:route/path "/es/x"
                                   :route/locale :es
                                   :route/revision "rev-7f3a91c"
                                   :route/artifact "artifacts/x.html"
                                   :route/media-type "text/html"}]})))))

(deftest a-cross-origin-artifact-is-rejected
  (testing "no request from the site may reach a Knoxx origin; the seam is files"
    (let [decoded (decode-fixture :cross-origin-artifact)]
      (is (not (law/valid? decoded)))
      (is (= [:manifest.error/non-relative-artifact]
             (mapv :error/kind (:published/errors decoded)))))))

(deftest relative-path-is-the-mechanism-not-the-convention
  (is (law/relative-path? "artifacts/notes/hello/es/rev-7f3a91c.html"))
  (is (law/relative-path? "/published/manifest.edn"))
  (testing "anything that could leave this origin is not a relative path"
    (is (not (law/relative-path? "https://knoxx.internal/x.html")))
    (is (not (law/relative-path? "http://example.com/x.html")))
    (is (not (law/relative-path? "//knoxx.internal/x.html")))
    (is (not (law/relative-path? "data:text/html,<b>x</b>")))
    (is (not (law/relative-path? "../../etc/passwd")))
    (is (not (law/relative-path? "artifacts/../../secret")))
    (is (not (law/relative-path? "")))
    (is (not (law/relative-path? nil)))))

(deftest a-manifest-that-is-not-a-map-fails-loudly
  (doseq [value [[] "nope" 42]]
    (let [decoded (law/decode value)]
      (is (not (law/valid? decoded)) (pr-str value))
      (is (= [:manifest.error/not-a-map]
             (mapv :error/kind (:published/errors decoded)))))))

(deftest every-error-is-reported-not-just-the-first
  (testing "a writer fixing one field wants the whole list"
    (let [decoded (law/decode {:manifest/version 1
                               :manifest/routes
                               [{:route/locale :es
                                 :route/artifact "artifacts/a.html"
                                 :route/media-type "text/html"}
                                {:route/path "/es/b"
                                 :route/locale :es
                                 :route/revision :source/current
                                 :route/artifact "https://knoxx.internal/b.html"
                                 :route/media-type "text/html"}]})]
      (is (not (law/valid? decoded)))
      (is (= [:manifest.error/missing-required-field
              :manifest.error/non-relative-artifact
              :manifest.error/selector-revision]
             (mapv :error/kind (:published/errors decoded))))
      (testing "each error names the route it came from"
        (is (= [0 1 1] (mapv :error/route-index (:published/errors decoded))))))))

(deftest malformed-required-fields-fail-loudly-too
  (testing "a present-but-wrong field is a writer defect like a missing one"
    (is (= [:manifest.error/malformed-path]
           (mapv :error/kind
                 (:published/errors
                  (law/decode {:manifest/version 1
                               :manifest/routes
                               [{:route/path "es/notes/hello"
                                 :route/locale :es
                                 :route/artifact "artifacts/x.html"
                                 :route/media-type "text/html"}]})))))
    (is (= [:manifest.error/malformed-locale]
           (mapv :error/kind
                 (:published/errors
                  (law/decode {:manifest/version 1
                               :manifest/routes
                               [{:route/path "/es/notes/hello"
                                 :route/locale "es"
                                 :route/artifact "artifacts/x.html"
                                 :route/media-type "text/html"}]})))))
    (is (= [:manifest.error/route-not-a-map]
           (mapv :error/kind
                 (:published/errors
                  (law/decode {:manifest/version 1
                               :manifest/routes ["not a route"]})))))
    (is (= [:manifest.error/routes-not-sequential]
           (mapv :error/kind
                 (:published/errors
                  (law/decode {:manifest/version 1
                               :manifest/routes {:route/path "/x"}})))))))

(deftest a-manifest-with-no-routes-key-is-valid
  (testing "routes are optional; a manifest may declare a version and nothing else"
    (let [decoded (law/decode {:manifest/version 1})]
      (is (law/valid? decoded))
      (is (= [] (:published/routes decoded))))))
