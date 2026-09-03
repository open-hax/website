(ns open-hax.website.law.copy
  "The laws every per-locale message catalogue obeys. Pure, no I/O.

  A translated site fails quietly. A missing key renders as a fallback nobody
  notices; a dropped `{count}` renders a sentence that reads fine and states
  nothing; a translated product name looks like a different product. None of
  those raise an exception, so they must be asserted instead:

  1. COMPLETENESS — every locale defines exactly the source locale's key set.
     A missing key and an extra key are both failures; an extra key is dead
     copy that will be edited forever by someone who thinks it ships.
  2. PLACEHOLDERS — for every key, each locale uses exactly the placeholder
     names the source uses. Word order may differ, and must; the holes may not.
  3. VERBATIM — proper nouns are byte-identical across locales.
  4. NON-BLANK — no value is blank. A blank string is the one 'translation'
     that passes completeness while saying nothing.

  `verify` returns problems as data so a test can name them; nothing here
  throws, because the caller is a test and a report is more useful than a
  stack trace."
  (:require [clojure.string :as str]
            [open-hax.website.shape.template :as template]))

(def verbatim-keys
  "Message keys whose value is a proper noun, not copy. `Knoxx`, `Proxx` and
  `OpenPlanner` are absent because product names never enter a dictionary at
  all — they live in `open-hax.website.data.products` as untranslated data."
  #{:brand/name :brand/promethean :link/github})

(defn key-set
  [dictionary]
  (set (keys dictionary)))

(defn key-diff
  "`{:missing #{}, :extra #{}}` for `dictionary` against `reference`."
  [dictionary reference]
  {:missing (into (sorted-set) (remove (partial contains? dictionary) (keys reference)))
   :extra (into (sorted-set) (remove (partial contains? reference) (keys dictionary)))})

(defn placeholder-diff
  "Keys whose placeholder set differs from `reference`, with both sets."
  [dictionary reference]
  (into []
        (comp (filter (fn [[k v]]
                        (and (contains? dictionary k)
                             (not= (template/placeholders v)
                                   (template/placeholders (get dictionary k))))))
              (map (fn [[k v]]
                     {:message/key k
                      :expected (template/placeholders v)
                      :actual (template/placeholders (get dictionary k))})))
        (sort-by key reference)))

(defn- problem
  [kind locale extra]
  (merge {:problem/kind kind :problem/locale locale} extra))

(defn verify-locale
  "Problems for one locale's `dictionary` against `reference`."
  [locale dictionary reference]
  (let [{:keys [missing extra]} (key-diff dictionary reference)]
    (concat
     (when (seq missing)
       [(problem :copy.problem/missing-keys locale {:problem/keys missing})])
     (when (seq extra)
       [(problem :copy.problem/extra-keys locale {:problem/keys extra})])
     (for [{mismatched-key :message/key :keys [expected actual]} (placeholder-diff dictionary reference)]
       (problem :copy.problem/placeholder-mismatch locale
                {:problem/key mismatched-key
                 :problem/expected expected
                 :problem/actual actual}))
     (for [[k v] (sort-by key dictionary)
           :when (or (not (string? v)) (str/blank? v))]
       (problem :copy.problem/blank-value locale {:problem/key k :problem/value v}))
     (for [k (sort verbatim-keys)
           :when (and (contains? dictionary k)
                      (contains? reference k)
                      (not= (get dictionary k) (get reference k)))]
       (problem :copy.problem/verbatim-differs locale
                {:problem/key k
                 :problem/expected (get reference k)
                 :problem/actual (get dictionary k)})))))

(defn verify
  "Verify every locale in `dictionaries` against the `reference-locale` entry.

  Returns `{:law/ok? bool, :law/problems [...], :law/locales [...]}`."
  [dictionaries reference-locale]
  (let [reference (get dictionaries reference-locale)
        problems (vec (mapcat (fn [[locale dictionary]]
                                (verify-locale locale dictionary reference))
                              (sort-by (comp name key) dictionaries)))]
    {:law/ok? (empty? problems)
     :law/problems problems
     :law/locales (vec (sort (keys dictionaries)))}))
