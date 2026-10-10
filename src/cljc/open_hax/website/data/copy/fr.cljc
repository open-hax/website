(ns open-hax.website.data.copy.fr
  "French message catalogue.

  Translation data requires human review before publication. See
  `docs/site-copy-review.md` for the current approval status. The build never
  translates. Key set and `{placeholder}` names are
  fixed by `open-hax.website.data.copy.en` and enforced by
  `open-hax.website.law.copy`.")

(def messages
  {:brand/name "OpenHax"
   :brand/promethean "Promethean"
   :link/github "GitHub"

   :meta/title "OpenHax — Art, musique et programmes"
   :meta/description "Le portfolio personnel OpenHax : sites web, logiciels libres, art génératif et musique."
   :meta/noscript "Ce site a besoin de JavaScript pour afficher ses galeries et les documents publiés."

   :hero/tagline "Sites web, art, musique et logiciels utiles"
   :hero/lede "Je suis développeur et artiste. OpenHax réunit mon travail : créations visuelles originales, expériences créatives et logiciels que je développe."
   :hero/cta-work "Explorer mon travail"
   :hero/cta-github "Me contacter sur GitHub"

   :products/heading "Projets logiciels"
   :products/lede "Des outils libres que je développe ou auxquels je contribue. Découvrez le code, les idées et les personnes derrière chaque projet."
   :products/knoxx-description "Un environnement d'agents avec conversations, outils et règles. Un projet distinct au sein de l'écosystème OpenHax."
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

   :graphics/heading "Art et graphisme"
   :graphics/lede "{count} œuvres visuelles : études géométriques, motifs génératifs et illustrations numériques."
   :hero/client-lede "Un site web pour votre entreprise ? Je crée des sites clairs et singuliers qui donnent une place à votre travail. Contactez-moi pour parler de votre projet."
   :graphics/featured "Œuvres choisies"
   :graphics/collection "Parcourir la collection"
   :graphics/search "Rechercher une œuvre"
   :graphics/all "Toutes les collections"
   :graphics/more "Voir plus d'œuvres"
   :graphics/results "{shown} œuvres affichées sur {count}"
   :graphics/empty "Aucune œuvre ne correspond à cette recherche."
   :graphics/open "Voir {title}"

   :graphics/close "Fermer"

   :music/heading "Musique"
   :music/lede "{count} chansons sélectionnées et publiées sur Suno."
   :music/listen "Écouter ici"
   :music/play "Écouter {title}"
   :music/song-link "Ouvrir sur Suno"
   :music/player-label "Lecteur Suno pour {title}"
   :music/profile "Plus de musique de {name} sur Suno"
   :music/local-heading "De la collection locale"
   :music/local-lede "{count} morceaux supplémentaires."

   :nav/products "Logiciels"
   :nav/graphics "Art"
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
