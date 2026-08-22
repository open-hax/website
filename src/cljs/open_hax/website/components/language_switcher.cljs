(ns open-hax.website.components.language-switcher
  "The language switcher.

  It renders exactly the options it is handed and computes nothing: which
  locales may be offered is a decision, it differs between the site's own
  pages and a published document, and it therefore belongs in
  `open-hax.website.domain.published/switcher-options` where it is tested.

  Each option is a real link to a real path, so switching language is a
  navigation to that locale's own document — which is how the browser gets a
  shell whose `lang` and `<title>` are already correct."
  (:require [helix.core :refer [defnc]]
            [helix.dom :as d]
            [open-hax.website.domain.copy :as copy]))

(defnc LanguageSwitcher [{:keys [locale options]}]
  (when (seq options)
    (d/nav {:class-name "flex items-center gap-2"
            :aria-label (copy/text locale :nav/language)}
           (for [{:keys [locale/id locale/label locale/tag locale/href locale/current?]} options]
             (d/a {:key (str id)
                   :href href
                   :hrefLang tag
                   :lang tag
                   :aria-current (when current? "true")
                   :class-name (str "text-xs px-2 py-1 rounded border transition "
                                    (if current?
                                      "border-slate-600 text-white"
                                      "border-transparent text-slate-400 hover:text-slate-200"))}
                  label)))))
