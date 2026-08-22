(ns open-hax.website.data.copy.fr
  "French message catalogue.

  Reviewed translation data, not machine output: this file is edited by a
  person and the build never translates. Key set and `{placeholder}` names are
  fixed by `open-hax.website.data.copy.en` and enforced by
  `open-hax.website.law.copy`.")

(def messages
  {:brand/name "OpenHax"
   :brand/promethean "Promethean"
   :link/github "GitHub"

   :meta/title "OpenHax — Art, musique et programmes"
   :meta/description "Portfolio de l'organisation OpenHax : art, musique et projets logiciels."
   :meta/noscript "Ce site a besoin de JavaScript pour afficher ses galeries et les documents publiés."

   :hero/tagline "Art, musique et programmes"
   :hero/lede "Un collectif de bâtisseurs, d'artistes et de musiciens qui repoussent les limites de la technologie créative."
   :hero/cta-work "Découvrir nos travaux"
   :hero/cta-github "Voir sur GitHub"

   :products/heading "Nos produits"
   :products/lede "Infrastructure et outils pour la prochaine génération de systèmes créatifs et intelligents."
   :products/knoxx-description "Backend, frontend et moteur de règles pour agents. Le système nerveux central de notre écosystème d'agents."
   :products/proxx-description "Proxy de modèles, fédération et courtier de baux d'identifiants. Acheminez les requêtes d'IA avec intelligence."
   :products/openplanner-description "API de mémoire, de graphe et de planification. Reliez les connaissances et orchestrez des flux de travail complexes."

   :tag/agents "Agents"
   :tag/policy "Règles"
   :tag/runtime "Exécution"
   :tag/ai "IA"
   :tag/proxy "Proxy"
   :tag/federation "Fédération"
   :tag/graph "Graphe"
   :tag/memory "Mémoire"
   :tag/planning "Planification"

   :graphics/heading "Graphismes"
   :graphics/lede "{count} expériences visuelles, œuvres génératives et explorations créatives."
   :graphics/close "Fermer"

   :music/heading "Musique"
   :music/lede "{count} expériences sonores et compositions."

   :nav/products "Produits"
   :nav/graphics "Graphismes"
   :nav/music "Musique"
   :nav/language "Langue"

   :footer/copyright "© {year} OpenHax. Tous droits réservés."

   :published/heading "Documents publiés"
   :published/untitled "Document sans titre"
   :published/not-found-title "Rien de publié ici"
   :published/not-found-body "Cette adresse ne porte aucun document publié. Il a peut-être été dépublié, ou le lien est erroné."
   :published/back "Retour au site"

   :manifest/error-title "Le manifeste de contenu publié est invalide"
   :manifest/error-body "Le site continue d'afficher ses propres sections. Les documents publiés restent indisponibles jusqu'à la réparation du manifeste."})
