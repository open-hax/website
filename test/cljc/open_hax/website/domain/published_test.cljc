(ns open-hax.website.domain.published-test
  "Routing and switcher decisions over a decoded manifest."
  (:require [clojure.string :as str]
            [clojure.test :refer [deftest is testing]]
            [open-hax.website.domain.locale :as locale]
            [open-hax.website.domain.published :as published]
            [open-hax.website.law.published-manifest :as law]
            [open-hax.website.law.published-manifest-fixtures :as fixtures]))

(defn- decoded
  [id]
  (law/decode (fixtures/fixture-edn id)))

(deftest with-no-manifest-the-site-renders-its-own-sections
  (doseq [id [:absent :empty]]
    (let [location (published/resolve-location (decoded id) "/")]
      (is (= :site (:view/kind location)) (str id))
      (is (= :en (:locale/id location)) (str id))
      (is (= [] (published/listing (decoded id) :en)) (str id)))))

(deftest each-locale-root-is-a-site-view
  (doseq [loc locale/supported-locales]
    (let [location (published/resolve-location (decoded :absent) (locale/locale-root loc))]
      (is (= :site (:view/kind location)) (str loc))
      (is (= loc (:locale/id location)) (str loc)))))

(deftest one-document-in-two-locales-renders-at-both-routes
  (let [manifest (decoded :multi-locale)
        es (published/resolve-location manifest "/es/notes/hello")
        fr (published/resolve-location manifest "/fr/notes/hello")]
    (is (= :document (:view/kind es)))
    (is (= :es (:locale/id es)))
    (is (= "es" (locale/language-tag (:locale/id es))))
    (is (= "Hola" (:route/title (:view/route es))))

    (is (= :document (:view/kind fr)))
    (is (= :fr (:locale/id fr)))
    (is (= "fr" (locale/language-tag (:locale/id fr))))
    (is (= "Bonjour" (:route/title (:view/route fr))))))

(deftest a-cms-document-uses-its-published-locale-without-a-leading-locale-prefix
  (let [manifest (law/decode
                  {:manifest/version 1
                   :manifest/routes
                   (mapv (fn [loc]
                           {:route/path (str "/cms/org-one/demo/" (name loc))
                            :route/locale loc
                            :route/document "cms.org-one/doc-demo"
                            :route/artifact (str "artifacts/demo/" (name loc) "/rev-1.html")
                            :route/media-type "text/html"})
                         [:en :es])})
        location (published/resolve-location manifest "/cms/org-one/demo/es")
        options (published/switcher-options manifest location)]
    (is (= :document (:view/kind location)))
    (is (= :es (:locale/id location)))
    (is (= "es" (locale/language-tag (:locale/id location))))
    (is (= "/cms/org-one/demo/es" (:view/canonical-path location)))
    (is (false? (:locale/fallback? location)))
    (is (= [:en :es] (mapv :locale/id options)))
    (is (= ["/cms/org-one/demo/en" "/cms/org-one/demo/es"]
           (mapv :locale/href options)))
    (is (= [false true] (mapv :locale/current? options)))))

(deftest publication-locale-resolution-retains-prefixed-routes-and-default-fallback
  (let [manifest (law/decode
                  {:manifest/version 1
                   :manifest/routes
                   [{:route/path "/es/garden/demo"
                     :route/locale :es
                     :route/artifact "artifacts/garden/demo/es/rev-1.html"
                     :route/media-type "text/html"}
                    {:route/path "/garden/demo"
                     :route/locale :en
                     :route/artifact "artifacts/garden/demo/en/rev-1.html"
                     :route/media-type "text/html"}]})]
    (testing "the site's locale prefix still finds a published route"
      (let [location (published/resolve-location manifest "/es/garden/demo")]
        (is (= :document (:view/kind location)))
        (is (= :es (:locale/id location)))
        (is (false? (:locale/fallback? location)))
        (is (= "/es/garden/demo" (:view/canonical-path location)))))
    (testing "an unknown prefix still looks for the default locale's publication"
      (let [location (published/resolve-location manifest "/pt/garden/demo")]
        (is (= :document (:view/kind location)))
        (is (= :en (:locale/id location)))
        (is (true? (:locale/fallback? location)))
        (is (= "/garden/demo" (:view/canonical-path location)))))))

