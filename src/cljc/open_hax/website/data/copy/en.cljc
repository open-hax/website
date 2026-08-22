(ns open-hax.website.data.copy.en
  "English message catalogue — the SOURCE locale.

  This dictionary is the reference every other locale is measured against:
  `open-hax.website.law.copy` requires each locale to define exactly this key
  set, with exactly these `{placeholder}` names per key. Adding a key here
  makes the other four dictionaries fail their law until they are translated,
  which is the intended order of events.

  Inert data: a dictionary namespace requires nothing and decides nothing.")

(def messages
  {;; Proper nouns. Identical in every locale by law — see
   ;; `open-hax.website.law.copy/verbatim-keys`.
   :brand/name "OpenHax"
   :brand/promethean "Promethean"
   :link/github "GitHub"

   ;; Document shell. Rendered into <title>/<meta> by the static build, per locale.
   :meta/title "OpenHax — Art, Music & Programs"
   :meta/description "OpenHax organization portfolio showcasing art, music, and software projects."
   :meta/noscript "This site needs JavaScript to show its galleries and any published documents."

   :hero/tagline "Art, Music, and Programs"
   :hero/lede "A collective of builders, artists, and musicians pushing the boundaries of creative technology."
   :hero/cta-work "Explore Our Work"
   :hero/cta-github "View on GitHub"

   :products/heading "Our Products"
   :products/lede "Infrastructure and tools for the next generation of creative and intelligent systems."
   :products/knoxx-description "Agent backend, frontend, and policy runtime. The central nervous system of our agent ecosystem."
   :products/proxx-description "Model proxy, federation, and credential lease broker. Route AI requests with intelligence."
   :products/openplanner-description "Memory, graph, and planning API. Connect knowledge and orchestrate complex workflows."

   :tag/agents "Agents"
   :tag/policy "Policy"
   :tag/runtime "Runtime"
   :tag/ai "AI"
   :tag/proxy "Proxy"
   :tag/federation "Federation"
   :tag/graph "Graph"
   :tag/memory "Memory"
   :tag/planning "Planning"

   :graphics/heading "Graphics"
   :graphics/lede "{count} visual experiments, generative art, and creative explorations."
   :graphics/close "Close"

   :music/heading "Music"
   :music/lede "{count} sonic experiments and compositions."

   :nav/products "Products"
   :nav/graphics "Graphics"
   :nav/music "Music"
   :nav/language "Language"

   :footer/copyright "© {year} OpenHax. All rights reserved."

   :published/heading "Published documents"
   :published/untitled "Untitled document"
   :published/not-found-title "Nothing published here"
   :published/not-found-body "This address carries no published document. It may have been unpublished, or the link may be wrong."
   :published/back "Back to the site"

   :manifest/error-title "The published content manifest is invalid"
   :manifest/error-body "The site is serving its own sections. Published documents stay unavailable until the manifest is repaired."})
