(ns open-hax.website.infra.published-manifest-test
  "The committed fixture FILES, and the fetch/parse edge that reads them.

  The pure contract tests live in
  `open-hax.website.law.published-manifest-test`. This namespace covers the two
  things those cannot: that the readable fixture table and the EDN bytes on
  disk still agree, and that a real `fetch` result maps onto the right outcome."
  (:require ["fs" :as fs]
            ["path" :as path]
            [cljs.reader :as reader]
            [cljs.test :refer [deftest is testing]]
            [clojure.string :as str]
            [open-hax.website.infra.published-manifest :as infra]
            [open-hax.website.law.published-manifest :as law]
            [open-hax.website.law.published-manifest-fixtures :as fixtures]))

(defn- fixture-path
  [file]
  (path/join (.cwd js/process) fixtures/fixture-dir file))

(defn- slurp-fixture
  [file]
  (let [p (fixture-path file)]
    (is (fs/existsSync p) (str "missing fixture file: " p))
    (fs/readFileSync p "utf8")))

(deftest every-fixture-file-exists-and-matches-the-readable-table
  (testing "the EDN bytes and the fixture table cannot drift apart"
    (doseq [{:keys [fixture/id fixture/file fixture/edn]} fixtures/fixtures
            :when file]
      (let [parsed (reader/read-string (slurp-fixture file))]
        (is (= edn parsed) (str id " (" file ")"))))))

(deftest every-fixture-file-names-its-writer
  (testing "provenance is a comment in the fixture, so the next person can find the writer"
    (doseq [{:keys [fixture/id fixture/file]} fixtures/fixtures
            :when file]
      (let [text (slurp-fixture file)]
        (is (str/includes? text "knoxx") (str id " names the writer repository"))
        (is (str/includes? text "publication-target-static-site")
            (str id " names the writer namespace"))
        (is (str/includes? text "EXPECTED:") (str id " states its expected outcome"))))))

(deftest fixture-files-decode-to-their-stated-outcome-from-real-bytes
  (testing "parsed from disk, not from a literal in a test"
    (doseq [{:keys [fixture/id fixture/file fixture/expect]} fixtures/fixtures
            :when file]
      (let [decoded (law/decode (reader/read-string (slurp-fixture file)))]
        (is (= (:status expect) (:published/status decoded)) (str id))
        (is (= (:route-count expect) (count (:published/routes decoded))) (str id))))))

(deftest the-fixture-directory-holds-no-orphans
  (testing "a fixture nobody reads is a second implementation nobody maintains"
    (let [on-disk (set (fs/readdirSync (path/join (.cwd js/process) fixtures/fixture-dir)))
          declared (into #{} (keep :fixture/file) fixtures/fixtures)]
      (is (= declared on-disk)))))

(deftest an-absent-file-is-not-an-error
  (let [decoded (infra/decode-fetch {:fetch/status :absent})]
    (is (law/valid? decoded))
    (is (= :absent (:published/source decoded)))
    (is (= [] (:published/routes decoded)))))

(deftest a-fetched-manifest-decodes
  (let [decoded (infra/decode-fetch {:fetch/status :ok
                                     :fetch/body (slurp-fixture "multi-locale.edn")})]
    (is (law/valid? decoded))
    (is (= 3 (count (:published/routes decoded))))))

(deftest bytes-that-are-not-edn-fail-loudly
  (testing "truncated EDN, an empty file, and the HTML a static host serves instead of a 404"
    (doseq [body ["{:manifest/version 1"
                  ""
                  "   "
                  "<!doctype html><html></html>"
                  "<html lang=\"en\"></html>"]]
      (let [decoded (infra/decode-fetch {:fetch/status :ok :fetch/body body})]
        (is (not (law/valid? decoded)) (pr-str body))
        (is (= [:manifest.error/unparseable]
               (mapv :error/kind (:published/errors decoded)))
            (pr-str body))))))

(deftest readable-edn-that-is-not-a-manifest-fails-loudly
  (testing "parsing is not the same as being a manifest"
    (doseq [body ["[1 2 3]" "\"nope\"" "42"]]
      (let [decoded (infra/decode-fetch {:fetch/status :ok :fetch/body body})]
        (is (not (law/valid? decoded)) (pr-str body))
        (is (= [:manifest.error/not-a-map]
               (mapv :error/kind (:published/errors decoded)))
            (pr-str body))))))

(deftest an-unreadable-manifest-fails-loudly-rather-than-looking-empty
  (testing "'nothing is published' and 'we cannot tell' are different facts"
    (doseq [result [{:fetch/status :error :fetch/message "HTTP 500"}
                    {:fetch/status :refused :fetch/message "non-relative"}]]
      (let [decoded (infra/decode-fetch result)]
        (is (not (law/valid? decoded)))
        (is (= [:manifest.error/unreadable]
               (mapv :error/kind (:published/errors decoded))))))))
