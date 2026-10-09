(ns open-hax.website.domain.navigation
  "Pure destinations shared by the personal site's hero and navigation."
  (:require [open-hax.website.domain.locale :as locale]))

(def site-destinations
  {:products "#products"
   :graphics "#graphics"
   :music "#music"
   :personal-github "https://github.com/riatzukiza"
   :organization-github "https://github.com/open-hax"})

(defn destinations
  "Resolve the locale's home link alongside locale-independent destinations."
  [loc]
  (assoc site-destinations :home (locale/locale-root loc)))
