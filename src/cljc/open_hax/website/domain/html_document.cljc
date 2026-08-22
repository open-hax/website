(ns open-hax.website.domain.html-document
  "The per-locale HTML shell, as a pure function of the locale.

  There is one shell per locale rather than one shell for the site because two
  attributes cannot be deferred to JavaScript honestly: `lang`, which a screen
  reader and a search engine read before any script runs, and the localized
  `<title>`/`<meta description>`, which a crawler and a link preview read
  without running scripts at all. The shell the browser receives is therefore
  already in the right language; the app only fills in the body.

  Copy comes from the same dictionaries the views use, so the document head
  cannot drift out of translation — there is no second place to edit it.

  Pure: returns strings. `open-hax.website.build.html` writes them to disk."
  (:require [clojure.string :as str]
            [open-hax.website.domain.copy :as copy]
            [open-hax.website.domain.locale :as locale]))

(def stylesheet-url "/app.css")
(def script-url "/cljs/app.js")
(def favicon-url "/favicon.svg")

(defn escape-html
  "Escape text for an element body or a double-quoted attribute value."
  [s]
  (-> (str s)
      (str/replace "&" "&amp;")
      (str/replace "<" "&lt;")
      (str/replace ">" "&gt;")
      (str/replace "\"" "&quot;")))

(defn alternates
  "`hreflang` alternates for every locale this build carries, plus the
  `x-default` the contract puts at the root. Emitted on every page, including
  its own locale, which is what the spec asks for and what makes each page
  independently interpretable."
  []
  (conj (mapv (fn [loc]
                {:alternate/hreflang (locale/language-tag loc)
                 :alternate/href (locale/locale-root loc)})
              locale/supported-locales)
        {:alternate/hreflang "x-default"
         :alternate/href (locale/locale-root locale/default-locale)}))

(defn output-path
  "Path of the locale's shell inside the build output directory."
  [loc]
  (if (= loc locale/default-locale)
    "index.html"
    (str (name loc) "/index.html")))

(defn render
  "The complete HTML document for `loc`."
  [loc]
  (let [tag (locale/language-tag loc)
        title (copy/text loc :meta/title)
        description (copy/text loc :meta/description)
        noscript (copy/text loc :meta/noscript)]
    (str "<!doctype html>\n"
         "<html lang=\"" tag "\" class=\"dark\">\n"
         "  <head>\n"
         "    <meta charset=\"UTF-8\" />\n"
         "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />\n"
         "    <link rel=\"icon\" type=\"image/svg+xml\" href=\"" favicon-url "\" />\n"
         "    <title>" (escape-html title) "</title>\n"
         "    <meta name=\"description\" content=\"" (escape-html description) "\" />\n"
         "    <link rel=\"canonical\" href=\"" (locale/locale-root loc) "\" />\n"
         (str/join (for [{:keys [alternate/hreflang alternate/href]} (alternates)]
                     (str "    <link rel=\"alternate\" hreflang=\"" hreflang
                          "\" href=\"" href "\" />\n")))
         "    <link rel=\"stylesheet\" href=\"" stylesheet-url "\" />\n"
         "  </head>\n"
         "  <body>\n"
         "    <div id=\"root\"></div>\n"
         "    <noscript>" (escape-html noscript) "</noscript>\n"
         "    <script src=\"" script-url "\"></script>\n"
         "  </body>\n"
         "</html>\n")))

(defn pages
  "Every shell this build emits: one per locale, the default at the root."
  []
  (mapv (fn [loc]
          {:page/locale loc
           :page/language-tag (locale/language-tag loc)
           :page/output-path (output-path loc)
           :page/html (render loc)})
        locale/supported-locales))
