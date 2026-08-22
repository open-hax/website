(ns open-hax.website.sections.music
  "Music showcase section.

  As with the graphics gallery, an empty track list is a valid build rather
  than a failure — the count sentence reads `0` and nothing is referenced that
  was not staged."
  (:require [helix.core :refer [$ defnc]]
            [helix.dom :as d]
            [helix.hooks :as hooks]
            [open-hax.website.data.assets :as assets]
            [open-hax.website.domain.copy :as copy]))

(defn folder-badge
  [folder]
  (when (seq folder)
    (d/span {:class-name "px-2 py-0.5 text-xs rounded bg-slate-800 text-slate-400 border border-slate-700 truncate max-w-[120px]"}
            folder)))

(defnc TrackRow [{:keys [track index on-play]}]
  (d/div {:class-name "flex items-center gap-3 p-2 rounded-lg hover:bg-slate-800/50 transition group cursor-pointer"
          :on-click #(on-play track)}
         (d/span {:class-name "text-slate-600 text-sm w-5 text-center font-mono"}
                 (str (inc index)))
         (d/div {:class-name "h-9 w-9 rounded bg-slate-800 flex items-center justify-center group-hover:bg-slate-700 transition shrink-0"}
                (d/span {:class-name "text-sm" :aria-hidden true} "▶"))
         (d/div {:class-name "flex-1 min-w-0"}
                (d/h4 {:class-name "text-white font-medium truncate text-sm"} (:title track))
                (d/p {:class-name "text-slate-500 text-xs truncate"} (:folder track)))
         (folder-badge (:folder track))
         (d/span {:class-name "text-slate-500 text-xs font-mono shrink-0"}
                 (:duration track))))

(defnc MusicSection [{:keys [locale]}]
  (let [[playing set-playing] (hooks/use-state nil)]
    (d/div {:class-name "max-w-4xl mx-auto"}
           (d/div {:class-name "text-center mb-16"}
                  (d/h2 {:class-name "text-3xl sm:text-4xl font-bold text-white mb-4"}
                        (copy/text locale :music/heading))
                  (d/p {:class-name "text-slate-400 max-w-2xl mx-auto"}
                       (copy/text-with locale :music/lede
                                       {:count (count assets/music)})))
           (d/div {:class-name "space-y-0.5 max-h-[600px] overflow-y-auto pr-2"
                   :style {:scrollbar-width "thin"
                           :scrollbar-color "#334155 transparent"}}
                  (for [[idx track] (map-indexed vector assets/music)]
                    ($ TrackRow {:key (:id track)
                                 :track track
                                 :index idx
                                 :on-play set-playing})))
           (when playing
             (d/div {:class-name "mt-6 p-4 rounded-lg bg-slate-800/50 border border-slate-700"}
                    (d/p {:class-name "text-white font-medium"} (:title playing))
                    (d/p {:class-name "text-slate-400 text-sm"} (:folder playing))
                    (d/audio {:src (:src playing)
                              :controls true
                              :class-name "w-full mt-2"
                              :auto-play true}))))))
