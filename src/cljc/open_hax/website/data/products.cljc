(ns open-hax.website.data.products
  "The product cards, as data.

  `:product/name` is a proper noun and is NOT a message key: Knoxx, Proxx and
  OpenPlanner are the same word in every locale, so putting them in the
  dictionaries would invite five chances to translate a product name. Their
  descriptions and tags ARE copy, and appear here only as keys into the
  per-locale dictionaries.

  Inert data: a data namespace requires nothing and decides nothing.")

(def products
  [{:product/id :knoxx
    :product/name "Knoxx"
    :product/icon "🧠"
    :product/href "https://github.com/open-hax/openplanner/tree/main/packages/agents/knoxx"
    :product/description-key :products/knoxx-description
    :product/tag-keys [:tag/agents :tag/policy :tag/runtime]}
   {:product/id :proxx
    :product/name "Proxx"
    :product/icon "⚡"
    :product/href "https://github.com/open-hax/proxx"
    :product/description-key :products/proxx-description
    :product/tag-keys [:tag/ai :tag/proxy :tag/federation]}
   {:product/id :openplanner
    :product/name "OpenPlanner"
    :product/icon "🕸️"
    :product/href "https://github.com/open-hax/openplanner"
    :product/description-key :products/openplanner-description
    :product/tag-keys [:tag/graph :tag/memory :tag/planning]}])
