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
   :meta/description "The personal OpenHax portfolio: websites, open-source software, generative art, and music."
   :meta/noscript "This site needs JavaScript to show its galleries and any published documents."

   :hero/tagline "Websites, art, music & useful software"
   :hero/lede "I'm a developer and artist. OpenHax is the home for my work: original visuals, creative experiments, and the software I build."
   :hero/cta-work "Explore my work"
   :hero/cta-github "Connect on GitHub"

   :products/heading "Software projects"
   :products/lede "Open-source tools I build and work on. Explore the code, ideas, and people behind each project."
   :products/knoxx-description "An agent runtime with conversations, tools, and policy. A separate project in the OpenHax ecosystem."
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

   :graphics/heading "Art & graphics"
   :graphics/lede "{count} visual works, from geometric studies and generative patterns to digital illustrations."
   :hero/client-lede "Have a business website in mind? I build clear, distinctive sites that give your work a home. Connect with me to discuss your project."
   :graphics/featured "Selected work"
   :graphics/collection "Browse the collection"
   :graphics/search "Search artwork"
   :graphics/all "All collections"
   :graphics/more "Show more work"
   :graphics/results "Showing {shown} of {count} works"
   :graphics/empty "No artwork matches this search."
   :graphics/open "View {title}"

   :graphics/close "Close"

   :music/heading "Music"
   :music/lede "{count} selected songs published on Suno."
   :music/listen "Listen here"
   :music/play "Listen to {title}"
   :music/song-link "Open in Suno"
   :music/player-label "Suno player for {title}"
   :music/profile "More music from {name} on Suno"
   :music/local-heading "From the local collection"
   :music/local-lede "{count} additional tracks."

   :nav/products "Software"
   :nav/graphics "Art"
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
