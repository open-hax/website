(ns open-hax.website.render-test
  "Render the real components and read the real markup.

  `(fn? HeroSection)` proves a var exists. It does not prove that the hero says
  anything, in any language, and it would not have noticed the whole site
  rendering English at `/ja/`. These tests put the components through
  `react-dom/server` and assert on the HTML that comes out.

  Only components that render without a DOM are covered here — no `window`, no
  `document`, no effects, which React does not run when server-rendering. The
  shell itself is covered by `open-hax.website.domain.html-document-test`."
  (:require ["react-dom/server" :as rdom-server]
            [cljs.test :refer [deftest is testing]]
            [clojure.string :as str]
            [helix.core :refer [$]]
            [open-hax.website.components.footer :as footer]
            [open-hax.website.components.language-switcher :as switcher]
            [open-hax.website.components.manifest-error :as manifest-error]
            [open-hax.website.domain.copy :as copy]
            [open-hax.website.domain.locale :as locale]
            [open-hax.website.domain.published :as published]
            [open-hax.website.law.published-manifest :as law]
            [open-hax.website.law.published-manifest-fixtures :as fixtures]
            [open-hax.website.sections.hero :as hero]
            [open-hax.website.sections.products :as products]
            [open-hax.website.sections.published :as published-views]))

(defn- render
  [element]
  (.renderToStaticMarkup rdom-server element))

(defn- text
  "A message as React writes it into markup.

  React escapes `'` to `&#x27;`, so a French sentence carrying an apostrophe is
  present in the DOM and absent from a naive substring search. Escaping the
  expectation rather than unescaping the markup keeps the assertion honest
  about what the browser receives."
  [loc k]
  (-> (copy/text loc k)
      (str/replace "&" "&amp;")
      (str/replace "<" "&lt;")
      (str/replace ">" "&gt;")
      (str/replace "\"" "&quot;")
      (str/replace "'" "&#x27;")))

(deftest the-hero-renders-every-locale-in-that-locale
  (doseq [loc locale/supported-locales]
    (let [markup (render ($ hero/HeroSection {:locale loc}))]
      (is (str/includes? markup "OpenHax") (str loc " brand"))
      (doseq [k [:hero/tagline :hero/lede :hero/cta-work :hero/cta-github]]
        (is (str/includes? markup (text loc k))
            (str loc " " k))))))

(deftest the-hero-in-one-locale-is-not-the-hero-in-another
  (testing "the failure mode a fn? assertion cannot see"
    (let [markup (mapv #(render ($ hero/HeroSection {:locale %})) locale/supported-locales)]
      (is (= (count markup) (count (set markup)))
          "five locales produced fewer than five distinct renderings"))))

(deftest the-product-cards-translate-descriptions-and-tags-but-not-names
  (doseq [loc locale/supported-locales]
    (let [markup (render ($ products/ProductsSection {:locale loc}))]
      (testing "product names are proper nouns in every locale"
        (doseq [product-name ["Knoxx" "Proxx" "OpenPlanner"]]
          (is (str/includes? markup product-name) (str loc " " product-name))))
      (testing "descriptions and tags are translated"
        (doseq [k [:products/heading :products/lede
                   :products/knoxx-description :products/proxx-description
                   :products/openplanner-description
                   :tag/agents :tag/policy :tag/runtime
                   :tag/ai :tag/proxy :tag/federation
                   :tag/graph :tag/memory :tag/planning]]
          (is (str/includes? markup (text loc k)) (str loc " " k)))))))

(deftest the-footer-interpolates-the-year-inside-the-translated-sentence
  (doseq [loc locale/supported-locales]
    (let [markup (render ($ footer/Footer {:locale loc :year 2026}))]
      (is (str/includes? markup "2026") (str loc " year"))
      (is (not (str/includes? markup "{year}")) (str loc " placeholder left standing"))
      (is (str/includes? markup (str/replace (copy/text-with loc :footer/copyright {:year 2026})
                                            "'" "&#x27;"))
          (str loc " copyright sentence")))))

(deftest the-switcher-renders-one-link-per-offered-locale
  (let [manifest (law/decode (fixtures/fixture-edn :absent))
        location (published/resolve-location manifest "/")
        options (published/switcher-options manifest location)
        markup (render ($ switcher/LanguageSwitcher {:locale :en :options options}))]
    (testing "on the site's own pages every compiled locale is offered"
      (doseq [loc locale/supported-locales]
        (is (str/includes? markup (locale/endonym loc)) (str loc " endonym"))
        (is (str/includes? markup (str "href=\"" (locale/locale-root loc) "\""))
            (str loc " href"))))
    (testing "and the current one is marked"
      (is (str/includes? markup "aria-current=\"true\"")))))

(deftest the-switcher-on-a-document-offers-only-published-locales
  (let [manifest (law/decode (fixtures/fixture-edn :multi-locale))
        location (published/resolve-location manifest "/es/notes/hello")
        options (published/switcher-options manifest location)
        markup (render ($ switcher/LanguageSwitcher {:locale :es :options options}))]
    (is (str/includes? markup "href=\"/es/notes/hello\""))
    (is (str/includes? markup "href=\"/fr/notes/hello\""))
    (testing "a locale with no published translation of this document is absent"
      (is (not (str/includes? markup "Deutsch")))
      (is (not (str/includes? markup "日本語")))
      (is (not (str/includes? markup "English"))))))

(deftest an-empty-manifest-renders-no-published-listing-at-all
  (doseq [id [:absent :empty]]
    (let [manifest (law/decode (fixtures/fixture-edn id))
          markup (render ($ published-views/PublishedListing {:locale :en :manifest manifest}))]
      (is (= "" markup) (str id " rendered something")))))

(deftest a-manifest-with-routes-renders-their-titles
  (let [manifest (law/decode (fixtures/fixture-edn :multi-locale))
        es (render ($ published-views/PublishedListing {:locale :es :manifest manifest}))
        fr (render ($ published-views/PublishedListing {:locale :fr :manifest manifest}))
        de (render ($ published-views/PublishedListing {:locale :de :manifest manifest}))]
    (is (str/includes? es "Hola"))
    (is (str/includes? es "Solo"))
    (is (str/includes? es (text :es :published/heading)))
    (is (str/includes? fr "Bonjour"))
    (is (not (str/includes? fr "Hola")))
    (testing "a locale the manifest does not carry lists nothing"
      (is (= "" de)))))

(deftest the-not-found-view-says-so-in-the-right-language
  (doseq [loc locale/supported-locales]
    (let [markup (render ($ published-views/PublishedNotFound {:locale loc}))]
      (is (str/includes? markup (text loc :published/not-found-title)) (str loc))
      (is (str/includes? markup (text loc :published/not-found-body)) (str loc))
      (is (str/includes? markup (str "href=\"" (locale/locale-root loc) "\"")) (str loc)))))

(deftest an-invalid-manifest-renders-a-loud-alert-not-a-blank-page
  (let [decoded (law/decode (fixtures/fixture-edn :missing-required-field))
        markup (render ($ manifest-error/ManifestErrorBanner
                          {:locale :en :errors (:published/errors decoded)}))]
    (is (str/includes? markup "role=\"alert\""))
    (is (str/includes? markup (text :en :manifest/error-title)))
    (testing "and names the offending field, for whoever is about to fix the writer"
      (is (str/includes? markup "missing-required-field"))
      (is (str/includes? markup "route/media-type")))))
