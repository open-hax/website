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
   :meta/description "Portafolio de la organización OpenHax: arte, música y proyectos de software."
   :meta/noscript "Este sitio necesita JavaScript para mostrar sus galerías y los documentos publicados."

   :hero/tagline "Arte, música y programas"
   :hero/lede "Un colectivo de constructores, artistas y músicos que amplía los límites de la tecnología creativa."
   :hero/cta-work "Explora nuestro trabajo"
   :hero/cta-github "Ver en GitHub"

   :products/heading "Nuestros productos"
   :products/lede "Infraestructura y herramientas para la próxima generación de sistemas creativos e inteligentes."
   :products/knoxx-description "Backend, frontend y motor de políticas para agentes. El sistema nervioso central de nuestro ecosistema de agentes."
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

   :graphics/heading "Gráficos"
   :graphics/lede "{count} experimentos visuales, arte generativo y exploraciones creativas."
   :graphics/close "Cerrar"

   :music/heading "Música"
   :music/lede "{count} experimentos sonoros y composiciones."

   :nav/products "Productos"
   :nav/graphics "Gráficos"
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
