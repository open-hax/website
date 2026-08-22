(ns open-hax.website.law.view-literals-test
  "`:website/copy-resolves-from-locale-dictionaries`, checked rather than trusted.

  The invariant Foresight declares against this repo's AGENTS.md is that view
  namespaces embed no translatable literals. That is exactly the kind of rule
  that holds on the day it is written and erodes on every hurried afternoon
  afterwards, so it is asserted here by reading the view sources.

  Two checks, both narrow enough not to cry wolf:

  1. No message VALUE from the source dictionary appears as a literal in a view.
     Re-hardcoding `Explore Our Work` next to the key that already holds it is
     the specific regression this catches.
  2. No view holds a sentence — a run of words ending in sentence punctuation.
     Class lists, hrefs, DOM ids and icon glyphs are all fine; prose is not.

  Docstrings are excluded before scanning: a namespace docstring is allowed to
  say the word Graphics."
  (:require ["fs" :as fs]
            ["path" :as path]
            [cljs.test :refer [deftest is testing]]
            [clojure.string :as str]
            [open-hax.website.data.copy.en :as en]))

(def view-dirs
  ["src/cljs/open_hax/website/sections"
   "src/cljs/open_hax/website/components"])

(defn- view-files
  []
  (vec
   (for [dir view-dirs
         file (sort (fs/readdirSync (path/join (.cwd js/process) dir)))
         :when (str/ends-with? file ".cljs")]
     {:file/path (str dir "/" file)
      :file/source (fs/readFileSync (path/join (.cwd js/process) dir file) "utf8")})))

(def ^:private docstring-pattern
  ;; A string in the docstring position of ns/def/defn/defnc.
  #"\((?:ns|def|defn|defn-|defnc|defnc-)\s+\^?\{?[^\s()\[\]{}]+\}?\s+\"(?:[^\"\\]|\\.)*\"")

(def ^:private string-literal-pattern
  #"\"(?:[^\"\\]|\\.)*\"")

(def ^:private sentence-pattern
  ;; Two or more words, then sentence punctuation: prose, not a class list.
  #"[A-Za-z]{2,}\s+[A-Za-z]{2,}[.!?]")

(defn- code-without-docstrings
  [source]
  (str/replace source docstring-pattern ""))

(defn- literals
  [source]
  (mapv #(subs % 1 (dec (count %)))
        (re-seq string-literal-pattern (code-without-docstrings source))))

(deftest views-exist-to-be-checked
  (testing "a check that scans nothing passes for the wrong reason"
    (let [files (view-files)]
      (is (<= 7 (count files)))
      (is (every? #(str/includes? (:file/source %) "open-hax.website.domain.copy")
                  (filterv #(not (str/includes? (:file/path %) "language_switcher"))
                           files))
          "every view resolves its copy from the dictionaries"))))

(defn- embeds?
  "True when `literal` is, or contains, the message value `v`.

  Equality catches the exact regression — someone typing `Explore Our Work`
  back into the view beside the key that already holds it. Containment is only
  applied to long values, because a short one like `Music` is a substring of
  `MusicSection` and of half the class names on the page."
  [literal v]
  (or (= literal v)
      (and (< 12 (count v)) (str/includes? literal v))))

(defn- embedded-messages
  [source]
  (vec (for [literal (literals source)
             [k v] en/messages
             :when (and (string? v) (< 3 (count v)) (embeds? literal v))]
         {:message/key k :literal literal})))

(deftest no-view-holds-a-dictionary-value
  (testing "a translatable string lives in exactly one place"
    (doseq [{:keys [file/path file/source]} (view-files)]
      (is (= [] (embedded-messages source))
          (str path " embeds message values")))))

(deftest no-view-holds-a-sentence
  (testing "prose in a view is untranslatable by construction"
    (doseq [{:keys [file/path file/source]} (view-files)]
      (doseq [literal (literals source)
              :when (re-find sentence-pattern literal)]
        (is false (str path " holds prose: " (pr-str literal)))))))

(deftest the-check-would-notice
  (testing "the scan is sensitive to what it claims to detect"
    (is (re-find sentence-pattern "A collective of builders."))
    (is (not (re-find sentence-pattern "flex items-center gap-3 p-2 rounded-lg")))
    (is (not (re-find sentence-pattern "https://github.com/open-hax")))
    (is (= ["kept"] (literals "(defnc X \"dropped.\" [] \"kept\")")))))
