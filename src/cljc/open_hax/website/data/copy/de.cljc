(ns open-hax.website.data.copy.de
  "German message catalogue.

  Translation data requires human review before publication. See
  `docs/site-copy-review.md` for the current approval status. The build never
  translates. Key set and `{placeholder}` names are
  fixed by `open-hax.website.data.copy.en` and enforced by
  `open-hax.website.law.copy`.")

(def messages
  {:brand/name "OpenHax"
   :brand/promethean "Promethean"
   :link/github "GitHub"

   :meta/title "OpenHax — Kunst, Musik und Programme"
   :meta/description "Das persönliche OpenHax-Portfolio: Websites, freie Software, generative Kunst und Musik."
   :meta/noscript "Diese Website benötigt JavaScript, um ihre Galerien und veröffentlichte Dokumente anzuzeigen."

   :hero/tagline "Websites, Kunst, Musik und nützliche Software"
   :hero/lede "Ich bin Entwickler und Künstler. OpenHax vereint meine Arbeit: eigene Bilder, kreative Experimente und die Software, die ich entwickle."
   :hero/cta-work "Meine Arbeit entdecken"
   :hero/cta-github "Kontakt auf GitHub"

   :products/heading "Softwareprojekte"
   :products/lede "Freie Werkzeuge, die ich entwickle oder mitgestalte. Entdecken Sie den Code, die Ideen und die Menschen hinter jedem Projekt."
   :products/knoxx-description "Eine Agentenlaufzeit mit Gesprächen, Werkzeugen und Regeln. Ein eigenständiges Projekt im OpenHax-Ökosystem."
   :products/proxx-description "Modell-Proxy, Föderation und Broker für Zugangsdaten-Leases. Leitet KI-Anfragen intelligent weiter."
   :products/openplanner-description "API für Speicher, Graph und Planung. Verbindet Wissen und orchestriert komplexe Arbeitsabläufe."

   :tag/agents "Agenten"
   :tag/policy "Richtlinien"
   :tag/runtime "Laufzeit"
   :tag/ai "KI"
   :tag/proxy "Proxy"
   :tag/federation "Föderation"
   :tag/graph "Graph"
   :tag/memory "Speicher"
   :tag/planning "Planung"

   :graphics/heading "Kunst und Grafik"
   :graphics/lede "{count} visuelle Arbeiten: geometrische Studien, generative Muster und digitale Illustrationen."
   :hero/client-lede "Eine Website für Ihr Unternehmen? Ich entwickle klare, eigenständige Websites, die Ihrer Arbeit ein Zuhause geben. Kontaktieren Sie mich, um Ihr Projekt zu besprechen."
   :graphics/featured "Ausgewählte Arbeiten"
   :graphics/collection "Sammlung erkunden"
   :graphics/search "Kunstwerke suchen"
   :graphics/all "Alle Sammlungen"
   :graphics/more "Weitere Arbeiten zeigen"
   :graphics/results "{shown} von {count} Arbeiten angezeigt"
   :graphics/empty "Keine Arbeit entspricht dieser Suche."
   :graphics/open "{title} ansehen"

   :graphics/close "Schließen"

   :music/heading "Musik"
   :music/lede "{count} ausgewählte, auf Suno veröffentlichte Songs."
   :music/listen "Hier anhören"
   :music/play "{title} anhören"
   :music/song-link "Auf Suno öffnen"
   :music/player-label "Suno-Player für {title}"
   :music/profile "Mehr Musik von {name} auf Suno"
   :music/local-heading "Aus der lokalen Sammlung"
   :music/local-lede "{count} weitere Titel."

   :nav/products "Software"
   :nav/graphics "Kunst"
   :nav/music "Musik"
   :nav/language "Sprache"

   :footer/copyright "© {year} OpenHax. Alle Rechte vorbehalten."

   :published/heading "Veröffentlichte Dokumente"
   :published/untitled "Dokument ohne Titel"
   :published/not-found-title "Hier ist nichts veröffentlicht"
   :published/not-found-body "Unter dieser Adresse liegt kein veröffentlichtes Dokument. Möglicherweise wurde es zurückgezogen, oder der Link ist falsch."
   :published/back "Zurück zur Website"

   :manifest/error-title "Das Manifest der veröffentlichten Inhalte ist ungültig"
   :manifest/error-body "Die Website zeigt weiterhin ihre eigenen Abschnitte. Veröffentlichte Dokumente bleiben nicht verfügbar, bis das Manifest repariert ist."})
