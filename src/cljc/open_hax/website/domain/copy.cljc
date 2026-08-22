(ns open-hax.website.domain.copy
  "Message resolution: the only way a view obtains a user-visible string.

  Views take a locale and a message key. They never hold a literal, which is
  what makes `:website/copy-resolves-from-locale-dictionaries` checkable by
  reading the view namespaces rather than by trusting them.

  The fallback chain is locale -> default locale -> the key's own name. The
  last two steps are unreachable while `open-hax.website.law.copy` passes;
  they exist so that a dictionary defect degrades to visibly-wrong English
  instead of to `nil` inside a DOM node."
  (:require [open-hax.website.data.copy.de :as de]
            [open-hax.website.data.copy.en :as en]
            [open-hax.website.data.copy.es :as es]
            [open-hax.website.data.copy.fr :as fr]
            [open-hax.website.data.copy.ja :as ja]
            [open-hax.website.domain.locale :as locale]
            [open-hax.website.shape.template :as template]))

(def dictionaries
  "Every locale this build carries copy for. Compiled in, so always present —
  unlike published documents, which arrive as data at runtime."
  {:en en/messages
   :es es/messages
   :fr fr/messages
   :de de/messages
   :ja ja/messages})

(def reference-locale
  "The locale other locales are measured against, and the fallback."
  locale/default-locale)

(defn dictionary
  [loc]
  (or (get dictionaries loc)
      (get dictionaries reference-locale)))

(defn text
  "The literal message for `k` in `loc`."
  [loc k]
  (or (get (dictionary loc) k)
      (get (dictionary reference-locale) k)
      (subs (str k) 1)))

(defn text-with
  "The message for `k` in `loc` with its `{placeholder}`s filled from `params`.

  Interpolation happens inside the translated sentence, so each locale keeps
  its own word order — `(text-with :ja :music/lede {:count 12})` puts the
  Japanese counter after the numeral, which concatenation cannot do."
  [loc k params]
  (template/render (text loc k) params))
