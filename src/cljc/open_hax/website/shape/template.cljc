(ns open-hax.website.shape.template
  "Structure-only interpolation of `{placeholder}` templates.

  Pure, portable, and deliberately ignorant of locales, messages and the DOM:
  it knows only that a string may carry named holes and that a map may fill
  them. It exists because three of this site's sentences embed a number:

      graphics lede   {count} visual experiments, generative art, ...
      music lede      {count} sonic experiments and compositions.
      footer          (c) {year} OpenHax. All rights reserved.

  Concatenating a translated fragment onto a number cannot express those
  sentences in every locale — Japanese needs a counter word directly after the
  numeral and puts the clause in a different order, and German reorders the
  noun phrase. A template keeps word order inside the translated string, which
  is where a translator can see and fix it."
  (:require [clojure.string :as str]))

(def placeholder-pattern
  "A placeholder is a lowercase, hyphenated name in braces: `{count}`."
  #"\{([a-z][a-z0-9-]*)\}")

(defn placeholders
  "The set of placeholder names (as keywords) `template` refers to."
  [template]
  (into #{}
        (map (comp keyword second))
        (re-seq placeholder-pattern (str template))))

(defn render
  "`template` with every `{name}` replaced by `(get params (keyword name))`.

  An unsupplied placeholder is left standing rather than rendered as `null` or
  an empty gap: a visible `{count}` is a bug report, an invisible one is a lie.
  `open-hax.website.law.copy` makes that case unreachable by asserting that
  every locale uses exactly the placeholders the source locale uses."
  [template params]
  (str/replace (str template)
               placeholder-pattern
               (fn [[whole name]]
                 (let [v (get params (keyword name) ::missing)]
                   (if (= ::missing v)
                     whole
                     (str v))))))
