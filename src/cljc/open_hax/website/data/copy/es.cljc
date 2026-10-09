(ns open-hax.website.data.copy.es
  "Spanish message catalogue.

  Reviewed translation data, not machine output: this file is edited by a
  person and the build never translates. Key set and `{placeholder}` names are
  fixed by `open-hax.website.data.copy.en` and enforced by
  `open-hax.website.law.copy`.")

(def messages
  {:brand/name "OpenHax"
   :brand/promethean "Promethean"
   :link/github "GitHub"

   :meta/title "OpenHax — Arte, música y programas"
   :meta/description "El portafolio personal de OpenHax: sitios web, software de código abierto, arte generativo y música."
   :meta/noscript "Este sitio necesita JavaScript para mostrar sus galerías y los documentos publicados."

   :hero/tagline "Sitios web, arte, música y software útil"
   :hero/lede "Soy desarrollador y artista. OpenHax reúne mi trabajo: imágenes originales, experimentos creativos y el software que construyo."
   :hero/cta-work "Explora mi trabajo"
   :hero/cta-github "Contacta en GitHub"

   :products/heading "Proyectos de software"
   :products/lede "Herramientas de código abierto que creo y en las que colaboro. Explora el código, las ideas y las personas detrás de cada proyecto."
   :products/knoxx-description "Un entorno de ejecución de agentes con conversaciones, herramientas y políticas. Un proyecto independiente del ecosistema OpenHax."
   :products/proxx-description "Proxy de modelos, federación y gestión de credenciales temporales. Enruta las peticiones de IA con inteligencia."
   :products/openplanner-description "API de memoria, grafo y planificación. Conecta el conocimiento y orquesta flujos de trabajo complejos."

   :tag/agents "Agentes"
   :tag/policy "Políticas"
   :tag/runtime "Ejecución"
   :tag/ai "IA"
   :tag/proxy "Proxy"
   :tag/federation "Federación"
   :tag/graph "Grafo"
   :tag/memory "Memoria"
   :tag/planning "Planificación"

   :graphics/heading "Arte y gráficos"
   :graphics/lede "{count} obras visuales: estudios geométricos, patrones generativos e ilustraciones digitales."
   :hero/client-lede "¿Tienes en mente un sitio web para tu negocio? Creo sitios claros y con personalidad para dar un hogar a tu trabajo. Contacta conmigo para hablar de tu proyecto."
   :graphics/featured "Obras seleccionadas"
   :graphics/collection "Explora la colección"
   :graphics/search "Buscar obras"
   :graphics/all "Todas las colecciones"
   :graphics/more "Mostrar más obras"
   :graphics/results "Mostrando {shown} de {count} obras"
   :graphics/empty "Ninguna obra coincide con esta búsqueda."
   :graphics/open "Ver {title}"

   :graphics/close "Cerrar"

   :music/heading "Música"
   :music/lede "{count} canciones seleccionadas y publicadas en Suno."
   :music/listen "Escuchar aquí"
   :music/play "Escuchar {title}"
   :music/song-link "Abrir en Suno"
   :music/player-label "Reproductor de Suno para {title}"
   :music/profile "Más música de {name} en Suno"
   :music/local-heading "De la colección local"
   :music/local-lede "{count} canciones adicionales."

   :nav/products "Software"
   :nav/graphics "Arte"
   :nav/music "Música"
   :nav/language "Idioma"

   :footer/copyright "© {year} OpenHax. Todos los derechos reservados."

   :published/heading "Documentos publicados"
   :published/untitled "Documento sin título"
   :published/not-found-title "Aquí no hay nada publicado"
   :published/not-found-body "Esta dirección no contiene ningún documento publicado. Puede que se haya retirado o que el enlace sea incorrecto."
   :published/back "Volver al sitio"

   :manifest/error-title "El manifiesto de contenido publicado no es válido"
   :manifest/error-body "El sitio sigue mostrando sus propias secciones. Los documentos publicados no estarán disponibles hasta que se repare el manifiesto."})