(deftest removing-a-document-from-the-manifest-stops-it-rendering
  (let [full (decoded :multi-locale)
        without-fr (law/decode
                    (update (fixtures/fixture-edn :multi-locale)
                            :manifest/routes
                            (fn [routes] (filterv #(not= :fr (:route/locale %)) routes))))]
    (is (= :document (:view/kind (published/resolve-location full "/fr/notes/hello"))))
    (is (= :not-found (:view/kind (published/resolve-location without-fr "/fr/notes/hello"))))
    (testing "and the locale stops being offered"
      (is (= [:es :fr] (published/published-locales full)))
      (is (= [:es] (published/published-locales without-fr))))))

(deftest an-unknown-locale-prefix-resolves-to-the-default-locale
  (let [manifest (decoded :single-locale)]
    (testing "the site's own page"
      (let [location (published/resolve-location manifest "/pt/")]
        (is (= :en (:locale/id location)))
        (is (true? (:locale/fallback? location)))
        (is (= :site (:view/kind location)))))
    (testing "a document path under an unsupported prefix looks for the default locale's route"
      (let [location (published/resolve-location manifest "/pt/notes/hello")]
        (is (= :en (:locale/id location)))
        (is (true? (:locale/fallback? location)))
        (is (= :not-found (:view/kind location)))))))

(deftest a-path-with-no-route-behind-it-is-not-found
  (let [manifest (decoded :single-locale)]
    (is (= :not-found (:view/kind (published/resolve-location manifest "/es/notes/missing"))))
    (is (= :not-found (:view/kind (published/resolve-location manifest "/nope"))))))

(deftest the-switcher-offers-only-published-locales-on-a-document
  (let [manifest (decoded :multi-locale)
        on-hello (published/switcher-options manifest (published/resolve-location manifest "/es/notes/hello"))
        on-solo (published/switcher-options manifest (published/resolve-location manifest "/es/notes/solo"))]
    (testing "a document translated into two locales offers exactly those two"
      (is (= [:es :fr] (mapv :locale/id on-hello)))
      (is (= ["/es/notes/hello" "/fr/notes/hello"] (mapv :locale/href on-hello))))
    (testing "a document in one locale offers only that locale"
      (is (= [:es] (mapv :locale/id on-solo))))
    (testing "the current locale is marked"
      (is (= [true false] (mapv :locale/current? on-hello))))))

(deftest the-switcher-offers-every-compiled-locale-on-the-site-itself
  (testing "TWO DISTINCT NOTIONS: the site's own copy always exists for all five"
    (let [manifest (decoded :absent)
          options (published/switcher-options manifest (published/resolve-location manifest "/"))]
      (is (= locale/supported-locales (mapv :locale/id options)))
      (is (= ["/" "/es/" "/fr/" "/de/" "/ja/"] (mapv :locale/href options)))
      (testing "even though no locale carries a published document"
        (is (= [] (published/published-locales manifest)))))))

(deftest artifact-urls-stay-inside-this-origin
  (let [manifest (decoded :single-locale)
        route (first (published/routes manifest))]
    (is (= "/published/artifacts/notes/hello/es/rev-7f3a91c.html"
           (published/artifact-url route)))
    (testing "the manifest url is relative too"
      (is (str/starts-with? published/manifest-url "/"))
      (is (law/relative-path? published/manifest-url)))
    (testing "no url this namespace can produce names another host"
      (doseq [url (into [published/manifest-url]
                        (keep published/artifact-url (published/routes (decoded :multi-locale))))]
        (is (str/starts-with? url (str published/content-root "/")) url)
        (is (not (str/includes? url "//")) url)))))

(deftest the-listing-renders-titles-and-falls-back-to-the-document-id
  (let [manifest (law/decode {:manifest/version 1
                              :manifest/routes
                              [{:route/path "/es/notes/titled"
                                :route/locale :es
                                :route/document "notes/titled"
                                :route/artifact "artifacts/a.html"
                                :route/media-type "text/html"
                                :route/title "Con título"}
                               {:route/path "/es/notes/untitled"
                                :route/locale :es
                                :route/document "notes/untitled"
                                :route/artifact "artifacts/b.html"
                                :route/media-type "text/html"}]})
        entries (published/listing manifest :es)]
    (is (= ["Con título" nil] (mapv :listing/title entries)))
    (is (= ["notes/titled" "notes/untitled"] (mapv :listing/document entries)))
    (testing "and a locale with nothing published lists nothing"
      (is (= [] (published/listing manifest :fr))))))

(deftest an-invalid-manifest-publishes-nothing-but-still-resolves-the-site
  (testing "a writer defect must not take the site's own pages down"
    (let [manifest (decoded :missing-required-field)]
      (is (not (law/valid? manifest)))
      (is (= [] (published/routes manifest)))
      (is (= :site (:view/kind (published/resolve-location manifest "/"))))
      (is (= :site (:view/kind (published/resolve-location manifest "/ja/")))))))
