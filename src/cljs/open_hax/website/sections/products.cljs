(ns open-hax.website.sections.products
  "Product showcase section for Knoxx, Proxx, and OpenPlanner."
  (:require [helix.core :as hx :refer [$ defnc]]
            [helix.dom :as d]))

(defnc ProductCard [{:keys [title description tags href icon]}]
  (d/a {:href href :target "_blank"
        :class-name "group block p-6 rounded-xl border border-slate-800 bg-slate-900/50 hover:bg-slate-800/50 hover:border-slate-700 transition-all duration-300"}
       (d/div {:class-name "flex items-start justify-between mb-4"}
              (d/h3 {:class-name "text-xl font-bold text-white group-hover:text-slate-100 transition"}
                    title)
              (d/span {:class-name "text-2xl"} icon))
       (d/p {:class-name "text-slate-400 text-sm leading-relaxed mb-4"}
            description)
       (d/div {:class-name "flex flex-wrap gap-2"}
              (for [tag tags]
                (d/span {:key tag
                         :class-name "px-2 py-1 text-xs font-medium rounded-md bg-slate-800 text-slate-300 border border-slate-700"}
                        tag)))))

(defnc ProductsSection []
  (d/div {:class-name "max-w-7xl mx-auto"}
         (d/div {:class-name "text-center mb-16"}
                (d/h2 {:class-name "text-3xl sm:text-4xl font-bold text-white mb-4"}
                      "Our Products")
                (d/p {:class-name "text-slate-400 max-w-2xl mx-auto"}
                     "Infrastructure and tools for the next generation of creative and intelligent systems."))
         (d/div {:class-name "grid grid-cols-1 md:grid-cols-3 gap-6"}
                ($ ProductCard
                   {:title "Knoxx"
                    :description "Agent backend, frontend, and policy runtime. The central nervous system of our agent ecosystem."
                    :tags ["Agents" "Policy" "Runtime"]
                    :href "https://github.com/open-hax/openplanner/tree/main/packages/agents/knoxx"
                    :icon "🧠"})
                ($ ProductCard
                   {:title "Proxx"
                    :description "Model proxy, federation, and credential lease broker. Route AI requests with intelligence."
                    :tags ["AI" "Proxy" "Federation"]
                    :href "https://github.com/open-hax/proxx"
                    :icon "⚡"})
                ($ ProductCard
                   {:title "OpenPlanner"
                    :description "Memory, graph, and planning API. Connect knowledge and orchestrate complex workflows."
                    :tags ["Graph" "Memory" "Planning"]
                    :href "https://github.com/open-hax/openplanner"
                    :icon "🕸️"}))))
